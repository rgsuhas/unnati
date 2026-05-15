package com.example.unnati.ui.screens.savings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unnati.ui.components.GlassCard
import com.example.unnati.ui.theme.LocalAppPalette
import com.example.unnati.ui.theme.SuccessGreen
import com.example.unnati.ui.theme.WarningAmber
import com.example.unnati.ui.viewmodel.MemberSavingsRow
import com.example.unnati.ui.viewmodel.SavingsEntryViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsEntryScreen(
    vm: SavingsEntryViewModel,
    onBack: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val rows by vm.rows.collectAsState()
    val paidCount by vm.paidCount.collectAsState()
    val total by vm.totalCollected.collectAsState()
    val selectedWeek by vm.selectedWeek.collectAsState()
    val saved by vm.saved.collectAsState()

    val currency = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(saved) { if (saved) onBack() }

    if (showDatePicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = selectedWeek)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { vm.selectedWeek.value = it }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state) }
    }

    val progress = if (rows.isEmpty()) 0f else paidCount.toFloat() / rows.size

    Scaffold(
        containerColor = palette.bodyBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Weekly Savings",
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
                    IconButton(onClick = { vm.saveAll() }) {
                        Icon(Icons.Default.Check, contentDescription = "Save",
                            tint = palette.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = palette.glassTopBarBg,
                )
            )
        },
        bottomBar = {
            // Glass bottom bar
            Surface(
                color = palette.glassTopBarBg,
                shadowElevation = 0.dp,
                border = BorderStroke(1.5.dp, palette.glassBorder),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            ) {
                Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("$paidCount / ${rows.size} paid",
                            style = MaterialTheme.typography.labelMedium,
                            color = palette.onSurfaceVariant)
                        Text(currency.format(total),
                            fontWeight = FontWeight.Bold, fontSize = 16.sp,
                            color = palette.primary)
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50.dp)),
                        color = SuccessGreen,
                        trackColor = palette.surfaceContainer,
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { vm.saveAll() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = palette.buttonContainer,
                            contentColor = palette.buttonContent,
                        )
                    ) {
                        Text("Save All Entries", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                // Week picker glass card
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Week of",
                            style = MaterialTheme.typography.labelMedium,
                            color = palette.onSurfaceVariant)
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                sdf.format(Date(selectedWeek)),
                                style = MaterialTheme.typography.titleSmall,
                                color = palette.onSurface,
                            )
                            FilledTonalButton(
                                onClick = { showDatePicker = true },
                                shape = RoundedCornerShape(50.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = palette.primary.copy(alpha = 0.10f),
                                    contentColor = palette.primary,
                                )
                            ) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null,
                                    modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Change", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }

            items(rows, key = { it.member.id }) { row ->
                SavingsRowCard(row = row, currency = currency,
                    onToggle = { vm.toggleStatus(row.member.id) })
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun SavingsRowCard(
    row: MemberSavingsRow,
    currency: NumberFormat,
    onToggle: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val isPaid = row.status == "PAID"
    val initials = row.member.name.split(" ")
        .take(2).joinToString("") { it.firstOrNull()?.uppercase() ?: "" }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(palette.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, color = palette.primary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(row.member.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = palette.onSurface)
                Text(currency.format(row.amount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.onSurfaceVariant, fontSize = 13.sp)
            }

            // PAID / PENDING pill buttons
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = { if (!isPaid) onToggle() },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isPaid) SuccessGreen.copy(alpha = 0.12f) else Color.Transparent,
                        contentColor = SuccessGreen,
                    ),
                    border = BorderStroke(1.5.dp, SuccessGreen.copy(alpha = if (isPaid) 1f else 0.4f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text("PAID", style = MaterialTheme.typography.labelMedium, fontSize = 11.sp)
                }
                OutlinedButton(
                    onClick = { if (isPaid) onToggle() },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (!isPaid) WarningAmber.copy(alpha = 0.12f) else Color.Transparent,
                        contentColor = WarningAmber,
                    ),
                    border = BorderStroke(1.5.dp, WarningAmber.copy(alpha = if (!isPaid) 1f else 0.4f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text("PEND", style = MaterialTheme.typography.labelMedium, fontSize = 11.sp)
                }
            }
        }
    }
}
