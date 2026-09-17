package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.SrLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SrLocationDao {
    @Query("SELECT * FROM sr_locations ORDER BY lastUpdatedTime DESC")
    fun getAllSrLocations(): Flow<List<SrLocationEntity>>

    @Query("SELECT COUNT(*) FROM sr_locations")
    suspend fun getLocationCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<SrLocationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(location: SrLocationEntity)

    @Update
    suspend fun updateLocation(location: SrLocationEntity)
}
