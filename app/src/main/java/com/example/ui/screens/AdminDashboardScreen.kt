package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.SrLocationEntity

@Composable
fun AdminDashboardScreen(
    orders: List<OrderEntity>,
    lowStockItems: List<ProductEntity>,
    srLocations: List<SrLocationEntity>,
    onNavigateToOrderBooking: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToTracking: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToOrders: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalRevenue = orders.sumOf { it.grandTotal }
    val totalOrdersCount = orders.size
    val pendingSyncCount = orders.count { !it.isSynced }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAF8))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Welcome & Overview Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_kpi_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "আজকের মোট সেলস পারফরম্যান্স (Today's Sales)",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "৳$totalRevenue BDT",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF38BDF8)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        KpiItem("মোট অর্ডার", "$totalOrdersCount টি", Color(0xFF38BDF8))
                        KpiItem("একটিভ এসআর", "${srLocations.size} জন", Color(0xFF4ADE80))
                        KpiItem("স্টক এলার্ট", "${lowStockItems.size} টি", Color(0xFFFACC15))
                        KpiItem("অফলাইন পেন্ডিং", "$pendingSyncCount টি", Color(0xFFF87171))
                    }
                }
            }
        }

        // Quick Action Shortcut Buttons
        item {
            Text(
                text = "দ্রুত কাজ সম্পাদন করুন (Quick Actions):",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    title = "অর্ডার কাটুন",
                    subtitle = "নতুন মেমো",
                    icon = Icons.Default.AddShoppingCart,
                    color = Color(0xFF0284C7),
                    onClick = onNavigateToOrderBooking,
                    modifier = Modifier.weight(1f).testTag("quick_btn_order")
                )

                QuickActionButton(
                    title = "ইনভেন্টরি",
                    subtitle = "মজুদ চেক",
                    icon = Icons.Default.Inventory,
                    color = Color(0xFF059669),
                    onClick = onNavigateToInventory,
                    modifier = Modifier.weight(1f).testTag("quick_btn_inventory")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    title = "লাইভ ট্র্যাকিং",
                    subtitle = "এসআর বিট",
                    icon = Icons.Default.LocationOn,
                    color = Color(0xFF7C3AED),
                    onClick = onNavigateToTracking,
                    modifier = Modifier.weight(1f).testTag("quick_btn_tracking")
                )

                QuickActionButton(
                    title = "সেলস রিপোর্ট",
                    subtitle = "এনালিটিক্স",
                    icon = Icons.Default.Analytics,
                    color = Color(0xFFD97706),
                    onClick = onNavigateToReports,
                    modifier = Modifier.weight(1f).testTag("quick_btn_reports")
                )
            }
        }

        // Live Field SR Status Overview
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sr_live_status_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.People, contentDescription = "SR", tint = Color(0xFF7C3AED))
                            Text(
                                text = "লাইভ এসআর ফিল্ড স্ট্যাটাস (Live SR Status)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Text(
                            text = "সব দেখুন >",
                            fontSize = 11.sp,
                            color = Color(0xFF0284C7),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateToTracking() }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    srLocations.take(3).forEach { sr ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(Color(0xFFF8FAF8), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(sr.srName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("বিট: ${sr.beatRoute} • লাস্ট শপ: ${sr.lastVisitedShop}", fontSize = 10.sp, color = Color.Gray)
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (sr.status == "VISITING") Color(0xFFDCFCE7) else Color(0xFFFEF3C7))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (sr.status == "VISITING") "দোকানে ভিজিট" else "এক্টিভ",
                                    fontSize = 10.sp,
                                    color = if (sr.status == "VISITING") Color(0xFF16A34A) else Color(0xFFD97706),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent Orders Overview
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recent_orders_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Receipt, contentDescription = "Recent Orders", tint = Color(0xFF0284C7))
                            Text(
                                text = "সাম্প্রতিক মেমো / ইনভয়েস (${orders.size}টি)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Text(
                            text = "সব ইনভয়েস >",
                            fontSize = 11.sp,
                            color = Color(0xFF0284C7),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateToOrders() }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (orders.isEmpty()) {
                        Text("এখনো কোনো অর্ডার কাটা হয়নি।", fontSize = 11.sp, color = Color.Gray)
                    } else {
                        orders.take(3).forEach { order ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("#${order.orderNo} - ${order.retailerName}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("কাটুন ভূমিকা: ${order.srName} (${order.paymentMethod})", fontSize = 10.sp, color = Color.Gray)
                                }

                                Text("৳${order.grandTotal}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0284C7))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiItem(title: String, value: String, color: Color) {
    Column {
        Text(title, fontSize = 9.sp, color = Color(0xFF94A3B8))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(24.dp))
            Column {
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(subtitle, fontSize = 9.sp, color = Color.White.copy(alpha = 0.8f))
            }
        }
    }
}
