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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unnati.data.entity.Member
import com.example.unnati.ui.components.GlassCard
import com.example.unnati.ui.theme.LocalAppPalette
import com.example.unnati.ui.viewmodel.MembersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberListScreen(
    vm: MembersViewModel,
    onAddMember: () -> Unit,
    onMemberClick: (Int) -> Unit,
) {
    val palette = LocalAppPalette.current
    val members by vm.members.collectAsState()
    val query by vm.searchQuery.collectAsState()

    Scaffold(
        containerColor = palette.bodyBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Members",
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
                onClick = onAddMember,
                containerColor = palette.buttonContainer,
                contentColor = palette.buttonContent,
                shape = CircleShape,
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Member")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Glass search bar
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = null,
                        tint = palette.onSurfaceVariant, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    TextField(
                        value = query,
                        onValueChange = { vm.searchQuery.value = it },
                        placeholder = {
                            Text("Search members…", style = MaterialTheme.typography.bodyMedium,
                                color = palette.onSurfaceVariant)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            if (members.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(palette.primary.copy(alpha = 0.08f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.People, contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = palette.primary.copy(alpha = 0.5f))
                        }
                        Text("No members yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = palette.onSurface)
                        Text("Tap + to add the first member",
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(members, key = { it.id }) { member ->
                        MemberCard(member = member, onClick = { onMemberClick(member.id) })
                    }
                    item { Spacer(Modifier.height(72.dp)) } // FAB clearance
                }
            }
        }
    }
}

@Composable
private fun MemberCard(member: Member, onClick: () -> Unit) {
    val palette = LocalAppPalette.current
    val initials = member.name.split(" ")
        .take(2).joinToString("") { it.firstOrNull()?.uppercase() ?: "" }

    GlassCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(palette.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, color = palette.primary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    member.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = palette.onSurface,
                )
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Call, contentDescription = null,
                        modifier = Modifier.size(14.dp), tint = palette.onSurfaceVariant)
                    Text(member.phone, style = MaterialTheme.typography.bodyMedium,
                        color = palette.onSurfaceVariant, fontSize = 13.sp)
                }
            }

            // Role badge
            Box(
                modifier = Modifier
                    .background(palette.primary.copy(alpha = 0.10f), RoundedCornerShape(50.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    member.role,
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.primary,
                    fontSize = 11.sp,
                )
            }
        }
    }
}
