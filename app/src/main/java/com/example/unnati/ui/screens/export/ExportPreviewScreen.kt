package com.example.unnati.ui.screens.export

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unnati.ui.components.GlassCard
import com.example.unnati.ui.theme.LocalAppPalette
import com.example.unnati.ui.viewmodel.ExportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportPreviewScreen(
    vm: ExportViewModel,
    onBack: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val report by vm.report.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) { vm.buildReport() }

    fun shareReport() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, report)
            putExtra(Intent.EXTRA_SUBJECT, "Mahila-Shakti Unnati Group Report")
        }
        context.startActivity(Intent.createChooser(intent, "Share Report via"))
    }

    Scaffold(
        containerColor = palette.bodyBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Export Preview",
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
                    IconButton(onClick = ::shareReport) {
                        Icon(Icons.Default.Share, contentDescription = "Share",
                            tint = palette.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = palette.glassTopBarBg,
                )
            )
        },
        bottomBar = {
            Surface(
                color = palette.glassTopBarBg,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            ) {
                Button(
                    onClick = ::shareReport,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.buttonContainer,
                        contentColor = palette.buttonContent,
                    )
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Share via WhatsApp / SMS", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            if (report.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = palette.primary)
                }
            } else {
                GlassCard(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = report,
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = palette.onSurface,
                    )
                }
            }
        }
    }
}
