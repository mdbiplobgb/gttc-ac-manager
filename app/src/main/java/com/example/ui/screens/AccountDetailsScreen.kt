package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Account
import com.example.ui.components.RewardPointsBadge
import com.example.ui.components.SeriesChipBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandNavy
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GrowGreen
import com.example.ui.theme.PendingSlate
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(
    account: Account,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault())

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Account Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.openEditAccount(account) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = BrandBlue)
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Account", tint = DangerRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Profile Card Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(3.dp, BrandBlue, CircleShape)
                            .background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (account.profileImage.isNotBlank()) {
                            AsyncImage(
                                model = account.profileImage,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            val initial = account.fullName.firstOrNull()?.uppercaseChar()?.toString() ?: "U"
                            Text(
                                text = initial,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = account.fullName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadge(status = account.status)
                        RewardPointsBadge(points = account.earnPoint)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick Action Buttons (WhatsApp, Email, Toggle Grow)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (account.whatsapp.isNotBlank()) {
                            Button(
                                onClick = { viewModel.openWhatsApp(context, account.whatsapp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (account.email.isNotBlank()) {
                            Button(
                                onClick = { viewModel.openEmail(context, account.email) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                            ) {
                                Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Email", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Toggle Status Button
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (account.successfulGrow) Color(0xFFFEF3C7) else Color(0xFFDCFCE7)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (account.successfulGrow) "Status: Completed (Grow)" else "Status: Pending Verification",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextDark
                        )
                        Text(
                            text = if (account.successfulGrow) "Tap to move back to Pending (0 Pts)" else "Tap to mark as Successful Grow (+300 Pts)",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Button(
                        onClick = { viewModel.toggleGrowStatus(account) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (account.successfulGrow) Color(0xFF64748B) else Color(0xFF16A34A),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (account.successfulGrow) "Set Pending" else "Verify Grow (+300)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Information Grid
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Registration Specifications",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    DetailItem(
                        icon = Icons.Default.Layers,
                        label = "Series",
                        value = account.series
                    )

                    DetailItem(
                        icon = Icons.Default.Language,
                        label = "Platform",
                        value = account.platform
                    )

                    DetailItem(
                        icon = Icons.Default.Layers,
                        label = "Maintain Series",
                        value = account.maintainSeries.ifBlank { "Not Specified" }
                    )

                    DetailItem(
                        icon = if (account.successfulGrow) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
                        label = "Successful Grow",
                        value = if (account.successfulGrow) "Yes (Completed)" else "No (Pending)"
                    )

                    DetailItem(
                        icon = Icons.Default.Stars,
                        label = "Earn Point",
                        value = "${account.earnPoint} Points"
                    )

                    DetailItem(
                        icon = Icons.Default.CalendarToday,
                        label = "Registration Date",
                        value = dateFormat.format(Date(account.registerDate))
                    )

                    if (account.whatsapp.isNotBlank()) {
                        DetailItem(
                            icon = Icons.AutoMirrored.Filled.Chat,
                            label = "WhatsApp",
                            value = account.whatsapp
                        )
                    }

                    if (account.email.isNotBlank()) {
                        DetailItem(
                            icon = Icons.Default.Email,
                            label = "Email Address",
                            value = account.email
                        )
                    }

                    if (account.notes.isNotBlank()) {
                        DetailItem(
                            icon = Icons.Default.Notes,
                            label = "Notes",
                            value = account.notes
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Delete Account Action
            OutlinedButton(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("delete_account_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Delete Account Record", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Account") },
            text = { Text("Are you sure you want to permanently delete '${account.fullName}'? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.deleteAccount(account) {
                            onBack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BrandBlue,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontSize = 11.sp, color = TextMuted)
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
        }
    }
}
