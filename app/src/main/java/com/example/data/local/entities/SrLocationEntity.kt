package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sr_locations")
data class SrLocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val srId: String,
    val srName: String,
    val beatRoute: String,
    val latitude: Double,
    val longitude: Double,
    val lastVisitedShop: String,
    val ordersTakenToday: Int,
    val lastUpdatedTime: Long = System.currentTimeMillis(),
    val status: String // ACTIVE, VISITING, IDLE
)
