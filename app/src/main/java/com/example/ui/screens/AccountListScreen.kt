package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SeriesData
import com.example.ui.components.AccountCard
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandNavy
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.SortOption

@Composable
fun AccountListScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accounts by viewModel.filteredAccounts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val seriesFilter by viewModel.seriesFilter.collectAsState()
    val platformFilter by viewModel.platformFilter.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()

    var showSortMenu by remember { mutableStateOf(false) }
    var showPlatformMenu by remember { mutableStateOf(false) }

    val allSeriesList = remember { listOf("All") + SeriesData.getSeriesNames() }
    val availablePlatforms = remember(seriesFilter) {
        if (seriesFilter == "All") {
            listOf("All") + SeriesData.ALL_INITIAL_PLATFORMS_MAP.keys.sorted()
        } else {
            listOf("All") + SeriesData.getPlatformsForSeries(seriesFilter)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF8FAFC),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddAccount() },
                containerColor = BrandBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 70.dp)
                    .testTag("account_list_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Account")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar & Filter Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                // Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = { Text("Search by name, email, phone...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_account_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandBlue,
                        unfocusedBorderColor = BorderLight,
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Series Filter Horizontal Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    allSeriesList.forEach { s ->
                        val isSelected = seriesFilter == s
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) BrandBlue else Color(0xFFF1F5F9))
                                .border(
                                    1.dp,
                                    if (isSelected) BrandBlue else BorderLight,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    viewModel.seriesFilter.value = s
                                    // Reset platform filter if it doesn't belong to the new series
                                    if (s != "All" && platformFilter != "All") {
                                        val validPlatforms = SeriesData.getPlatformsForSeries(s)
                                        if (!validPlatforms.contains(platformFilter)) {
                                            viewModel.platformFilter.value = "All"
                                        }
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = s,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Second Filter Row: Platform dropdown, Status toggle, Sort dropdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status Filter Chips (All, Grow, Pending)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("All", "Grow", "Pending").forEach { st ->
                            val isSelected = statusFilter == st
                            val bgColor = when {
                                isSelected && st == "Grow" -> Color(0xFF16A34A)
                                isSelected && st == "Pending" -> Color(0xFF64748B)
                                isSelected -> BrandNavy
                                else -> Color(0xFFF1F5F9)
                            }
                            val textColor = if (isSelected) Color.White else Color(0xFF475569)

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bgColor)
                                    .clickable { viewModel.statusFilter.value = st }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = st,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = textColor
                                )
                            }
                        }
                    }

                    // Platform Filter Dropdown & Sort Menu
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Platform Button
                        Box {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFEFF6FF))
                                    .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(8.dp))
                                    .clickable { showPlatformMenu = true }
                                    .padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.FilterList, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (platformFilter == "All") "Platform" else platformFilter.substringBefore("."),
                                    fontSize = 11.sp,
                                    color = BrandBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            DropdownMenu(
                                expanded = showPlatformMenu,
                                onDismissRequest = { showPlatformMenu = false }
                            ) {
                                availablePlatforms.forEach { p ->
                                    DropdownMenuItem(
                                        text = { Text(p, fontSize = 13.sp) },
                                        onClick = {
                                            viewModel.platformFilter.value = p
                                            showPlatformMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Sort Button
                        Box {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                                    .clickable { showSortMenu = true }
                                    .padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Sort, contentDescription = null, tint = TextDark, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Sort",
                                    fontSize = 11.sp,
                                    color = TextDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                SortOption.values().forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                option.label,
                                                fontSize = 13.sp,
                                                fontWeight = if (sortOption == option) FontWeight.Bold else FontWeight.Normal,
                                                color = if (sortOption == option) BrandBlue else TextDark
                                            )
                                        },
                                        onClick = {
                                            viewModel.sortOption.value = option
                                            showSortMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Results count banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${accounts.size} Accounts Found",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted
                )
                if (seriesFilter != "All" || platformFilter != "All" || statusFilter != "All" || searchQuery.isNotBlank()) {
                    Text(
                        text = "Reset Filters",
                        fontSize = 12.sp,
                        color = BrandBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            viewModel.searchQuery.value = ""
                            viewModel.seriesFilter.value = "All"
                            viewModel.platformFilter.value = "All"
                            viewModel.statusFilter.value = "All"
                        }
                    )
                }
            }

            // Accounts List
            if (accounts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No accounts match current filters",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.openAddAccount() },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add New Account", fontSize = 13.sp)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(accounts, key = { it.id }) { account ->
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
}
