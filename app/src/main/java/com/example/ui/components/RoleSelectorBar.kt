package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.model.UserRole

@Composable
fun RoleSelectorBar(
    selectedRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF0284C7).copy(alpha = 0.08f),
        modifier = modifier
            .fillMaxWidth()
            .testTag("role_selector_bar")
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = "রোল নির্বাচন করুন (Role Switcher):",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(UserRole.values()) { role ->
                    val isSelected = role == selectedRole
                    val (icon, bgColor, contentColor) = getRoleStyle(role, isSelected)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgColor)
                            .clickable { onRoleSelected(role) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("role_chip_${role.name}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = role.titleBangla,
                                tint = contentColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = role.titleBangla,
                                color = contentColor,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getRoleStyle(role: UserRole, isSelected: Boolean): Triple<ImageVector, Color, Color> {
    val icon = when (role) {
        UserRole.ADMIN -> Icons.Default.AdminPanelSettings
        UserRole.SR -> Icons.Default.Badge
        UserRole.DSR -> Icons.Default.LocalShipping
        UserRole.DEALER -> Icons.Default.Storefront
        UserRole.RETAILER -> Icons.Default.Person
    }

    if (isSelected) {
        return Triple(icon, Color(0xFF0F172A), Color.White)
    }

    return Triple(icon, Color.White, Color(0xFF334155))
}
