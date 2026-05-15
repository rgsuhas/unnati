package com.example.unnati.ui.screens.loans

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unnati.ui.components.GlassCard
import com.example.unnati.ui.theme.*
import com.example.unnati.ui.viewmodel.NewLoanViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewLoanScreen(
    vm: NewLoanViewModel,
    onBack: () -> Unit,
    onViewLoan: (Int) -> Unit,
) {
    val palette = LocalAppPalette.current
    val members by vm.members.collectAsState()
    val isBlocked by vm.isBlocked.collectAsState()
    val existingLoan by vm.existingLoan.collectAsState()
    val eligibility by vm.eligibility.collectAsState()
    val interest by vm.estimatedInterest.collectAsState()
    val issued by vm.issued.collectAsState()
    val principal by vm.principal.collectAsState()
    val rate by vm.rate.collectAsState()
    val duration by vm.duration.collectAsState()
    val selectedMemberId by vm.selectedMemberId.collectAsState()

    val currency = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    var expanded by remember { mutableStateOf(false) }
    val selectedMember = members.firstOrNull { it.id == selectedMemberId }

    LaunchedEffect(issued) { if (issued) onBack() }

    Scaffold(
        containerColor = palette.bodyBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "New Loan",
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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Member picker glass card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select Member", style = MaterialTheme.typography.labelMedium,
                        color = palette.onSurfaceVariant)
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                        OutlinedTextField(
                            value = selectedMember?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Choose a member…") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(16.dp),
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            members.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(m.name) },
                                    onClick = { vm.selectedMemberId.value = m.id; expanded = false }
                                )
                            }
                        }
                    }
                }
            }

            // Eligibility / Block glass card
            if (selectedMember != null) {
                if (isBlocked) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(ErrorRed.copy(alpha = 0.12f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Block, contentDescription = null,
                                        tint = ErrorRed, modifier = Modifier.size(20.dp))
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("Cannot Issue Loan",
                                        fontWeight = FontWeight.SemiBold, color = ErrorRed,
                                        style = MaterialTheme.typography.titleSmall)
                                    Text("${selectedMember.name} has an unpaid active loan.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = ErrorRed.copy(alpha = 0.7f), fontSize = 13.sp)
                                }
                            }
                            TextButton(
                                onClick = { existingLoan?.let { onViewLoan(it.id) } },
                                modifier = Modifier.padding(top = 4.dp)
                            ) { Text("View Existing Loan →", color = ErrorRed) }
                        }
                    }
                } else {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(SuccessGreen.copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null,
                                    tint = SuccessGreen, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("Eligible for Loan",
                                    fontWeight = FontWeight.SemiBold, color = SuccessGreen,
                                    style = MaterialTheme.typography.titleSmall)
                                Text("Max: ${currency.format(eligibility)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SuccessGreen.copy(alpha = 0.8f), fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Loan form fields
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Loan Details", style = MaterialTheme.typography.labelMedium,
                        color = palette.onSurfaceVariant)

                    OutlinedTextField(
                        value = principal,
                        onValueChange = { vm.principal.value = it },
                        label = { Text("Loan Amount (₹) *") },
                        leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isBlocked
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = rate,
                            onValueChange = { vm.rate.value = it },
                            label = { Text("Rate %/month") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            enabled = !isBlocked
                        )
                        OutlinedTextField(
                            value = duration,
                            onValueChange = { vm.duration.value = it },
                            label = { Text("Months") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            enabled = !isBlocked
                        )
                    }
                }
            }

            // Preview card
            if (principal.toDoubleOrNull() != null && !isBlocked) {
                val p = principal.toDoubleOrNull() ?: 0.0
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Loan Preview", style = MaterialTheme.typography.labelMedium,
                            color = palette.onSurfaceVariant)
                        HorizontalDivider(color = palette.outline.copy(alpha = 0.10f))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Est. Interest",
                                style = MaterialTheme.typography.bodyMedium)
                            Text(currency.format(interest), fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Repayable",
                                style = MaterialTheme.typography.bodyMedium)
                            Text(currency.format(p + interest), fontWeight = FontWeight.Bold,
                                color = palette.primary, fontSize = 16.sp)
                        }
                    }
                }
            }

            Button(
                onClick = { vm.issueLoan() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                enabled = !isBlocked && selectedMember != null && principal.toDoubleOrNull() != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.buttonContainer,
                    contentColor = palette.buttonContent,
                )
            ) {
                Text("Issue Loan", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
