package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Account
import com.example.model.SeriesData
import com.example.ui.components.AccountCard
import com.example.ui.components.NavTab
import com.example.ui.components.StatCard
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandNavy
import com.example.ui.theme.GoldPoint
import com.example.ui.theme.GrowGreen
import com.example.ui.theme.PendingSlate
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMedium
import com.example.ui.theme.TextMuted
import com.example.viewmodel.MainViewModel

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val stats by viewModel.dashboardStats.collectAsState()
    val todayAccounts by viewModel.todayAccounts.collectAsState()
    val recentCompleted by viewModel.recentCompletedAccounts.collectAsState()
    val mismatchedPlatforms by viewModel.mismatchedPlatforms.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Welcome Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(BrandNavy, Color(0xFF003E99))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "GTTC Command Center",
                                color = Color(0xFF93C5FD),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Account Overview",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { viewModel.openAddAccount() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0052CC),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("dashboard_quick_add_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Account",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "New Ac", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Completion Status Bar Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF4ADE80),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Growth Completion Ratio",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                val percent = if (stats.totalAccounts > 0) {
                                    (stats.completedAccounts.toFloat() / stats.totalAccounts * 100).toInt()
                                } else 0
                                Text(
                                    text = "$percent% Completed",
                                    color = Color(0xFF4ADE80),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            val progress = if (stats.totalAccounts > 0) {
                                (stats.completedAccounts.toFloat() / stats.totalAccounts)
                            } else 0f
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = Color(0xFF4ADE80),
                                trackColor = Color.White.copy(alpha = 0.2f),
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${stats.completedAccounts} Grow Completed",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "${stats.pendingAccounts} Pending",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Warning Alert for Mismatched / Duplicate platforms from pla.csv
        if (mismatchedPlatforms.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("review_alert_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFDE68A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Alert",
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Platform Review Required",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            Text(
                                text = "${mismatchedPlatforms.size} links have duplicate URLs or mismatched series from pla.csv.",
                                fontSize = 11.sp,
                                color = Color(0xFFB45309)
                            )
                        }
                        Button(
                            onClick = { viewModel.setTab(NavTab.SERIES) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(text = "Review", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // The 4 Main Statistics Cards
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Stat 1: Total Accounts
                    StatCard(
                        title = "Total Accounts",
                        value = stats.totalAccounts.toString(),
                        subtitle = "All registered",
                        icon = Icons.Default.People,
                        iconBgColor = Color(0xFFDBEAFE),
                        iconTint = BrandBlue,
                        modifier = Modifier.weight(1f)
                    )

                    // Stat 2: Successful Completed
                    StatCard(
                        title = "Completed",
                        value = stats.completedAccounts.toString(),
                        subtitle = "Verified Grow",
                        icon = Icons.Default.CheckCircle,
                        iconBgColor = Color(0xFFDCFCE7),
                        iconTint = GrowGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Stat 3: Total Earn Point
                    StatCard(
                        title = "Total Earn Point",
                        value = "${stats.totalEarnPoints}",
                        subtitle = "300 Pts / Grow",
                        icon = Icons.Default.Stars,
                        iconBgColor = Color(0xFFFEF3C7),
                        iconTint = GoldPoint,
                        modifier = Modifier.weight(1f)
                    )

                    // Stat 4: Recently Completed Series
                    StatCard(
                        title = "Recent Series",
                        value = stats.recentlyCompletedSeries,
                        subtitle = "Active growth",
                        icon = Icons.Default.Layers,
                        iconBgColor = Color(0xFFF1F5F9),
                        iconTint = BrandNavy,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Access to Each Series
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Series Access",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    TextButton(onClick = { viewModel.setTab(NavTab.SERIES) }) {
                        Text(text = "View All (8)", color = BrandBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(SeriesData.SERIES_LIST) { series ->
                        Card(
                            modifier = Modifier
                                .clickable { viewModel.openSeriesDetails(series.name) }
                                .testTag("series_pill_${series.name}")
                                .border(1.dp, BorderLight, RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(series.primaryColor)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = series.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                    Text(
                                        text = "${series.platforms.size} Platforms",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Today's Registrations Section
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Today",
                        tint = BrandBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Today's Registrations",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFDBEAFE))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = todayAccounts.size.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandBlue
                        )
                    }
                }

                TextButton(onClick = { viewModel.setTab(NavTab.ACCOUNTS) }) {
                    Text(text = "View All", color = BrandBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (todayAccounts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "No registrations recorded today yet", color = TextMuted, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.openAddAccount() },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Register Account Now", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        } else {
            items(todayAccounts) { account ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    AccountCard(
                        account = account,
                        onClick = { viewModel.openAccountDetails(account) },
                        onWhatsAppClick = { viewModel.openWhatsApp(context, account.whatsapp) },
                        onEmailClick = { viewModel.openEmail(context, account.email) }
                    )
                }
            }
        }

        // Recent Completed (Grow) Accounts Section
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = GrowGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Recent Completed Accounts",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }
            }
        }

        if (recentCompleted.isEmpty()) {
            item {
                Text(
                    text = "No completed accounts yet",
                    color = TextMuted,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        } else {
            items(recentCompleted) { account ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    AccountCard(
                        account = account,
                        onClick = { viewModel.openAccountDetails(account) },
                        onWhatsAppClick = { viewModel.openWhatsApp(context, account.whatsapp) },
                        onEmailClick = { viewModel.openEmail(context, account.email) }
                    )
                }
            }
        }
    }
}
