package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PlatformItem
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandNavy
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun PlatformCard(
    item: PlatformItem,
    onOpenUrl: (String) -> Unit,
    onCopyUrl: (String) -> Unit,
    onEditUrl: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("platform_card_${item.platform}")
            .border(
                1.dp,
                if (item.isMismatchedOrDuplicate) Color(0xFFF59E0B) else BorderLight,
                RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Platform Name & Reward Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDBEAFE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = item.platform,
                            tint = BrandBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = item.platform,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = item.series,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                RewardPointsBadge(points = item.rewardPoint)
            }

            // Warning Banner if Mismatched or Duplicate
            if (item.isMismatchedOrDuplicate) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFEF3C7))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Needs Review",
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.reviewNote.ifBlank { "Needs admin review (mismatch or duplicate URL)" },
                        fontSize = 11.sp,
                        color = Color(0xFF92400E),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Signup URL display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.signupUrl.ifBlank { "No signup link configured" },
                        fontSize = 12.sp,
                        color = if (item.signupUrl.isNotBlank()) Color(0xFF2563EB) else TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (onEditUrl != null) {
                        IconButton(
                            onClick = onEditUrl,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Signup URL",
                                tint = TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Buttons: Open Signup Link & Copy Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onOpenUrl(item.signupUrl) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("open_signup_button_${item.platform}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandBlue,
                        contentColor = Color.White
                    ),
                    enabled = item.signupUrl.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = "Open Signup",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Open Link", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = { onCopyUrl(item.signupUrl) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("copy_link_button_${item.platform}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BrandNavy
                    ),
                    enabled = item.signupUrl.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Link",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Copy Link", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
