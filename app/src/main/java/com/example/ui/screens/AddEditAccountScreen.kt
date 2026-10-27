package com.example.ui.screens

import android.app.DatePickerDialog
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.model.SeriesData
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandNavy
import com.example.ui.theme.GoldPoint
import com.example.ui.theme.GrowGreen
import com.example.ui.theme.PendingSlate
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

private val PRESET_AVATARS = listOf(
    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=150&auto=format&fit=crop&q=80"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAccountScreen(
    viewModel: MainViewModel,
    accountToEdit: Account?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isEdit = accountToEdit != null

    // Form fields
    var fullName by remember { mutableStateOf(accountToEdit?.fullName ?: "") }
    var selectedSeries by remember { mutableStateOf(accountToEdit?.series ?: "11-Series") }
    var selectedPlatform by remember {
        mutableStateOf(
            accountToEdit?.platform ?: SeriesData.getPlatformsForSeries(accountToEdit?.series ?: "11-Series").firstOrNull() ?: ""
        )
    }
    var maintainSeries by remember { mutableStateOf(accountToEdit?.maintainSeries ?: "") }
    var successfulGrow by remember { mutableStateOf(accountToEdit?.successfulGrow ?: false) }
    var registerDate by remember { mutableLongStateOf(accountToEdit?.registerDate ?: System.currentTimeMillis()) }
    var whatsapp by remember { mutableStateOf(accountToEdit?.whatsapp ?: "") }
    var email by remember { mutableStateOf(accountToEdit?.email ?: "") }
    var notes by remember { mutableStateOf(accountToEdit?.notes ?: "") }
    var profileImage by remember { mutableStateOf(accountToEdit?.profileImage ?: PRESET_AVATARS[0]) }

    // Dropdown expansion states
    var seriesExpanded by remember { mutableStateOf(false) }
    var platformExpanded by remember { mutableStateOf(false) }

    // Validation & Duplicate detection
    var nameError by remember { mutableStateOf(false) }
    var duplicateWarning by remember { mutableStateOf<String?>(null) }

    // Automatic platform reload when series changes
    LaunchedEffect(selectedSeries) {
        val platformsInSeries = SeriesData.getPlatformsForSeries(selectedSeries)
        if (!platformsInSeries.contains(selectedPlatform)) {
            selectedPlatform = platformsInSeries.firstOrNull() ?: ""
        }
    }

    // Duplicate account check on blur/change
    LaunchedEffect(fullName, email, whatsapp) {
        if (fullName.isNotBlank() && !isEdit) {
            val dup = viewModel.repository.checkDuplicateAccount(fullName, email, whatsapp)
            if (dup != null) {
                duplicateWarning = "Account with similar name, email, or WhatsApp already exists (${dup.fullName} in ${dup.series})."
            } else {
                duplicateWarning = null
            }
        } else {
            duplicateWarning = null
        }
    }

    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEdit) "Edit Account" else "Add New Account",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextDark
                        )
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
            // Profile Picture Selector
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Profile Avatar",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .border(2.dp, BrandBlue, CircleShape)
                    ) {
                        AsyncImage(
                            model = profileImage.ifBlank { PRESET_AVATARS[0] },
                            contentDescription = "Selected Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Select from presets or paste image URL:",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(PRESET_AVATARS) { url ->
                            val isSelected = profileImage == url
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .border(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) BrandBlue else BorderLight,
                                        CircleShape
                                    )
                                    .clickable { profileImage = url }
                            ) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = "Avatar preset",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Duplicate warning banner if detected
            if (duplicateWarning != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = duplicateWarning ?: "",
                            fontSize = 12.sp,
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Core Account Fields Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Account Details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Full Name
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            if (it.isNotBlank()) nameError = false
                        },
                        label = { Text("Full Name *") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = BrandBlue)
                        },
                        isError = nameError,
                        supportingText = {
                            if (nameError) Text("Full name is required", color = Color(0xFFDC2626))
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_full_name"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlue,
                            unfocusedBorderColor = BorderLight
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Choose Series Dropdown
                    ExposedDropdownMenuBox(
                        expanded = seriesExpanded,
                        onExpandedChange = { seriesExpanded = !seriesExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedSeries,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Choose Series *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = seriesExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .testTag("select_series_dropdown"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandBlue,
                                unfocusedBorderColor = BorderLight
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = seriesExpanded,
                            onDismissRequest = { seriesExpanded = false }
                        ) {
                            SeriesData.getSeriesNames().forEach { seriesName ->
                                DropdownMenuItem(
                                    text = { Text(seriesName, fontWeight = FontWeight.SemiBold) },
                                    onClick = {
                                        selectedSeries = seriesName
                                        seriesExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Choose Platform Dropdown (Restricted to the 3 platforms of the chosen series!)
                    val availablePlatforms = SeriesData.getPlatformsForSeries(selectedSeries)
                    ExposedDropdownMenuBox(
                        expanded = platformExpanded,
                        onExpandedChange = { platformExpanded = !platformExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedPlatform,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Choose Platform (3 in $selectedSeries) *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = platformExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .testTag("select_platform_dropdown"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandBlue,
                                unfocusedBorderColor = BorderLight
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = platformExpanded,
                            onDismissRequest = { platformExpanded = false }
                        ) {
                            availablePlatforms.forEach { platformName ->
                                DropdownMenuItem(
                                    text = { Text(platformName, fontSize = 13.sp) },
                                    onClick = {
                                        selectedPlatform = platformName
                                        platformExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Maintain Series
                    OutlinedTextField(
                        value = maintainSeries,
                        onValueChange = { maintainSeries = it },
                        label = { Text("Maintain Series (Optional)") },
                        placeholder = { Text("e.g. Primary Team A, Special Unit") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_maintain_series"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlue,
                            unfocusedBorderColor = BorderLight
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Growth & Reward Points Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Registration Status & Reward",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Successful Grow Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (successfulGrow) Color(0xFFDCFCE7) else Color(0xFFF1F5F9))
                            .border(
                                1.dp,
                                if (successfulGrow) Color(0xFF86EFAC) else BorderLight,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (successfulGrow) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
                                    contentDescription = null,
                                    tint = if (successfulGrow) GrowGreen else PendingSlate,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (successfulGrow) "Successful Grow: YES" else "Successful Grow: NO",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (successfulGrow) Color(0xFF14532D) else TextDark
                                )
                            }
                            Text(
                                text = if (successfulGrow) "Verified registration completed. 300 points auto-awarded." else "Awaiting verification. Stored as Pending (0 Pts).",
                                fontSize = 11.sp,
                                color = if (successfulGrow) Color(0xFF166534) else TextMuted
                            )
                        }

                        Switch(
                            checked = successfulGrow,
                            onCheckedChange = { successfulGrow = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GrowGreen
                            ),
                            modifier = Modifier.testTag("toggle_successful_grow")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Earn Point Badge (Auto 300 pts)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (successfulGrow) Color(0xFFFEF3C7) else Color(0xFFF8FAFC))
                            .border(1.dp, if (successfulGrow) GoldPoint else BorderLight, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                tint = if (successfulGrow) GoldPoint else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Earn Point",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextDark
                                )
                                Text(
                                    text = "Auto 300 after verified signup",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Text(
                            text = if (successfulGrow) "300 Pts" else "0 Pts",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (successfulGrow) Color(0xFFB45309) else TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Register Date Picker
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                            .clickable {
                                val cal = Calendar.getInstance().apply { timeInMillis = registerDate }
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        val newCal = Calendar.getInstance().apply {
                                            set(year, month, dayOfMonth)
                                        }
                                        registerDate = newCal.timeInMillis
                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Date",
                                tint = BrandBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = "Register Date", fontSize = 12.sp, color = TextMuted)
                                Text(
                                    text = dateFormat.format(Date(registerDate)),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                            }
                        }
                        Text(text = "Change", fontSize = 12.sp, color = BrandBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contact & Notes Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Contact & Notes",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // WhatsApp Number
                    OutlinedTextField(
                        value = whatsapp,
                        onValueChange = { whatsapp = it },
                        label = { Text("WhatsApp Number") },
                        placeholder = { Text("+8801712345678") },
                        leadingIcon = {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = GrowGreen)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_whatsapp"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlue,
                            unfocusedBorderColor = BorderLight
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Email Address
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        placeholder = { Text("user@example.com") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = BrandBlue)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_email"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlue,
                            unfocusedBorderColor = BorderLight
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Notes
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes & Verification Details") },
                        placeholder = { Text("e.g. Completed signup link, KYC submitted...") },
                        leadingIcon = {
                            Icon(Icons.Default.Notes, contentDescription = null, tint = TextMuted)
                        },
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_notes"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlue,
                            unfocusedBorderColor = BorderLight
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save & Cancel Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(text = "Cancel", fontSize = 14.sp)
                }

                Button(
                    onClick = {
                        if (fullName.isBlank()) {
                            nameError = true
                            return@Button
                        }
                        val account = Account(
                            id = accountToEdit?.id ?: UUID.randomUUID().toString(),
                            fullName = fullName.trim(),
                            profileImage = profileImage.trim(),
                            whatsapp = whatsapp.trim(),
                            email = email.trim(),
                            series = selectedSeries,
                            platform = selectedPlatform,
                            maintainSeries = maintainSeries.trim(),
                            successfulGrow = successfulGrow,
                            earnPoint = if (successfulGrow) 300 else 0,
                            registerDate = registerDate,
                            status = if (successfulGrow) "Grow" else "Pending",
                            notes = notes.trim()
                        )
                        viewModel.saveAccount(account, isNew = !isEdit) {
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .testTag("save_account_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandBlue,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isEdit) "Update Account" else "Save Account (+${if (successfulGrow) 300 else 0} Pts)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
