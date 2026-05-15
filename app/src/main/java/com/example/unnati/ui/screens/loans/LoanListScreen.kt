package com.example.unnati.ui.screens.loans

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
import com.example.unnati.data.entity.Loan
import com.example.unnati.data.entity.Member
import com.example.unnati.ui.components.GlassCard
import com.example.unnati.ui.theme.*
import com.example.unnati.ui.viewmodel.LoanViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanListScreen(
    vm: LoanViewModel,
    onNewLoan: () -> Unit,
    onLoanClick: (Int) -> Unit,
) {
    val palette = LocalAppPalette.current
    val loans by vm.activeLoans.collectAsState()
    val members by vm.members.collectAsState()
    val currency = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    Scaffold(
        containerColor = palette.bodyBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Loans",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                        color = palette.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = palette.glassTopBarBg,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewLoan,
                containerColor = palette.buttonContainer,
                contentColor = palette.buttonContent,
                shape = CircleShape,
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Loan")
            }
        }
    ) { padding ->
        if (loans.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(palette.primary.copy(alpha = 0.08f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountBalance, contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = palette.primary.copy(alpha = 0.5f))
                    }
                    Text("No active loans", style = MaterialTheme.typography.titleMedium,
                        color = palette.onSurface)
                    Text("Tap + to issue a new loan", style = MaterialTheme.typography.bodyMedium,
                        color = palette.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "${loans.size} active · ${currency.format(loans.sumOf { it.principal })} outstanding",
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.onSurfaceVariant
                    )
                }
                items(loans, key = { it.id }) { loan ->
                    val member = members.firstOrNull { it.id == loan.memberId }
                    LoanCard(loan = loan, member = member, currency = currency,
                        onClick = { onLoanClick(loan.id) })
                }
                item { Spacer(Modifier.height(72.dp)) }
            }
        }
    }
}

@Composable
private fun LoanCard(
    loan: Loan,
    member: Member?,
    currency: NumberFormat,
    onClick: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dueMs = loan.startDate + (loan.durationMonths.toLong() * 30L * 24 * 60 * 60 * 1000)
    val daysLeft = ((dueMs - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt()
    val dueBadgeColor = when {
        daysLeft <= 7 -> ErrorRed
        daysLeft <= 30 -> WarningAmber
        else -> SuccessGreen
    }
    val initials = member?.name?.split(" ")
        ?.take(2)?.joinToString("") { it.firstOrNull()?.uppercase() ?: "" } ?: "?"

    GlassCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(palette.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(initials, color = palette.primary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(member?.name ?: "Unknown",
                        style = MaterialTheme.typography.titleSmall,
                        color = palette.onSurface)
                    Text("${loan.interestRate}%/month · ${loan.durationMonths} months",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.onSurfaceVariant, fontSize = 12.sp)
                }
                // Due badge
                Box(
                    modifier = Modifier
                        .background(dueBadgeColor.copy(alpha = 0.12f), RoundedCornerShape(50.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        if (daysLeft < 0) "Overdue" else "$daysLeft d left",
                        color = dueBadgeColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Stats row
            HorizontalDivider(color = palette.outline.copy(alpha = 0.10f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                LoanStat("Principal", currency.format(loan.principal))
                LoanStat("Due Date", sdf.format(Date(dueMs)))
                LoanStat("Rate", "${loan.interestRate}%/mo")
            }
        }
    }
}

@Composable
private fun LoanStat(label: String, value: String) {
    val palette = LocalAppPalette.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = palette.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold,
            color = palette.onSurface)
    }
}
