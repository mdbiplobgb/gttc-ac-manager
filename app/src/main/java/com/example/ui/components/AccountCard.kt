package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Account
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BrandNavy
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AccountCard(
    account: Account,
    onClick: () -> Unit,
    onWhatsAppClick: (() -> Unit)? = null,
    onEmailClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(account.registerDate))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("account_card_${account.id}")
            .border(1.dp, BorderLight, RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Picture / Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    if (account.profileImage.isNotBlank()) {
                        AsyncImage(
                            model = account.profileImage,
                            contentDescription = "Profile picture of ${account.fullName}",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Fallback monogram
                        val initial = account.fullName.firstOrNull()?.uppercaseChar()?.toString() ?: "U"
                        Text(
                            text = initial,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name, Register Date
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = account.fullName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Reg: $formattedDate",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                // Status Badge (Grow or Pending)
                StatusBadge(status = account.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Series, Platform, and Earn Points
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SeriesChipBadge(series = account.series)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = account.platform,
                            fontSize = 11.sp,
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                RewardPointsBadge(points = account.earnPoint)
            }

            // Quick contact row if phone/email present
            if (account.whatsapp.isNotBlank() || account.email.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (account.whatsapp.isNotBlank() && onWhatsAppClick != null) {
                            IconButton(
                                onClick = onWhatsAppClick,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDCFCE7))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Chat,
                                    contentDescription = "WhatsApp ${account.whatsapp}",
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        if (account.email.isNotBlank() && onEmailClick != null) {
                            IconButton(
                                onClick = onEmailClick,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDBEAFE))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email ${account.email}",
                                    tint = Color(0xFF1D4ED8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onClick() }
                    ) {
                        Text(
                            text = "Details",
                            fontSize = 12.sp,
                            color = Color(0xFF0052CC),
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "View Details",
                            tint = Color(0xFF0052CC),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
