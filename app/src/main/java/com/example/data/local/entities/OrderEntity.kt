package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNo: String,
    val retailerId: Long,
    val retailerName: String,
    val retailerPhone: String,
    val srId: String,
    val srName: String,
    val userRole: String, // SR, DSR, DEALER, RETAILER
    val totalAmount: Double,
    val discountAmount: Double = 0.0,
    val grandTotal: Double,
    val paymentMethod: String, // BKASH, NAGAD, UPAY, CASH, LEDGER
    val paymentStatus: String, // PAID, PENDING, DUE
    val transactionId: String = "",
    val orderStatus: String, // PENDING, CONFIRMED, DELIVERED
    val createdAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = true,
    val pdfFilePath: String? = null
)
