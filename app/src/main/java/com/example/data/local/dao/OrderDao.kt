package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.OrderItemEntity
import kotlinx.coroutines.flow.Flow

data class OrderWithItems(
    val order: OrderEntity,
    val items: List<OrderItemEntity>
)

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE userRole = :role ORDER BY createdAt DESC")
    fun getOrdersByRole(role: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE isSynced = 0")
    suspend fun getUnsyncedOrders(): List<OrderEntity>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItems(orderId: Long): List<OrderItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markOrdersAsSynced(ids: List<Long>)

    @Query("UPDATE orders SET orderStatus = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: String)
}
