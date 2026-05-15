package com.example.unnati.ui.screens.members

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unnati.data.entity.SavingsEntry
import com.example.unnati.ui.components.GlassCard
import com.example.unnati.ui.theme.*
import com.example.unnati.ui.viewmodel.MemberProfileViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberProfileScreen(
    vm: MemberProfileViewModel,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    onLoanDetail: (Int) -> Unit,
    onLogSaving: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val member by vm.member.collectAsState()
    val totalSavings by vm.totalSavings.collectAsState()
    val activeLoan by vm.activeLoan.collectAsState()
    val creditScore by vm.creditScore.collectAsState()
    val currentWeek by vm.currentWeekEntry.collectAsState()
    val entries by vm.savingsEntries.collectAsState()

    val currency = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    val initials = member?.name?.split(" ")
        ?.take(2)?.joinToString("") { it.firstOrNull()?.uppercase() ?: "" } ?: "?"
    val scoreColor = when {
        creditScore >= 70 -> SuccessGreen
        creditScore >= 40 -> WarningAmber
        else -> ErrorRed
    }

    Scaffold(
        containerColor = palette.bodyBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Member Profile",
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
                    member?.let { m ->
                        IconButton(onClick = { onEdit(m.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit",
                                tint = palette.onSurfaceVariant)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = palette.glassTopBarBg,
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                // Hero header card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(palette.surfaceContainer, palette.primaryContainer.copy(alpha = 0.20f))
                            )
                        )
                        .padding(20.dp)
                ) {
                    // Decorative orb
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(palette.secondaryContainer.copy(alpha = 0.20f))
                            .align(Alignment.TopEnd)
                            .offset(x = 20.dp, y = (-20).dp)
                    )
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(palette.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(initials, color = palette.primary,
                                    fontSize = 26.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(member?.name ?: "—", color = palette.onSurface,
                                    style = MaterialTheme.typography.titleMedium)
                                Row(verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Call, contentDescription = null,
                                        tint = palette.onSurfaceVariant, modifier = Modifier.size(14.dp))
                                    Text(member?.phone ?: "", color = palette.onSurfaceVariant, fontSize = 13.sp)
                                }
                                member?.joinDate?.let {
                                    Row(verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Default.CalendarMonth, contentDescription = null,
                                            tint = palette.onSurfaceVariant, modifier = Modifier.size(14.dp))
                                        Text("Joined ${sdf.format(Date(it))}",
                                            color = palette.onSurfaceVariant, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatPill("Total Savings", currency.format(totalSavings), palette.primary)
                            StatPill("Credit Score", "$creditScore/100", scoreColor)
                        }
                    }
                }
            }

            item {
                // Current week status glass card
                val isPaid = currentWeek?.status == "PAID"
                val hasPending = currentWeek?.status == "PENDING"
                val statusColor = when {
                    isPaid -> SuccessGreen
                    hasPending -> WarningAmber
                    else -> palette.onSurfaceVariant
                }
                GlassCard(modifier = Modifier.fillMaxWidth(), onClick = onLogSaving) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(statusColor.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (isPaid) Icons.Default.CheckCircle else Icons.Default.Schedule,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("This Week", fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.titleSmall)
                            Text(
                                when {
                                    isPaid -> "Paid — ${currency.format(currentWeek?.amount ?: 0.0)}"
                                    hasPending -> "Pending"
                                    else -> "No entry yet — tap to log"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null,
                            tint = palette.onSurfaceVariant)
                    }
                }
            }

            // Active loan glass card
            activeLoan?.let { loan ->
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth(), onClick = { onLoanDetail(loan.id) }) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(palette.primary.copy(alpha = 0.10f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AccountBalance, contentDescription = null,
                                        tint = palette.primary, modifier = Modifier.size(18.dp))
                                }
                                Spacer(Modifier.width(10.dp))
                                Text("Active Loan", fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                                Text("View →", style = MaterialTheme.typography.labelMedium,
                                    color = palette.primary)
                            }
                            Text("Principal: ${currency.format(loan.principal)}",
                                style = MaterialTheme.typography.bodyMedium)
                            Text("${loan.interestRate}%/month · ${loan.durationMonths} months",
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.onSurfaceVariant, fontSize = 13.sp)
                        }
                    }
                }
            }

            item {
                Text("Contribution History",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 4.dp))
            }

            if (entries.isEmpty()) {
                item {
                    Text("No savings entries yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.onSurfaceVariant)
                }
            } else {
                items(entries.take(10)) { entry -> SavingsEntryRow(entry, currency, sdf) }
                if (entries.size > 10) {
                    item {
                        Text("+ ${entries.size - 10} more entries",
                            style = MaterialTheme.typography.labelMedium,
                            color = palette.primary,
                            modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun StatPill(label: String, value: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.10f), RoundedCornerShape(50.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Column {
            Text(label, fontSize = 10.sp, color = color.copy(alpha = 0.7f), fontWeight = FontWeight.Medium)
            Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun SavingsEntryRow(entry: SavingsEntry, currency: NumberFormat, sdf: SimpleDateFormat) {
    val isPaid = entry.status == "PAID"
    val statusColor = if (isPaid) SuccessGreen else WarningAmber
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(statusColor.copy(alpha = 0.10f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (isPaid) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = null, tint = statusColor, modifier = Modifier.size(16.dp)
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(sdf.format(Date(entry.weekStartDate)), Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium)
        Text(currency.format(entry.amount), fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .background(statusColor.copy(alpha = 0.10f), RoundedCornerShape(50.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(entry.status, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
}
