package com.example.unnati.ui.screens.loans

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unnati.data.entity.Repayment
import com.example.unnati.ui.components.GlassCard
import com.example.unnati.ui.theme.*
import com.example.unnati.ui.viewmodel.LoanDetailViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanDetailScreen(
    vm: LoanDetailViewModel,
    onBack: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val loan by vm.loan.collectAsState()
    val member by vm.member.collectAsState()
    val repayments by vm.repayments.collectAsState()
    val totalRepaid by vm.totalRepaid.collectAsState()
    val interest by vm.accruedInterest.collectAsState()
    val outstanding by vm.outstanding.collectAsState()
    val repaymentSaved by vm.repaymentSaved.collectAsState()

    val currency = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    var showRepaymentSheet by remember { mutableStateOf(false) }
    var repayAmount by remember { mutableStateOf("") }
    var repayNote by remember { mutableStateOf("") }

    LaunchedEffect(repaymentSaved) {
        if (repaymentSaved) { showRepaymentSheet = false; repayAmount = ""; repayNote = "" }
    }

    if (showRepaymentSheet) {
        ModalBottomSheet(
            onDismissRequest = { showRepaymentSheet = false },
            containerColor = palette.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Enter Repayment", fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium)
                Box(
                    modifier = Modifier
                        .background(WarningAmber.copy(alpha = 0.10f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("Outstanding: ${currency.format(outstanding)}",
                        color = WarningAmber, fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium)
                }
                OutlinedTextField(
                    value = repayAmount,
                    onValueChange = { repayAmount = it },
                    label = { Text("Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                OutlinedTextField(
                    value = repayNote,
                    onValueChange = { repayNote = it },
                    label = { Text("Note (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                Button(
                    onClick = {
                        repayAmount.toDoubleOrNull()?.let {
                            vm.addRepayment(it, repayNote.ifBlank { null })
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.buttonContainer,
                        contentColor = palette.buttonContent,
                    ),
                    enabled = repayAmount.toDoubleOrNull() != null
                ) {
                    Text("Record Repayment", style = MaterialTheme.typography.labelLarge)
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }

    Scaffold(
        containerColor = palette.bodyBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Loan Detail",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = palette.glassTopBarBg,
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                // Hero detail glass card
                val initials = member?.name?.split(" ")
                    ?.take(2)?.joinToString("") { it.firstOrNull()?.uppercase() ?: "" } ?: "?"
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Member row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(palette.primary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(initials, color = palette.primary, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(member?.name ?: "—",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall)
                                loan?.let {
                                    Text("Issued: ${sdf.format(Date(it.startDate))}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = palette.onSurfaceVariant, fontSize = 12.sp)
                                }
                            }
                        }
                        HorizontalDivider(color = palette.outline.copy(alpha = 0.10f))
                        loan?.let { l ->
                            DetailRow("Principal", currency.format(l.principal))
                            DetailRow("Interest Rate", "${l.interestRate}%/month")
                            DetailRow("Duration", "${l.durationMonths} months")
                        }
                        HorizontalDivider(color = palette.outline.copy(alpha = 0.10f))
                        DetailRow("Accrued Interest", currency.format(interest))
                        DetailRow("Total Repaid", currency.format(totalRepaid))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically) {
                            Text("OUTSTANDING",
                                style = MaterialTheme.typography.labelLarge,
                                color = palette.onSurfaceVariant)
                            Text(currency.format(outstanding), fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = if (outstanding > 0) ErrorRed else SuccessGreen)
                        }
                    }
                }
            }

            item {
                // Repayment progress
                val loanTotal = (loan?.principal ?: 0.0) + interest
                val progress = if (loanTotal > 0) (totalRepaid / loanTotal).toFloat().coerceIn(0f, 1f) else 0f
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Repayment Progress",
                                style = MaterialTheme.typography.labelMedium, color = palette.onSurfaceVariant)
                            Text("${(progress * 100).toInt()}%",
                                fontWeight = FontWeight.Bold, color = SuccessGreen)
                        }
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50.dp)),
                            color = SuccessGreen,
                            trackColor = palette.surfaceContainer,
                        )
                    }
                }
            }

            item {
                if (loan?.status == "ACTIVE") {
                    Button(
                        onClick = { showRepaymentSheet = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = palette.buttonContainer,
                            contentColor = palette.buttonContent,
                        )
                    ) {
                        Icon(Icons.Default.AddCard, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Enter Repayment", style = MaterialTheme.typography.labelLarge)
                    }
                }
                if (outstanding <= 0 && loan?.status == "ACTIVE") {
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { vm.closeLoan() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) { Text("Mark as Closed") }
                }
            }

            item {
                Text("Repayment History", fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall)
            }

            if (repayments.isEmpty()) {
                item {
                    Text("No repayments yet.", style = MaterialTheme.typography.bodyMedium,
                        color = palette.onSurfaceVariant)
                }
            } else {
                items(repayments) { r -> RepaymentRow(r, currency, sdf) }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    val palette = LocalAppPalette.current
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = palette.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun RepaymentRow(r: Repayment, currency: NumberFormat, sdf: SimpleDateFormat) {
    val palette = LocalAppPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(SuccessGreen.copy(alpha = 0.10f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Payments, contentDescription = null,
                tint = SuccessGreen, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(sdf.format(Date(r.paidDate)), style = MaterialTheme.typography.bodyMedium)
            r.note?.let { Text(it, style = MaterialTheme.typography.bodyMedium,
                color = palette.onSurfaceVariant, fontSize = 12.sp) }
        }
        Text(currency.format(r.amount), fontWeight = FontWeight.SemiBold, color = SuccessGreen)
    }
    HorizontalDivider(color = palette.outline.copy(alpha = 0.10f))
}
