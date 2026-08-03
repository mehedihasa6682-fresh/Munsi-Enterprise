package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.R

object NotificationHelper {

    private const val CHANNEL_ID = "ordercut_alerts"
    private const val CHANNEL_NAME = "OrderCut Alerts & Low Stock"

    fun showLowStockNotification(context: Context, productName: String, remainingStock: Int) {
        try {
            createChannel(context)
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("⚠️ স্টক সতর্কতা! (Low Stock Warning)")
                .setContentText("$productName এর মজুত শেষ পর্যায়ে! অবশিষ্ট আছে: $remainingStock টি।")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            manager.notify((System.currentTimeMillis() % 10000).toInt(), notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showOrderSuccessNotification(context: Context, orderNo: String, amount: Double) {
        try {
            createChannel(context)
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("✅ নতুন অর্ডার সফলভাবে গৃহীত হয়েছে!")
                .setContentText("অর্ডার নং #$orderNo - মোট মূল্য: ৳$amount। ইনভয়েস তৈরি করা হয়েছে।")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

            manager.notify((System.currentTimeMillis() % 10000).toInt(), notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for low stock alerts and new orders"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
