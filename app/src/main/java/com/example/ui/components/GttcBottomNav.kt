package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
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
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandNavy
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextMuted

enum class NavTab(val label: String) {
    DASHBOARD("Dashboard"),
    ACCOUNTS("Accounts"),
    ADD("Add"),
    SERIES("Series"),
    SETTINGS("Settings")
}

@Composable
fun GttcBottomNav(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = SurfaceWhite,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dashboard
            NavItem(
                label = "Dashboard",
                isSelected = selectedTab == NavTab.DASHBOARD,
                iconSelected = Icons.Filled.Dashboard,
                iconUnselected = Icons.Outlined.Dashboard,
                onClick = { onTabSelected(NavTab.DASHBOARD) },
                testTag = "nav_dashboard"
            )

            // Accounts
            NavItem(
                label = "Accounts",
                isSelected = selectedTab == NavTab.ACCOUNTS,
                iconSelected = Icons.Filled.People,
                iconUnselected = Icons.Outlined.People,
                onClick = { onTabSelected(NavTab.ACCOUNTS) },
                testTag = "nav_accounts"
            )

            // Center ADD Button (Prominent elevated floating style)
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(BrandBlue)
                    .clickable { onTabSelected(NavTab.ADD) }
                    .testTag("nav_add_center"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add New Account",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Series
            NavItem(
                label = "Series",
                isSelected = selectedTab == NavTab.SERIES,
                iconSelected = Icons.Filled.Layers,
                iconUnselected = Icons.Outlined.Layers,
                onClick = { onTabSelected(NavTab.SERIES) },
                testTag = "nav_series"
            )

            // Settings
            NavItem(
                label = "Settings",
                isSelected = selectedTab == NavTab.SETTINGS,
                iconSelected = Icons.Filled.Settings,
                iconUnselected = Icons.Outlined.Settings,
                onClick = { onTabSelected(NavTab.SETTINGS) },
                testTag = "nav_settings"
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    isSelected: Boolean,
    iconSelected: ImageVector,
    iconUnselected: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag(testTag)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(if (isSelected) Color(0xFFEFF6FF) else Color.Transparent)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) iconSelected else iconUnselected,
                contentDescription = label,
                tint = if (isSelected) BrandBlue else TextMuted,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) BrandBlue else TextMuted
        )
    }
}
