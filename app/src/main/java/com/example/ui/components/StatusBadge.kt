package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldPoint
import com.example.ui.theme.GoldPointContainer
import com.example.ui.theme.GoldPointText
import com.example.ui.theme.GrowGreen
import com.example.ui.theme.GrowGreenContainer
import com.example.ui.theme.GrowGreenText
import com.example.ui.theme.PendingSlate
import com.example.ui.theme.PendingSlateContainer
import com.example.ui.theme.PendingSlateText

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val isGrow = status.equals("Grow", ignoreCase = true)
    val bgColor = if (isGrow) GrowGreenContainer else PendingSlateContainer
    val textColor = if (isGrow) GrowGreenText else PendingSlateText
    val borderColor = if (isGrow) GrowGreen.copy(alpha = 0.4f) else PendingSlate.copy(alpha = 0.3f)
    val icon = if (isGrow) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty
    val label = if (isGrow) "Grow" else "Pending"

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = textColor,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RewardPointsBadge(
    points: Int,
    modifier: Modifier = Modifier
) {
    val isRewarded = points > 0
    val bgColor = if (isRewarded) GoldPointContainer else Color(0xFFF1F5F9)
    val textColor = if (isRewarded) GoldPointText else Color(0xFF64748B)
    val borderColor = if (isRewarded) GoldPoint.copy(alpha = 0.5f) else Color(0xFFE2E8F0)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Stars,
            contentDescription = "Reward Points",
            tint = if (isRewarded) GoldPoint else Color(0xFF94A3B8),
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$points Pts",
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SeriesChipBadge(
    series: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFEFF6FF))
            .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = series,
            color = Color(0xFF1D4ED8),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
