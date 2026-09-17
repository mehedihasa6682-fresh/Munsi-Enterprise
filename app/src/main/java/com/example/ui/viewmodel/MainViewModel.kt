package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.RetailerEntity
import com.example.data.local.entities.SrLocationEntity
import com.example.data.repository.CartItem
import com.example.data.repository.OrderRepository
import com.example.ui.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = OrderRepository(
        context = application,
        productDao = db.productDao(),
        retailerDao = db.retailerDao(),
        orderDao = db.orderDao(),
        srLocationDao = db.srLocationDao()
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.seedInitialDataIfEmpty()
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    val allProducts: StateFlow<List<ProductEntity>> = repository.allProducts
        .catch { e -> e.printStackTrace(); emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockProducts: StateFlow<List<ProductEntity>> = repository.lowStockProducts
        .catch { e -> e.printStackTrace(); emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRetailers: StateFlow<List<RetailerEntity>> = repository.allRetailers
        .catch { e -> e.printStackTrace(); emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .catch { e -> e.printStackTrace(); emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSrLocations: StateFlow<List<SrLocationEntity>> = repository.allSrLocations
        .catch { e -> e.printStackTrace(); emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedRole = MutableStateFlow(UserRole.ADMIN)
    val selectedRole: StateFlow<UserRole> = _selectedRole.asStateFlow()

    private val _selectedRetailer = MutableStateFlow<RetailerEntity?>(null)
    val selectedRetailer: StateFlow<RetailerEntity?> = _selectedRetailer.asStateFlow()

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("সব")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _lastGeneratedInvoice = MutableStateFlow<File?>(null)
    val lastGeneratedInvoice: StateFlow<File?> = _lastGeneratedInvoice.asStateFlow()

    private val _showInvoiceDialog = MutableStateFlow(false)
    val showInvoiceDialog: StateFlow<Boolean> = _showInvoiceDialog.asStateFlow()

    private val _lastPlacedOrder = MutableStateFlow<OrderEntity?>(null)
    val lastPlacedOrder: StateFlow<OrderEntity?> = _lastPlacedOrder.asStateFlow()

    private val _snackMessage = MutableStateFlow<String?>(null)
    val snackMessage: StateFlow<String?> = _snackMessage.asStateFlow()

    fun setRole(role: UserRole) {
        _selectedRole.value = role
    }

    fun selectRetailer(retailer: RetailerEntity?) {
        _selectedRetailer.value = retailer
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun addToCart(product: ProductEntity, quantity: Int = 1) {
        val currentList = _cart.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == product.id }
        if (index != -1) {
            val existing = currentList[index]
            val newQty = (existing.quantity + quantity).coerceAtMost(product.stockQuantity)
            currentList[index] = existing.copy(quantity = newQty)
        } else {
            val initialQty = quantity.coerceAtMost(product.stockQuantity)
            if (initialQty > 0) {
                currentList.add(CartItem(product, initialQty))
            } else {
                _snackMessage.value = "দুঃখিত, স্টক শেষ হয়ে গেছে!"
                return
            }
        }
        _cart.value = currentList
    }

    fun removeFromCart(productId: Long) {
        _cart.value = _cart.value.filter { it.product.id != productId }
    }

    fun updateCartQuantity(productId: Long, newQty: Int) {
        if (newQty <= 0) {
            removeFromCart(productId)
            return
        }
        val currentList = _cart.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == productId }
        if (index != -1) {
            val product = currentList[index].product
            val validQty = newQty.coerceAtMost(product.stockQuantity)
            if (newQty > product.stockQuantity) {
                _snackMessage.value = "${product.nameBangla} এর সর্বোচ্চ স্টক ${product.stockQuantity} টি!"
            }
            currentList[index] = currentList[index].copy(quantity = validQty)
            _cart.value = currentList
        } else {
            val product = _allProducts.value.find { it.id == productId }
            if (product != null) {
                val validQty = newQty.coerceAtMost(product.stockQuantity)
                if (validQty > 0) {
                    if (newQty > product.stockQuantity) {
                        _snackMessage.value = "${product.nameBangla} এর সর্বোচ্চ স্টক ${product.stockQuantity} টি!"
                    }
                    currentList.add(CartItem(product, validQty))
                    _cart.value = currentList
                } else {
                    _snackMessage.value = "দুঃখিত, স্টক শেষ হয়ে গেছে!"
                }
            }
        }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    fun placeOrder(
        paymentMethod: String,
        paymentStatus: String,
        discount: Double = 0.0
    ) {
        val retailer = _selectedRetailer.value ?: allRetailers.value.firstOrNull()
        if (retailer == null) {
            _snackMessage.value = "অনুগ্রহ করে একটি দোকান নির্বাচন করুন"
            return
        }

        if (_cart.value.isEmpty()) {
            _snackMessage.value = "কার্ট ফাঁকা, পণ্য যোগ করুন"
            return
        }

        val srName = when (_selectedRole.value) {
            UserRole.ADMIN -> "এ্যাডমিন অর্ডার"
            UserRole.SR -> "তানভীর আহমেদ (SR)"
            UserRole.DSR -> "জাহিদুল ইসলাম (DSR)"
            UserRole.DEALER -> "মেসার্স ট্রেড হাউস (ডিলার)"
            UserRole.RETAILER -> "সেলফ অর্ডার (দোকানদার)"
        }

        val srId = when (_selectedRole.value) {
            UserRole.SR -> "SR-101"
            UserRole.DSR -> "DSR-201"
            UserRole.DEALER -> "DLR-301"
            UserRole.RETAILER -> "RTL-${retailer.id}"
            else -> "ADM-001"
        }

        viewModelScope.launch {
            try {
                val (createdOrder, pdfFile) = repository.createOrder(
                    retailer = retailer,
                    srName = srName,
                    srId = srId,
                    userRole = _selectedRole.value.name,
                    cartItems = _cart.value,
                    paymentMethod = paymentMethod,
                    paymentStatus = paymentStatus,
                    discount = discount,
                    isOnline = _isOnline.value
                )

                _lastPlacedOrder.value = createdOrder
                _lastGeneratedInvoice.value = pdfFile
                _showInvoiceDialog.value = true
                clearCart()
                _snackMessage.value = "অর্ডার কাটিং সফল হয়েছে! ইনভয়েস তৈরি হয়েছে।"
            } catch (e: Exception) {
                e.printStackTrace()
                _snackMessage.value = "অর্ডার সংরক্ষণে সমস্যা হয়েছে: ${e.localizedMessage}"
            }
        }
    }

    fun dismissInvoiceDialog() {
        _showInvoiceDialog.value = false
    }

    fun viewInvoiceForOrder(order: OrderEntity) {
        viewModelScope.launch {
            try {
                val file = if (!order.pdfFilePath.isNullOrEmpty() && File(order.pdfFilePath).exists()) {
                    File(order.pdfFilePath)
                } else {
                    repository.regenerateInvoicePdf(order)
                }
                _lastPlacedOrder.value = order
                _lastGeneratedInvoice.value = file
                _showInvoiceDialog.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                _snackMessage.value = "ইনভয়েস লোড করতে সমস্যা হয়েছে: ${e.localizedMessage}"
            }
        }
    }

    fun toggleOnlineStatus() {
        _isOnline.value = !_isOnline.value
        _snackMessage.value = if (_isOnline.value) "অনলাইন মোড চালু হয়েছে" else "অফলাইন মোড চালু হয়েছে - অর্ডার লোকাল ডেটাবেজে জমা হবে"
    }

    fun syncNow() {
        viewModelScope.launch {
            val count = repository.syncOfflineOrders()
            _snackMessage.value = if (count > 0) "$count টি অফলাইন অর্ডার সার্ভারে সিঙ্ক করা হয়েছে!" else "সব ডেটা আগেই সিঙ্ক করা আছে।"
        }
    }

    fun addStock(productId: Long, qty: Int) {
        viewModelScope.launch {
            repository.addStock(productId, qty)
            _snackMessage.value = "ইনভেন্টরিতে $qty টি পণ্য যোগ করা হয়েছে!"
        }
    }

    fun addNewProduct(nameBangla: String, category: String, price: Double, stock: Int, unit: String) {
        viewModelScope.launch {
            repository.addNewProduct(
                ProductEntity(
                    name = nameBangla,
                    nameBangla = nameBangla,
                    category = category,
                    price = price,
                    tradeOfferPrice = price * 0.95,
                    stockQuantity = stock,
                    unit = unit,
                    code = "PRD-${System.currentTimeMillis().toString().takeLast(4)}"
                )
            )
            _snackMessage.value = "নতুন পণ্য যুক্ত হয়েছে!"
        }
    }

    fun addNewRetailer(shopName: String, ownerName: String, phone: String, address: String, beat: String) {
        viewModelScope.launch {
            repository.addNewRetailer(
                RetailerEntity(
                    shopName = shopName,
                    ownerName = ownerName,
                    phone = phone,
                    address = address,
                    routeBeat = beat
                )
            )
            _snackMessage.value = "নতুন দোকান নিবন্ধিত হয়েছে!"
        }
    }

    fun updateSrLocation(srId: String, srName: String, beat: String, lat: Double, lng: Double, visitedShop: String) {
        viewModelScope.launch {
            repository.updateSrLocation(
                SrLocationEntity(
                    srId = srId,
                    srName = srName,
                    beatRoute = beat,
                    latitude = lat,
                    longitude = lng,
                    lastVisitedShop = visitedShop,
                    ordersTakenToday = 9,
                    status = "VISITING"
                )
            )
        }
    }

    fun clearSnack() {
        _snackMessage.value = null
    }
}
