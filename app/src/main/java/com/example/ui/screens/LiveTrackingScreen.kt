package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PersonPin
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.SrLocationEntity

@Composable
fun LiveTrackingScreen(
    srLocations: List<SrLocationEntity>,
    onUpdateSrLocation: (srId: String, srName: String, beat: String, lat: Double, lng: Double, shop: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAF8))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = "Live Tracking",
                    tint = Color(0xFF7C3AED),
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "লাইভ ফিল্ড এসআর ও বিট ট্র্যাকিং",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            Button(
                onClick = {
                    onUpdateSrLocation("SR-101", "তানভীর আহমেদ (SR)", "মিরপুর বিট", 23.8105, 90.3695, "নিউ গোল্ডেন স্টোর")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_refresh_gps")
            ) {
                Icon(imageVector = Icons.Default.GpsFixed, contentDescription = "GPS", modifier = Modifier.size(14.dp))
                Text("GPS রিলোড", fontSize = 11.sp, modifier = Modifier.padding(start = 4.dp))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Visual Beat Map Canvas Box
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .testTag("visual_map_canvas")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Map background grid
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    // Draw grid lines
                    drawLine(Color(0xFF334155), Offset(0f, size.height / 2), Offset(size.width, size.height / 2), pathEffect = pathEffect)
                    drawLine(Color(0xFF334155), Offset(size.width / 2, 0f), Offset(size.width / 2, size.height), pathEffect = pathEffect)

                    // Draw route connectors
                    drawLine(Color(0xFF38BDF8), Offset(60f, 60f), Offset(200f, 120f), strokeWidth = 3f)
                    drawLine(Color(0xFF38BDF8), Offset(200f, 120f), Offset(380f, 70f), strokeWidth = 3f)
                }

                Text(
                    text = "📍 জিপিএস বিট ম্যাপ (GPS Beat Map Canvas)",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.align(Alignment.TopStart)
                )

                // SR Pin 1
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 40.dp, top = 40.dp)
                ) {
                    MapPinBadge("SR-101: তানভীর", Color(0xFF22C55E))
                }

                // SR Pin 2
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                ) {
                    MapPinBadge("SR-102: শাহিন", Color(0xFF38BDF8))
                }

                // SR Pin 3
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 30.dp, bottom = 20.dp)
                ) {
                    MapPinBadge("DSR-201: জাহিদ", Color(0xFFEAB308))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "ফিল্ড রিপ্রেজেন্টেটিভ তালিকা (${srLocations.size} জন এক্টিভ):",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // SR Location Cards List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(srLocations) { sr ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sr_tracking_card_${sr.srId}")
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PersonPin,
                                    contentDescription = "SR",
                                    tint = Color(0xFF7C3AED),
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text(
                                        text = sr.srName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "বিট: ${sr.beatRoute} • কোঅর্ডিনেট: (${sr.latitude}, ${sr.longitude})",
                                        fontSize = 10.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (sr.status == "VISITING") Color(0xFFDCFCE7) else Color(0xFFFEF3C7))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (sr.status == "VISITING") "দোকানে উপস্থিত" else "সক্রিয়",
                                    fontSize = 10.sp,
                                    color = if (sr.status == "VISITING") Color(0xFF16A34A) else Color(0xFFD97706),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAF8), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "সর্বশেষ শপ ভিজিট: ${sr.lastVisitedShop}",
                                fontSize = 11.sp,
                                color = Color(0xFF334155),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "আজকের অর্ডার: ${sr.ordersTakenToday} টি",
                                fontSize = 11.sp,
                                color = Color(0xFF0284C7),
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MapPinBadge(name: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(Icons.Default.LocationOn, contentDescription = "Pin", tint = Color.White, modifier = Modifier.size(12.dp))
        Text(name, fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}
