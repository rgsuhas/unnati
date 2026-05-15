package com.example.unnati.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import com.example.unnati.data.PrefsKeys
import com.example.unnati.data.appDataStore
import com.example.unnati.ui.components.GlassCard
import com.example.unnati.ui.theme.ErrorRed
import com.example.unnati.ui.theme.LocalAppPalette
import com.example.unnati.ui.theme.Palettes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var interestRate    by remember { mutableStateOf("2.0") }
    var weeklyAmount    by remember { mutableStateOf("100.0") }
    var selectedPalette by remember { mutableStateOf("Violet & Saffron") }
    var groupName       by remember { mutableStateOf("") }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val prefs = context.appDataStore.data.first()
        interestRate    = (prefs[PrefsKeys.INTEREST_RATE] ?: 2.0).toString()
        weeklyAmount    = (prefs[PrefsKeys.WEEKLY_AMOUNT] ?: 100.0).toString()
        selectedPalette = prefs[PrefsKeys.PALETTE_NAME] ?: "Violet & Saffron"
        groupName       = prefs[PrefsKeys.GROUP_NAME] ?: ""
    }

    fun save() = scope.launch {
        context.appDataStore.edit { prefs ->
            interestRate.toDoubleOrNull()?.let { prefs[PrefsKeys.INTEREST_RATE] = it }
            weeklyAmount.toDoubleOrNull()?.let { prefs[PrefsKeys.WEEKLY_AMOUNT] = it }
            prefs[PrefsKeys.PALETTE_NAME] = selectedPalette
            prefs[PrefsKeys.GROUP_NAME] = groupName
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Lock Screen?") },
            text = { Text("You'll need to enter the admin PIN again to continue.") },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; onLogout() }) {
                    Text("Lock", color = ErrorRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Group name initials for avatar
    val initials = groupName.split(" ").take(2)
        .joinToString("") { it.firstOrNull()?.uppercase() ?: "" }
        .ifEmpty { "SHG" }

    Scaffold(
        containerColor = palette.bodyBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile & Settings",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                        color = palette.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                            tint = palette.onSurfaceVariant)
                    }
                },
                actions = {
                    TextButton(onClick = { save() }) {
                        Text("Save", color = palette.primary, fontWeight = FontWeight.SemiBold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = palette.glassTopBarBg,
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── Group avatar + name ────────────────────────────────────────
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(palette.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initials,
                            color = palette.primary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold)
                    }
                    OutlinedTextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        label = { Text("Group / SHG Name") },
                        leadingIcon = { Icon(Icons.Default.Group, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                    )
                }
            }

            // ── Group defaults ─────────────────────────────────────────────
            SettingsSection("Group Defaults") {
                OutlinedTextField(
                    value = interestRate,
                    onValueChange = { interestRate = it },
                    label = { Text("Default Interest Rate (% / month)") },
                    leadingIcon = { Icon(Icons.Default.Percent, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = weeklyAmount,
                    onValueChange = { weeklyAmount = it },
                    label = { Text("Weekly Savings Amount (₹)") },
                    leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                )
            }

            // ── Appearance ─────────────────────────────────────────────────
            SettingsSection("Appearance") {
                Text("Color Palette",
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.onSurfaceVariant)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Palettes.all.keys.forEach { name ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = selectedPalette == name,
                                onClick = { selectedPalette = name },
                                colors = RadioButtonDefaults.colors(selectedColor = palette.primary)
                            )
                            Text(name, style = MaterialTheme.typography.bodyMedium,
                                color = palette.onSurface)
                        }
                    }
                }
                Text(
                    "Restart the app to apply a new palette.",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.onSurfaceVariant,
                )
            }

            // ── Security ───────────────────────────────────────────────────
            SettingsSection("Security") {
                OutlinedButton(
                    onClick = { /* PIN change flow */ },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Change Admin PIN")
                }

                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorRed.copy(alpha = 0.12f),
                        contentColor = ErrorRed,
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp),
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Lock / Logout", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    val palette = LocalAppPalette.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = palette.primary,
            letterSpacing = 1.5.sp,
            fontWeight = FontWeight.Bold,
        )
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                content()
            }
        }
    }
}
