package com.example.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.OrderItemEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfInvoiceGenerator {

    fun generateInvoicePdf(
        context: Context,
        order: OrderEntity,
        items: List<OrderItemEntity>
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size in points
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint()
        val titlePaint = Paint()
        val boldPaint = Paint()

        // Background
        canvas.drawColor(Color.WHITE)

        // Header Background Bar
        paint.color = Color.parseColor("#0F172A") // Navy Primary
        canvas.drawRect(0f, 0f, 595f, 90f, paint)

        // Company Title
        titlePaint.color = Color.WHITE
        titlePaint.textSize = 22f
        titlePaint.isFakeBoldText = true
        canvas.drawText("OrderCut Sales Invoice", 30f, 45f, titlePaint)

        titlePaint.textSize = 12f
        titlePaint.isFakeBoldText = false
        titlePaint.color = Color.parseColor("#94A3B8")
        canvas.drawText("Automated Order & Inventory System", 30f, 68f, titlePaint)

        // Date and Invoice No
        val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(order.createdAt))
        boldPaint.color = Color.parseColor("#0F172A")
        boldPaint.textSize = 12f
        boldPaint.isFakeBoldText = true

        paint.color = Color.parseColor("#334155")
        paint.textSize = 11f

        var y = 120f
        canvas.drawText("Invoice No: ${order.orderNo}", 30f, y, boldPaint)
        canvas.drawText("Date: $dateStr", 350f, y, paint)

        y += 25f
        canvas.drawRect(30f, y, 565f, y + 1f, paint) // Line divider

        // Customer & SR Details
        y += 25f
        canvas.drawText("Shop / Retailer: ${order.retailerName}", 30f, y, boldPaint)
        canvas.drawText("SR Name: ${order.srName}", 350f, y, paint)

        y += 20f
        canvas.drawText("Phone: ${order.retailerPhone}", 30f, y, paint)
        canvas.drawText("Role: ${order.userRole}", 350f, y, paint)

        y += 25f
        canvas.drawRect(30f, y, 565f, y + 1f, paint) // Line divider

        // Table Header
        y += 25f
        paint.color = Color.parseColor("#F1F5F9")
        canvas.drawRect(30f, y - 15f, 565f, y + 15f, paint)

        boldPaint.color = Color.parseColor("#0F172A")
        boldPaint.textSize = 11f
        canvas.drawText("Product Name", 40f, y, boldPaint)
        canvas.drawText("Qty", 320f, y, boldPaint)
        canvas.drawText("Unit Price", 380f, y, boldPaint)
        canvas.drawText("Total (BDT)", 480f, y, boldPaint)

        y += 25f
        paint.color = Color.parseColor("#1E293B")
        paint.textSize = 10f

        // Itemized rows
        items.forEach { item ->
            canvas.drawText(item.productName.take(35), 40f, y, paint)
            canvas.drawText("${item.quantity} ${item.unit}", 320f, y, paint)
            canvas.drawText("Tk ${item.unitPrice}", 380f, y, paint)
            canvas.drawText("Tk ${item.totalPrice}", 480f, y, paint)
            y += 22f
        }

        y += 10f
        canvas.drawRect(30f, y, 565f, y + 1f, paint) // Divider

        // Summary Calculations
        y += 25f
        paint.textSize = 11f
        canvas.drawText("Sub Total:", 380f, y, paint)
        canvas.drawText("Tk ${order.totalAmount}", 480f, y, paint)

        y += 20f
        canvas.drawText("Discount:", 380f, y, paint)
        canvas.drawText("- Tk ${order.discountAmount}", 480f, y, paint)

        y += 25f
        boldPaint.textSize = 13f
        boldPaint.color = Color.parseColor("#0284C7")
        canvas.drawText("Grand Total:", 380f, y, boldPaint)
        canvas.drawText("Tk ${order.grandTotal}", 480f, y, boldPaint)

        y += 25f
        paint.color = Color.parseColor("#059669")
        paint.textSize = 11f
        paint.isFakeBoldText = true
        canvas.drawText("Payment: ${order.paymentMethod} (${order.paymentStatus})", 30f, y, paint)

        // Footer stamp
        y += 60f
        paint.color = Color.parseColor("#94A3B8")
        paint.textSize = 10f
        paint.isFakeBoldText = false
        canvas.drawText("Thank you for your business! - OrderCut Pro Mobile App", 150f, y, paint)

        pdfDocument.finishPage(page)

        return try {
            val fileDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
            if (!fileDir.exists()) {
                fileDir.mkdirs()
            }
            val pdfFile = File(fileDir, "Invoice_${order.orderNo}.pdf")
            FileOutputStream(pdfFile).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            pdfDocument.close()
            pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                pdfDocument.close()
            } catch (_: Exception) {}
            null
        }
    }
}
