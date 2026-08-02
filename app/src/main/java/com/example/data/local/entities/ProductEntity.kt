package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val nameBangla: String,
    val category: String,
    val price: Double,
    val tradeOfferPrice: Double = price,
    val stockQuantity: Int,
    val minStockThreshold: Int = 30,
    val unit: String = "প্যাক", // e.g. কার্টন, প্যাক, পিস, কেজি
    val code: String = "",
    val imageUrl: String = ""
)
