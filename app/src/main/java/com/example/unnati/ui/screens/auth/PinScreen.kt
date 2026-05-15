package com.example.unnati.ui.screens.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unnati.ui.theme.ErrorRed
import com.example.unnati.ui.theme.LocalAppPalette
import com.example.unnati.ui.viewmodel.PinState
import com.example.unnati.ui.viewmodel.PinViewModel
import kotlinx.coroutines.delay

@Composable
fun PinScreen(
    onAdminSuccess: () -> Unit,
    onMemberView: () -> Unit,
) {
    val context = LocalContext.current
    val vm: PinViewModel = viewModel(factory = PinViewModel.Factory(context))
    val state by vm.state.collectAsState()
    val digits by vm.digits.collectAsState()
    val palette = LocalAppPalette.current

    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(state) {
        if (state == PinState.Error) {
            repeat(4) {
                shakeOffset.animateTo(10f, tween(50))
                shakeOffset.animateTo(-10f, tween(50))
            }
            shakeOffset.animateTo(0f, tween(50))
            delay(300)
            vm.resetState()
        } else if (state == PinState.Success) {
            onAdminSuccess()
        }
    }

    var settingPin by remember { mutableStateOf(false) }
    var newPinBuffer by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state == PinState.FirstLaunch) settingPin = true
        if (state == PinState.PinSet) { settingPin = false; onAdminSuccess() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.bodyBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Brush.verticalGradient(listOf(palette.primary, palette.primaryContainer))),
                contentAlignment = Alignment.Center
            ) {
                Text("U", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                "Mahila-Shakti Unnati",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.primary
            )
            Text(
                if (settingPin) "Set a 4-digit admin PIN" else "Enter PIN to continue",
                fontSize = 14.sp,
                color = palette.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            // PIN dot indicators
            val enteredCount = if (settingPin) newPinBuffer.length else digits.length
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .offset(x = shakeOffset.value.dp)
                    .padding(vertical = 8.dp)
            ) {
                repeat(4) { i ->
                    val filled = i < enteredCount
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (filled) palette.primary else Color.Transparent)
                            .border(2.dp, if (filled) palette.primary else palette.outline, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (filled) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }
                }
            }

            // Error message
            Box(Modifier.height(20.dp), contentAlignment = Alignment.Center) {
                if (state == PinState.Error) {
                    Text("Incorrect PIN. Try again.", color = ErrorRed, fontSize = 13.sp)
                }
            }

            // Glass numpad
            val keys = listOf("1","2","3","4","5","6","7","8","9","","0","⌫")
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                keys.chunked(3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        row.forEach { key ->
                            if (key.isEmpty()) {
                                Spacer(Modifier.size(76.dp))
                            } else {
                                Card(
                                    onClick = {
                                        if (key == "⌫") {
                                            if (settingPin) newPinBuffer = newPinBuffer.dropLast(1)
                                            else vm.backspace()
                                        } else {
                                            if (settingPin) {
                                                if (newPinBuffer.length < 4) {
                                                    newPinBuffer += key
                                                    if (newPinBuffer.length == 4) vm.setNewPin(newPinBuffer)
                                                }
                                            } else {
                                                vm.addDigit(key[0])
                                            }
                                        }
                                    },
                                    modifier = Modifier.size(76.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = palette.glassCardBg
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.5.dp, palette.glassBorder
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        if (key == "⌫") {
                                            Icon(
                                                Icons.Default.Backspace,
                                                contentDescription = "Delete",
                                                tint = palette.onSurfaceVariant,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        } else {
                                            Text(
                                                key,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = palette.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Member view bypass
            if (!settingPin) {
                TextButton(onClick = onMemberView) {
                    Text(
                        "Continue as Member (View Only)",
                        fontSize = 13.sp,
                        color = palette.primary
                    )
                }
            }
        }
    }
}
