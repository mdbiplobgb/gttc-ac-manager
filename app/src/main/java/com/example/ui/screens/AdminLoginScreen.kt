package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandNavy
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun AdminLoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("admin@gttc.manager") }
    var pin by remember { mutableStateOf("778899") }
    var errorText by remember { mutableStateOf("") }

    val gradientBg = Brush.verticalGradient(
        colors = listOf(
            BrandNavy,
            Color(0xFF003E99),
            BrandBlue
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradientBg)
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White)
                    .border(2.dp, ElectricCyan, RoundedCornerShape(22.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("https://slot.gt.tc/assets/img/logo.png")
                        .error(R.drawable.ic_app_logo)
                        .crossfade(true)
                        .build(),
                    contentDescription = "GTTC Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Gttc-Ac Manager",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Text(
                text = "Admin Portal & Account Control System",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFFBAE6FD)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Admin Shield",
                            tint = BrandBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Admin Authentication",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Admin Email") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = "Email", tint = BrandBlue)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_email_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlue,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = pin,
                        onValueChange = { pin = it },
                        label = { Text("Admin PIN / Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "PIN", tint = BrandBlue)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            if (pin.isNotBlank()) onLoginSuccess() else errorText = "Please enter Admin PIN"
                        }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_pin_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlue,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        )
                    )

                    if (errorText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorText,
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (pin.isNotBlank()) {
                                onLoginSuccess()
                            } else {
                                errorText = "Please enter Admin PIN"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("admin_login_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Login to Admin Dashboard",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(
                        onClick = {
                            email = "admin@gttc.manager"
                            pin = "778899"
                            onLoginSuccess()
                        }
                    ) {
                        Text(
                            text = "Quick Demo Access (PIN: 778899)",
                            color = BrandBlue,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "slot.gt.tc • Official System",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}
