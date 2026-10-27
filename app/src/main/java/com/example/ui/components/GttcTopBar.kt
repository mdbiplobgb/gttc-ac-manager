package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BrandNavy
import com.example.ui.theme.GrowGreen
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun GttcTopBar(
    title: String = "Gttc-Ac Manager",
    subtitle: String = "Admin Control Center",
    onAdminProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = SurfaceWhite,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // GTTC Logo with fallback
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("https://slot.gt.tc/assets/img/logo.png")
                        .error(R.drawable.ic_app_logo)
                        .crossfade(true)
                        .build(),
                    contentDescription = "GTTC Logo",
                    modifier = Modifier.size(36.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    // Admin verified shield
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFDBEAFE))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "ADMIN",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1D4ED8)
                        )
                    }
                }
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
            }

            // Admin Profile Avatar Button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BrandNavy)
                    .clickable { onAdminProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Admin Profile",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
