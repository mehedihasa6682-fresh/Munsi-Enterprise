package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.dao.OrderDao
import com.example.data.local.dao.ProductDao
import com.example.data.local.dao.RetailerDao
import com.example.data.local.dao.SrLocationDao
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.OrderItemEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.RetailerEntity
import com.example.data.local.entities.SrLocationEntity
import com.example.util.NotificationHelper
import com.example.util.PdfInvoiceGenerator
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.util.UUID

data class CartItem(
    val product: ProductEntity,
    var quantity: Int
)

class OrderRepository(
    private val context: Context,
    private val productDao: ProductDao,
    private val retailerDao: RetailerDao,
    private val orderDao: OrderDao,
    private val srLocationDao: SrLocationDao
) {

    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
    val lowStockProducts: Flow<List<ProductEntity>> = productDao.getLowStockProducts()
    val allRetailers: Flow<List<RetailerEntity>> = retailerDao.getAllRetailers()
    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()
    val allSrLocations: Flow<List<SrLocationEntity>> = srLocationDao.getAllSrLocations()

    suspend fun seedInitialDataIfEmpty() {
        try {
            if (productDao.getProductCount() == 0) {
                productDao.insertProducts(AppDatabase.initialProducts)
            }
            if (retailerDao.getRetailerCount() == 0) {
                retailerDao.insertRetailers(AppDatabase.initialRetailers)
            }
            if (srLocationDao.getLocationCount() == 0) {
                srLocationDao.insertLocations(AppDatabase.initialSrLocations)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    suspend fun regenerateInvoicePdf(order: OrderEntity): File? {
        return try {
            val items = orderDao.getOrderItems(order.id)
            val pdfFile = PdfInvoiceGenerator.generateInvoicePdf(context, order, items)
            if (pdfFile != null && pdfFile.exists()) {
                val updated = order.copy(pdfFilePath = pdfFile.absolutePath)
                orderDao.updateOrder(updated)
            }
            pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getOrdersByRole(role: String): Flow<List<OrderEntity>> = orderDao.getOrdersByRole(role)

    suspend fun getOrderItems(orderId: Long): List<OrderItemEntity> = orderDao.getOrderItems(orderId)

    suspend fun createOrder(
        retailer: RetailerEntity,
        srName: String,
        srId: String,
        userRole: String,
        cartItems: List<CartItem>,
        paymentMethod: String,
        paymentStatus: String,
        discount: Double = 0.0,
        isOnline: Boolean = true
    ): Pair<OrderEntity, File?> {
        val totalAmount = cartItems.sumOf { it.product.tradeOfferPrice * it.quantity }
        val grandTotal = (totalAmount - discount).coerceAtLeast(0.0)
        val orderNo = "ORD-${System.currentTimeMillis().toString().takeLast(6)}"

        val orderEntity = OrderEntity(
            orderNo = orderNo,
            retailerId = retailer.id,
            retailerName = retailer.shopName,
            retailerPhone = retailer.phone,
            srId = srId,
            srName = srName,
            userRole = userRole,
            totalAmount = totalAmount,
            discountAmount = discount,
            grandTotal = grandTotal,
            paymentMethod = paymentMethod,
            paymentStatus = paymentStatus,
            transactionId = if (paymentStatus == "PAID") "TXN-${UUID.randomUUID().toString().take(8).uppercase()}" else "",
            orderStatus = "CONFIRMED",
            createdAt = System.currentTimeMillis(),
            isSynced = isOnline
        )

        val orderId = orderDao.insertOrder(orderEntity)

        val orderItems = cartItems.map { cart ->
            OrderItemEntity(
                orderId = orderId,
                productId = cart.product.id,
                productName = cart.product.nameBangla.ifEmpty { cart.product.name },
                unit = cart.product.unit,
                quantity = cart.quantity,
                unitPrice = cart.product.tradeOfferPrice,
                totalPrice = cart.product.tradeOfferPrice * cart.quantity
            )
        }
        orderDao.insertOrderItems(orderItems)

        // Deduct inventory stock & check low stock threshold
        cartItems.forEach { cart ->
            productDao.deductStock(cart.product.id, cart.quantity)
            val updatedStock = cart.product.stockQuantity - cart.quantity
            if (updatedStock <= cart.product.minStockThreshold) {
                NotificationHelper.showLowStockNotification(
                    context,
                    cart.product.nameBangla.ifEmpty { cart.product.name },
                    updatedStock
                )
            }
        }

        // Update Retailer due balance if unpaid/ledger
        if (paymentStatus != "PAID") {
            retailerDao.updateDueAndOrderCount(retailer.id, grandTotal)
        } else {
            retailerDao.updateDueAndOrderCount(retailer.id, 0.0)
        }

        // Generate Invoice PDF
        val savedOrder = orderEntity.copy(id = orderId)
        val pdfFile = PdfInvoiceGenerator.generateInvoicePdf(context, savedOrder, orderItems)
        val finalOrder = if (pdfFile != null) {
            val updated = savedOrder.copy(pdfFilePath = pdfFile.absolutePath)
            orderDao.updateOrder(updated)
            updated
        } else {
            savedOrder
        }

        NotificationHelper.showOrderSuccessNotification(context, orderNo, grandTotal)

        return Pair(finalOrder, pdfFile)
    }

    suspend fun addStock(productId: Long, quantity: Int) {
        productDao.addStock(productId, quantity)
    }

    suspend fun addNewProduct(product: ProductEntity) {
        productDao.insertProduct(product)
    }

    suspend fun addNewRetailer(retailer: RetailerEntity) {
        retailerDao.insertRetailer(retailer)
    }

    suspend fun updateSrLocation(srLocation: SrLocationEntity) {
        srLocationDao.insertOrUpdate(srLocation)
    }

    suspend fun syncOfflineOrders(): Int {
        val unsynced = orderDao.getUnsyncedOrders()
        if (unsynced.isNotEmpty()) {
            orderDao.markOrdersAsSynced(unsynced.map { it.id })
        }
        return unsynced.size
    }
}
