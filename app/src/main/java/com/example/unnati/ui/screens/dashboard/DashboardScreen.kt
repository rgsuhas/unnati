package com.example.unnati.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unnati.ui.components.GlassCard
import com.example.unnati.ui.theme.LocalAppPalette
import com.example.unnati.ui.theme.SuccessGreen
import com.example.unnati.ui.theme.WarningAmber
import com.example.unnati.ui.viewmodel.DashboardViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    vm: DashboardViewModel,
    onAddMember: () -> Unit,
    onLogSaving: () -> Unit,
    onNewLoan: () -> Unit,
    onExport: () -> Unit,
    onLoans: () -> Unit,
    onProfile: () -> Unit,
    isDark: Boolean,
    onToggleDark: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val capital by vm.groupCapital.collectAsState()
    val loansCount by vm.activeLoansCount.collectAsState()
    val loanOutstanding by vm.totalLoanOutstanding.collectAsState()
    val memberCount by vm.memberCount.collectAsState()
    val currency = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    Scaffold(
        containerColor = palette.bodyBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mahila-Shakti Unnati",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                        color = palette.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                actions = {
                    IconButton(onClick = onToggleDark) {
                        Icon(
                            if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDark) "Switch to light" else "Switch to dark",
                            tint = palette.onSurfaceVariant,
                        )
                    }
                    IconButton(onClick = onProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile",
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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Hero capital card ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                palette.surfaceContainer,
                                palette.primaryContainer.copy(alpha = 0.20f)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                // Decorative orb
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .background(palette.secondaryContainer.copy(alpha = 0.25f))
                        .align(Alignment.TopEnd)
                        .offset(x = 24.dp, y = (-24).dp)
                )
                Column {
                    Text(
                        "Group Capital",
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        currency.format(capital),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.onSurface
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(SuccessGreen.copy(alpha = 0.12f), CircleShape)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.People, contentDescription = null,
                                    tint = SuccessGreen, modifier = Modifier.size(14.dp))
                                Text("$memberCount members", fontSize = 12.sp, color = SuccessGreen,
                                    fontWeight = FontWeight.Medium)
                            }
                        }
                        if (loansCount > 0) {
                            Box(
                                modifier = Modifier
                                    .background(WarningAmber.copy(alpha = 0.12f), CircleShape)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("$loansCount active loans", fontSize = 12.sp,
                                    color = WarningAmber, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // ── Stat cards grid ────────────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.AccountBalance,
                    iconTint = WarningAmber,
                    label = "Outstanding",
                    value = currency.format(loanOutstanding),
                    onClick = onLoans
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.TrendingUp,
                    iconTint = palette.primary,
                    label = "Active Loans",
                    value = "$loansCount",
                    onClick = onLoans
                )
            }

            // ── Quick actions ──────────────────────────────────────────────
            Text(
                "Quick Actions",
                style = MaterialTheme.typography.labelMedium,
                color = palette.onSurfaceVariant
            )
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickAction(Icons.Default.PersonAdd, "Add\nMember", palette.primary, onAddMember)
                    QuickAction(Icons.Default.Savings, "Log\nSaving", palette.primary, onLogSaving)
                    QuickAction(Icons.Default.AccountBalance, "New\nLoan", palette.primary, onNewLoan)
                    QuickAction(Icons.Default.Share, "Export", palette.primary, onExport)
                }
            }

            // ── Summary card ──────────────────────────────────────────────
            GlassCard(modifier = Modifier.fillMaxWidth(), onClick = onExport) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(palette.primary.copy(alpha = 0.10f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null,
                            tint = palette.primary, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Export Report", fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleSmall)
                        Text("Generate group financial summary",
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.onSurfaceVariant,
                            fontSize = 13.sp)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null,
                        tint = palette.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier,
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    GlassCard(modifier = modifier, onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconTint.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Text(label, style = MaterialTheme.typography.labelMedium, color = LocalAppPalette.current.onSurfaceVariant)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = LocalAppPalette.current.onSurface)
        }
    }
}

@Composable
private fun QuickAction(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilledTonalIconButton(
            onClick = onClick,
            modifier = Modifier.size(56.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = color.copy(alpha = 0.10f),
                contentColor = color,
            )
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(26.dp))
        }
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = LocalAppPalette.current.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
        )
    }
}
