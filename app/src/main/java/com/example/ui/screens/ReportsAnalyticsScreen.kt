package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.RetailerEntity

@Composable
fun ReportsAnalyticsScreen(
    orders: List<OrderEntity>,
    retailers: List<RetailerEntity>,
    modifier: Modifier = Modifier
) {
    val totalRevenue = orders.sumOf { it.grandTotal }
    val totalDiscount = orders.sumOf { it.discountAmount }
    val paidRevenue = orders.filter { it.paymentStatus == "PAID" }.sumOf { it.grandTotal }
    val pendingRevenue = totalRevenue - paidRevenue
    val totalDueInShops = retailers.sumOf { it.dueBalance }

    // Group orders by SR
    val srPerformance = orders.groupBy { it.srName }
        .mapValues { entry ->
            Pair(entry.value.size, entry.value.sumOf { it.grandTotal })
        }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAF8))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = "Reports",
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "সেলস রিপোর্ট ও এনালিটিক্স",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }
        }

        // Executive Summary Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reports_summary_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("সেলস ও রিভেনিউ সামারি (Total Sales)", fontSize = 12.sp, color = Color(0xFF94A3B8))
                    Text("৳$totalRevenue BDT", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF38BDF8))

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ReportKpi("নগদ পেমেন্ট (Paid)", "৳$paidRevenue", Color(0xFF4ADE80))
                        ReportKpi("বাকি বিল (Pending)", "৳$pendingRevenue", Color(0xFFF87171))
                        ReportKpi("মোট ছাড় (Discount)", "৳$totalDiscount", Color(0xFFFACC15))
                    }
                }
            }
        }

        // Retailer Due Summary Box
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("due_ledger_report_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = "Due", tint = Color(0xFFDC2626))
                            Text(
                                text = "রিটেইলার বকেয়া খাতা (Retailer Due Ledger)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Text("মোট বকেয়া: ৳$totalDueInShops", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFDC2626))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    retailers.filter { it.dueBalance > 0 }.forEach { shop ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(shop.shopName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("${shop.ownerName} (${shop.routeBeat})", fontSize = 10.sp, color = Color.Gray)
                            }
                            Text("৳${shop.dueBalance}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                        }
                    }
                }
            }
        }

        // SR Leaderboard
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sr_leaderboard_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Leaderboard, contentDescription = "Leaderboard", tint = Color(0xFF0284C7))
                        Text(
                            text = "এসআর পারফরম্যান্স লিডারবোর্ড (SR Sales Ranking)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (srPerformance.isEmpty()) {
                        Text("এখনো পর্যাপ্ত সেলস ডাটা নেই।", fontSize = 11.sp, color = Color.Gray)
                    } else {
                        srPerformance.entries.forEachIndexed { index, entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .background(Color(0xFFF8FAF8), RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .background(if (index == 0) Color(0xFFEAB308) else Color(0xFF94A3B8), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("#${index + 1}", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    Column {
                                        Text(entry.key, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("মোট অর্ডার: ${entry.value.first} টি", fontSize = 10.sp, color = Color.Gray)
                                    }
                                }

                                Text("৳${entry.value.second}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF059669))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportKpi(title: String, amount: String, color: Color) {
    Column {
        Text(title, fontSize = 10.sp, color = Color(0xFF94A3B8))
        Text(amount, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
