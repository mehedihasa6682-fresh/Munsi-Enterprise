package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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

@Composable
fun OfflineSyncBanner(
    isOnline: Boolean,
    onToggleOnline: () -> Unit,
    onSyncNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isOnline) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
    val textColor = if (isOnline) Color(0xFF16A34A) else Color(0xFFD97706)
    val statusText = if (isOnline) "অনলাইন সিঙ্ক চালু (Online Mode)" else "অফলাইন মোড চালু (Offline Saved Mode)"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.clickable { onToggleOnline() }
        ) {
            Icon(
                imageVector = if (isOnline) Icons.Default.CloudDone else Icons.Default.CloudOff,
                contentDescription = "Online Status",
                tint = textColor,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = statusText,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = onSyncNow,
            colors = ButtonDefaults.buttonColors(containerColor = textColor),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("btn_trigger_sync")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = "Sync",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "সিঙ্ক করুন",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
