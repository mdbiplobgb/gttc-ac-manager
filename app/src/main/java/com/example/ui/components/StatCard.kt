package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderLight
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    gradientBrush: Brush? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderLight, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (gradientBrush != null) Modifier.background(gradientBrush)
                    else Modifier
                )
                .padding(16.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(iconBgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark
                )
                if (subtitle.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}
