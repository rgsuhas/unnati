package com.example.unnati.ui.screens.members

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
import com.example.unnati.ui.theme.ErrorRed
import com.example.unnati.ui.theme.LocalAppPalette
import com.example.unnati.ui.viewmodel.AddEditMemberViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMemberScreen(
    vm: AddEditMemberViewModel,
    memberId: Int = -1,
    onBack: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val existing by vm.member.collectAsState()
    val saved by vm.saved.collectAsState()

    LaunchedEffect(memberId) { vm.load(memberId) }
    LaunchedEffect(saved) { if (saved) onBack() }

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var joinDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var role by remember { mutableStateOf("MEMBER") }
    var nameError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(existing) {
        existing?.let {
            name = it.name; phone = it.phone; joinDate = it.joinDate; role = it.role
        }
    }

    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    if (showDatePicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = joinDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { joinDate = it }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state) }
    }

    fun validate() {
        nameError = if (name.trim().length < 2) "Min 2 characters" else ""
        phoneError = if (phone.trim().length != 10) "Enter 10-digit number" else ""
        if (nameError.isEmpty() && phoneError.isEmpty()) {
            vm.save(memberId, name, phone, null, joinDate, role)
        }
    }

    Scaffold(
        containerColor = palette.bodyBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (memberId > 0) "Edit Member" else "Add Member",
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
                    TextButton(onClick = ::validate) {
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(palette.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                val initials = name.split(" ").take(2)
                    .joinToString("") { it.firstOrNull()?.uppercase() ?: "" }
                    .ifEmpty { "?" }
                Text(initials, color = palette.primary, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
            Text("Member photo (coming soon)", fontSize = 11.sp, color = palette.onSurfaceVariant)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = "" },
                label = { Text("Full Name *") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                isError = nameError.isNotEmpty(),
                supportingText = { if (nameError.isNotEmpty()) Text(nameError, color = ErrorRed) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { if (it.length <= 10) { phone = it; phoneError = "" } },
                label = { Text("Phone Number *") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                isError = phoneError.isNotEmpty(),
                supportingText = { if (phoneError.isNotEmpty()) Text(phoneError, color = ErrorRed) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = sdf.format(Date(joinDate)),
                onValueChange = {},
                label = { Text("Date Joined *") },
                leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Pick date")
                    }
                },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
            )

            // Role chips
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Role", style = MaterialTheme.typography.labelMedium, color = palette.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf("MEMBER", "ADMIN").forEach { r ->
                        FilterChip(
                            selected = role == r,
                            onClick = { role = r },
                            label = { Text(r) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = palette.primary,
                                selectedLabelColor = Color.White,
                            )
                        )
                    }
                }
            }

            // Save CTA
            Button(
                onClick = ::validate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.buttonContainer,
                    contentColor = palette.buttonContent,
                )
            ) {
                Text("Save Member", style = MaterialTheme.typography.labelLarge)
            }

            // Deactivate (edit mode only)
            if (memberId > 0) {
                var showConfirm by remember { mutableStateOf(false) }
                if (showConfirm) {
                    AlertDialog(
                        onDismissRequest = { showConfirm = false },
                        title = { Text("Deactivate Member?") },
                        text = { Text("All history will be preserved. This cannot be undone easily.") },
                        confirmButton = {
                            TextButton(onClick = { showConfirm = false; onBack() }) {
                                Text("Deactivate", color = ErrorRed)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showConfirm = false }) { Text("Cancel") }
                        }
                    )
                }
                OutlinedButton(
                    onClick = { showConfirm = true },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.PersonOff, contentDescription = null, tint = ErrorRed)
                    Spacer(Modifier.width(8.dp))
                    Text("Deactivate Member")
                }
            }
        }
    }
}
