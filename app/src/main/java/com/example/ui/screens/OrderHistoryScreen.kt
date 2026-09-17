package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.content.ActivityNotFoundException
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.OrderEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrderHistoryScreen(
    orders: List<OrderEntity>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAF8))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                imageVector = Icons.Default.ReceiptLong,
                contentDescription = "Orders History",
                tint = Color(0xFF0284C7),
                modifier = Modifier.size(26.dp)
            )
            Text(
                text = "সব অর্ডার ও মেমো হিস্টোরি (Order History)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("এখনো কোন ইনভয়েস অথবা অর্ডার কাটিং করা হয়নি।", color = Color.Gray, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(orders) { order ->
                    val dateFormatted = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(order.createdAt))

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("order_history_card_${order.orderNo}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "#${order.orderNo}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (order.isSynced) Color(0xFFDCFCE7) else Color(0xFFFEF3C7))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Icon(
                                                imageVector = if (order.isSynced) Icons.Default.CloudDone else Icons.Default.CloudOff,
                                                contentDescription = "Sync",
                                                tint = if (order.isSynced) Color(0xFF16A34A) else Color(0xFFD97706),
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Text(
                                                text = if (order.isSynced) "সিঙ্কড" else "অফলাইন",
                                                fontSize = 9.sp,
                                                color = if (order.isSynced) Color(0xFF16A34A) else Color(0xFFD97706),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = dateFormatted,
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "দোকান: ${order.retailerName} (${order.retailerPhone})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "অর্ডার কাটার ভূমিকা: ${order.srName} (${order.userRole})",
                                fontSize = 11.sp,
                                color = Color(0xFF475569)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "মোট বিল: ৳${order.grandTotal} BDT",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF0284C7)
                                    )
                                    Text(
                                        text = "পেমেন্ট: ${order.paymentMethod} (${order.paymentStatus})",
                                        fontSize = 10.sp,
                                        color = Color(0xFF059669)
                                    )
                                }

                                // PDF actions
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (!order.pdfFilePath.isNullOrEmpty()) {
                                        val pdfFile = File(order.pdfFilePath)

                                        OutlinedButton(
                                            onClick = { sharePdf(context, pdfFile) },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("btn_share_pdf_${order.orderNo}")
                                        ) {
                                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(14.dp))
                                        }

                                        Button(
                                            onClick = { viewPdf(context, pdfFile) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("btn_view_pdf_${order.orderNo}")
                                        ) {
                                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = "PDF", modifier = Modifier.size(14.dp))
                                            Text("PDF মেমো", fontSize = 11.sp, modifier = Modifier.padding(start = 2.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun sharePdf(context: Context, file: File) {
    try {
        if (!file.exists()) {
            Toast.makeText(context, "ইনভয়েস ফাইলটি খুঁজে পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
            return
        }
        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(shareIntent, "ইনভয়েস শেয়ার করুন").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "শেয়ার করতে সমস্যা হয়েছে: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}

private fun viewPdf(context: Context, file: File) {
    try {
        if (!file.exists()) {
            Toast.makeText(context, "ইনভয়েস ফাইলটি খুঁজে পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
            return
        }
        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "পিডিএফ দেখার কোনো অ্যাপ নেই। শেয়ার অপশন ব্যবহার করুন।", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "পিডিএফ ওপেন করতে সমস্যা হয়েছে: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}
