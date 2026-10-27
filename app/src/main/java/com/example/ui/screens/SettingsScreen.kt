package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CsvImportResult
import com.example.data.CsvManager
import com.example.model.ActivityLog
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandNavy
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GrowGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val adminName by viewModel.adminName.collectAsState()
    val adminEmail by viewModel.adminEmail.collectAsState()
    val recentLogs by viewModel.recentLogs.collectAsState()

    var showImportDialog by remember { mutableStateOf(false) }
    var importCsvInput by remember { mutableStateOf("") }
    var importResultDialog by remember { mutableStateOf<CsvImportResult?>(null) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var backupJsonContent by remember { mutableStateOf("") }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var restoreJsonInput by remember { mutableStateOf("") }
    var showLogsDialog by remember { mutableStateOf(false) }
    var showAppInfoDialog by remember { mutableStateOf(false) }
    var showAdminProfileDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Admin Profile Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAdminProfileDialog = true }
                    .testTag("admin_profile_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(BrandNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Admin",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = adminName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFDCFCE7))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF166534)
                                )
                            }
                        }
                        Text(
                            text = adminEmail,
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Details",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Section: Data & Link Management
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Data & Signup Links",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SettingsRowItem(
                        icon = Icons.Default.UploadFile,
                        title = "Import pla.csv Signup Links",
                        subtitle = "Import or update links, detect duplicates & series mapping",
                        onClick = {
                            importCsvInput = CsvManager.generateSamplePlaCsv()
                            showImportDialog = true
                        }
                    )

                    HorizontalDivider(color = BorderLight)

                    SettingsRowItem(
                        icon = Icons.Default.Share,
                        title = "Export Accounts to CSV / Excel",
                        subtitle = "Share or download structured accounts data spreadsheet",
                        onClick = { viewModel.exportAccountsToCsv(context) }
                    )

                    HorizontalDivider(color = BorderLight)

                    SettingsRowItem(
                        icon = Icons.Default.CloudDownload,
                        title = "Backup Database (JSON)",
                        subtitle = "Export complete offline snapshot of all accounts & links",
                        onClick = {
                            coroutineScope.launch {
                                backupJsonContent = viewModel.repository.exportBackupJson()
                                showBackupDialog = true
                            }
                        }
                    )

                    HorizontalDivider(color = BorderLight)

                    SettingsRowItem(
                        icon = Icons.Default.CloudUpload,
                        title = "Restore Database (JSON)",
                        subtitle = "Restore all accounts and platform URLs from backup",
                        onClick = { showRestoreDialog = true }
                    )
                }
            }
        }

        // Section: Logs & System Information
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "System & Audit",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SettingsRowItem(
                        icon = Icons.Default.History,
                        title = "Activity Log & Audit Trail",
                        subtitle = "View changes, creations, point grants, and URL updates",
                        onClick = { showLogsDialog = true }
                    )

                    HorizontalDivider(color = BorderLight)

                    SettingsRowItem(
                        icon = Icons.Default.Info,
                        title = "App Information",
                        subtitle = "Gttc-Ac Manager v1.0 • GTTC Platform slot.gt.tc",
                        onClick = { showAppInfoDialog = true }
                    )
                }
            }
        }

        // Logout Action
        item {
            Button(
                onClick = { viewModel.logoutAdmin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_logout_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFEE2E2),
                    contentColor = DangerRed
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Logout Admin Session",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Import pla.csv Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import pla.csv") },
            text = {
                Column {
                    Text(
                        text = "Paste or edit your pla.csv content below. Each line should contain platform token and URL (e.g., '1111,http://...'):",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importCsvInput,
                        onValueChange = { importCsvInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        minLines = 6
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.importCsvText(importCsvInput) { res ->
                            importResultDialog = res
                            showImportDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Text("Parse & Import")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Import Result Preview Dialog
    if (importResultDialog != null) {
        val res = importResultDialog!!
        AlertDialog(
            onDismissRequest = { importResultDialog = null },
            title = { Text("CSV Import Summary") },
            text = {
                Column {
                    Text(text = res.message, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (res.mismatchedOrDuplicate.isNotEmpty()) {
                        Text(
                            text = "Items Highlighted for Review (${res.mismatchedOrDuplicate.size}):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        res.mismatchedOrDuplicate.forEach { item ->
                            Text(
                                text = "• ${item.platform}: ${item.reviewNote}",
                                fontSize = 11.sp,
                                color = Color(0xFF92400E)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { importResultDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Text("OK")
                }
            }
        )
    }

    // Backup JSON Dialog
    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = { Text("Database Backup JSON") },
            text = {
                Column {
                    Text("Your complete backup JSON string is generated:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = backupJsonContent,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.copyToClipboard(context, backupJsonContent, "Backup JSON")
                        showBackupDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Text("Copy to Clipboard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackupDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Restore JSON Dialog
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("Restore From Backup") },
            text = {
                Column {
                    Text("Paste your exported backup JSON string below:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = restoreJsonInput,
                        onValueChange = { restoreJsonInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        placeholder = { Text("{\n  \"version\": 1,\n  ...") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val success = viewModel.repository.restoreBackupJson(restoreJsonInput.trim())
                            if (success) {
                                Toast.makeText(context, "Database restored successfully!", Toast.LENGTH_SHORT).show()
                                showRestoreDialog = false
                            } else {
                                Toast.makeText(context, "Invalid backup JSON format.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Text("Restore Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Activity Log Dialog
    if (showLogsDialog) {
        val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
        AlertDialog(
            onDismissRequest = { showLogsDialog = false },
            title = { Text("Activity Audit Log") },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (recentLogs.isEmpty()) {
                        item {
                            Text("No activity logs recorded yet.", fontSize = 12.sp, color = TextMuted)
                        }
                    } else {
                        items(recentLogs) { log ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = log.actionType,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandBlue
                                    )
                                    Text(
                                        text = dateFormat.format(Date(log.timestamp)),
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = log.description,
                                    fontSize = 12.sp,
                                    color = TextDark
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showLogsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Text("Close")
                }
            }
        )
    }

    // App Information Dialog
    if (showAppInfoDialog) {
        AlertDialog(
            onDismissRequest = { showAppInfoDialog = false },
            title = { Text("About Gttc-Ac Manager") },
            text = {
                Column {
                    Text("Gttc-Ac Manager", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    Text("Version 1.0 (Production Build)", fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Official administrative account management, signup link distributor, registration tracker, and 300-point reward allocation system.", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Platform: https://slot.gt.tc", fontSize = 12.sp, color = BrandBlue, fontWeight = FontWeight.SemiBold)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAppInfoDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Text("Done")
                }
            }
        )
    }

    // Admin Profile Dialog
    if (showAdminProfileDialog) {
        AlertDialog(
            onDismissRequest = { showAdminProfileDialog = false },
            title = { Text("Admin Profile") },
            text = {
                Column {
                    Text(text = "Name: $adminName", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Email: $adminEmail", fontSize = 13.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Role: Master System Administrator", fontSize = 12.sp, color = BrandBlue)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Permissions: Full CRUD, Link Distribution, Point Granting", fontSize = 11.sp, color = TextDark)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAdminProfileDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun SettingsRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFFEFF6FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = BrandBlue,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
            Text(text = subtitle, fontSize = 11.sp, color = TextMuted)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}
