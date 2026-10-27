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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.PlatformItem
import com.example.model.SeriesData
import com.example.model.SeriesDefinition
import com.example.ui.components.AccountCard
import com.example.ui.components.PlatformCard
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.GoldPoint
import com.example.ui.theme.GrowGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.MainViewModel

@Composable
fun SeriesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedSeries by viewModel.selectedSeriesDetails.collectAsState()
    val allAccounts by viewModel.allAccounts.collectAsState()
    val allPlatforms by viewModel.allPlatforms.collectAsState()
    val editingPlatform by viewModel.editingPlatform.collectAsState()

    var editUrlInput by remember { mutableStateOf("") }

    if (selectedSeries != null) {
        // Detailed Platform Screen for the selected Series
        val currentSeriesName = selectedSeries ?: "11-Series"
        val seriesDef = SeriesData.SERIES_LIST.find { it.name.equals(currentSeriesName, ignoreCase = true) }
            ?: SeriesData.SERIES_LIST[0]

        val platformsInSeries = allPlatforms.filter { it.series.equals(currentSeriesName, ignoreCase = true) }
        val accountsInSeries = allAccounts.filter { it.series.equals(currentSeriesName, ignoreCase = true) }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC)),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Header Bar
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(seriesDef.gradientColors))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.closeSeriesDetails() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = seriesDef.name,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Exactly 3 Platforms • ${accountsInSeries.size} Registered Accounts",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Summary Row in Series
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val growCount = accountsInSeries.count { it.status.equals("Grow", ignoreCase = true) }
                            val totalPoints = accountsInSeries.sumOf { it.earnPoint }

                            SeriesStatMini(
                                label = "Total Accounts",
                                value = accountsInSeries.size.toString(),
                                modifier = Modifier.weight(1f)
                            )
                            SeriesStatMini(
                                label = "Grow (Done)",
                                value = growCount.toString(),
                                modifier = Modifier.weight(1f)
                            )
                            SeriesStatMini(
                                label = "Earned Pts",
                                value = "$totalPoints",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Platforms Section Header
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Series Platforms (${seriesDef.platforms.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "300 Pts / verified signup",
                        fontSize = 11.sp,
                        color = GoldPoint,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Exactly 3 Platform Cards
            val platformsToDisplay = if (platformsInSeries.isNotEmpty()) {
                platformsInSeries
            } else {
                seriesDef.platforms.map { pName ->
                    PlatformItem(
                        platform = pName,
                        series = seriesDef.name,
                        signupUrl = SeriesData.getDefaultSignupUrl(pName),
                        rewardPoint = 300
                    )
                }
            }

            items(platformsToDisplay) { platformItem ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    PlatformCard(
                        item = platformItem,
                        onOpenUrl = { url -> viewModel.openBrowser(context, url) },
                        onCopyUrl = { url -> viewModel.copyToClipboard(context, url) },
                        onEditUrl = {
                            editUrlInput = platformItem.signupUrl
                            viewModel.setEditingPlatform(platformItem)
                        }
                    )
                }
            }

            // Accounts in this Series Section
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Accounts in ${seriesDef.name} (${accountsInSeries.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }
            }

            if (accountsInSeries.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No accounts registered in ${seriesDef.name} yet.",
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            } else {
                items(accountsInSeries) { account ->
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
    } else {
        // Main Series Overview: All 8 Series Cards
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC)),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Series Management",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "8 Distinct Series • Each contains exactly 3 platforms",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            items(SeriesData.SERIES_LIST) { series ->
                val seriesAccounts = allAccounts.filter { it.series.equals(series.name, ignoreCase = true) }
                val totalAccounts = seriesAccounts.size
                val completedAccounts = seriesAccounts.count { it.status.equals("Grow", ignoreCase = true) }
                val totalPoints = seriesAccounts.sumOf { it.earnPoint }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("series_card_${series.name}")
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { viewModel.openSeriesDetails(series.name) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    Column {
                        // Colored Series Top Accent
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .background(Brush.horizontalGradient(series.gradientColors))
                        )

                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(Brush.horizontalGradient(series.gradientColors)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Layers,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = series.name,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextDark
                                        )
                                        Text(
                                            text = "${series.platforms.size} Platforms",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Open",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandBlue
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "Open Series",
                                        tint = BrandBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 3 Metrics Display
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "Total Accounts", fontSize = 10.sp, color = TextMuted)
                                    Text(text = "$totalAccounts", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                }
                                Box(modifier = Modifier.width(1.dp).height(24.dp).background(BorderLight))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "Completed Grow", fontSize = 10.sp, color = TextMuted)
                                    Text(text = "$completedAccounts", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GrowGreen)
                                }
                                Box(modifier = Modifier.width(1.dp).height(24.dp).background(BorderLight))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "Earned Points", fontSize = 10.sp, color = TextMuted)
                                    Text(text = "$totalPoints", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoldPoint)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Platforms Preview Pills
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                series.platforms.forEach { platform ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFEFF6FF))
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = platform,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF1D4ED8)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Platform URL Dialog
    if (editingPlatform != null) {
        val platform = editingPlatform
        AlertDialog(
            onDismissRequest = { viewModel.setEditingPlatform(null) },
            title = { Text("Edit Signup URL") },
            text = {
                Column {
                    Text(
                        text = "Platform: ${platform?.platform} (${platform?.series})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editUrlInput,
                        onValueChange = { editUrlInput = it },
                        label = { Text("Signup URL") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        platform?.let {
                            viewModel.updateSignupUrl(it.platform, editUrlInput.trim())
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Text("Save URL")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setEditingPlatform(null) }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SeriesStatMini(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.18f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.85f))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
