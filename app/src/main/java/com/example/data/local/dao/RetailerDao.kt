package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.RetailerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RetailerDao {
    @Query("SELECT * FROM retailers ORDER BY shopName ASC")
    fun getAllRetailers(): Flow<List<RetailerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRetailers(retailers: List<RetailerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRetailer(retailer: RetailerEntity): Long

    @Update
    suspend fun updateRetailer(retailer: RetailerEntity)

    @Query("UPDATE retailers SET dueBalance = dueBalance + :amount, totalOrdersCount = totalOrdersCount + 1 WHERE id = :retailerId")
    suspend fun updateDueAndOrderCount(retailerId: Long, amount: Double)
}
