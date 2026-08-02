package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
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
import com.example.data.local.entities.ProductEntity

@Composable
fun LowStockAlertBanner(
    lowStockItems: List<ProductEntity>,
    onClickViewInventory: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(visible = lowStockItems.isNotEmpty()) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(Color(0xFFFEF2F2))
                .clickable { onClickViewInventory() }
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .testTag("low_stock_banner"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Low Stock Warning",
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "সতর্কতা: ${lowStockItems.size} টি পণ্যের মজুদ শেষ পর্যায়ে!",
                    color = Color(0xFFDC2626),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "ইনভেন্টরি দেখুন >",
                color = Color(0xFFDC2626),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
