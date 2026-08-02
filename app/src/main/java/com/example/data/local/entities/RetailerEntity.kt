package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "retailers")
data class RetailerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val shopName: String,
    val ownerName: String,
    val phone: String,
    val address: String,
    val routeBeat: String, // e.g. "মিরপুর ১০ বিট", "ধানমন্ডি রুট"
    val dueBalance: Double = 0.0,
    val totalOrdersCount: Int = 0,
    val latitude: Double = 23.8103,
    val longitude: Double = 90.4125
)
