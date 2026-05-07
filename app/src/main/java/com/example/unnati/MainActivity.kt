package com.example.unnati

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.unnati.data.PrefsKeys
import com.example.unnati.data.appDataStore
import com.example.unnati.ui.navigation.Screen
import com.example.unnati.ui.screens.auth.PinScreen
import com.example.unnati.ui.screens.dashboard.DashboardScreen
import com.example.unnati.ui.screens.export.ExportPreviewScreen
import com.example.unnati.ui.screens.loans.LoanDetailScreen
import com.example.unnati.ui.screens.loans.LoanListScreen
import com.example.unnati.ui.screens.loans.NewLoanScreen
import com.example.unnati.ui.screens.members.AddEditMemberScreen
import com.example.unnati.ui.screens.members.MemberListScreen
import com.example.unnati.ui.screens.members.MemberProfileScreen
import com.example.unnati.ui.screens.savings.SavingsEntryScreen
import com.example.unnati.ui.screens.settings.SettingsScreen
import com.example.unnati.ui.screens.splash.SplashScreen
import com.example.unnati.ui.theme.LocalAppPalette
import com.example.unnati.ui.theme.UnnatiTheme
import com.example.unnati.ui.viewmodel.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val systemDark = isSystemInDarkTheme()
            val scope = rememberCoroutineScope()

            // Reactive dark-mode preference; null = follow system
            val isDarkOverride by context.appDataStore.data
                .map { it[PrefsKeys.IS_DARK_MODE] }
                .collectAsState(initial = null)
            val isDark = isDarkOverride ?: systemDark

            fun toggleDark() = scope.launch {
                context.appDataStore.edit { it[PrefsKeys.IS_DARK_MODE] = !isDark }
            }

            UnnatiTheme(darkTheme = isDark) {
                val app = application as UnnatiApp
                val repo = app.repository
                val factory = RepositoryViewModelFactory(repo)

                val navController = rememberNavController()
                val currentBack by navController.currentBackStackEntryAsState()
                val currentRoute = currentBack?.destination?.route

                val bottomItems = listOf(
                    BottomNavItem("Home",    Icons.Default.Home,           Screen.Dashboard.route),
                    BottomNavItem("Members", Icons.Default.People,         Screen.MemberList.route),
                    BottomNavItem("Savings", Icons.Default.Savings,        Screen.SavingsEntry.route),
                    BottomNavItem("Loans",   Icons.Default.AccountBalance, Screen.LoanList.route),
                )
                val showBottomBar = currentRoute in bottomItems.map { it.route }
                val palette = LocalAppPalette.current

                Scaffold(
                    containerColor = palette.bodyBackground,
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(
                                containerColor = palette.glassBottomNavBg,
                                tonalElevation = 0.dp,
                            ) {
                                bottomItems.forEach { item ->
                                    val selected = currentRoute == item.route
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = { Icon(item.icon, contentDescription = item.label) },
                                        label = { Text(item.label) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor   = palette.primary,
                                            selectedTextColor   = palette.primary,
                                            indicatorColor      = palette.primary.copy(alpha = 0.12f),
                                            unselectedIconColor = palette.onSurfaceVariant,
                                            unselectedTextColor = palette.onSurfaceVariant,
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Splash.route
                        ) {
                            // ── Auth ────────────────────────────────────────────
                            composable(Screen.Splash.route) {
                                SplashScreen(onNavigateNext = {
                                    navController.navigate(Screen.PinLogin.route) {
                                        popUpTo(Screen.Splash.route) { inclusive = true }
                                    }
                                })
                            }
                            composable(Screen.PinLogin.route) {
                                PinScreen(
                                    onAdminSuccess = {
                                        navController.navigate(Screen.Dashboard.route) {
                                            popUpTo(Screen.PinLogin.route) { inclusive = true }
                                        }
                                    },
                                    onMemberView = {
                                        navController.navigate(Screen.Dashboard.route) {
                                            popUpTo(Screen.PinLogin.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            // ── Main tabs ───────────────────────────────────────
                            composable(Screen.Dashboard.route) {
                                val vm: DashboardViewModel = viewModel(factory = factory)
                                DashboardScreen(
                                    vm = vm,
                                    onAddMember  = { navController.navigate(Screen.AddMember.createRoute()) },
                                    onLogSaving  = { navController.navigate(Screen.SavingsEntry.route) },
                                    onNewLoan    = { navController.navigate(Screen.NewLoan.route) },
                                    onExport     = { navController.navigate(Screen.Export.route) },
                                    onLoans      = { navController.navigate(Screen.LoanList.route) },
                                    onProfile    = { navController.navigate(Screen.Settings.route) },
                                    isDark       = isDark,
                                    onToggleDark = { toggleDark() },
                                )
                            }
                            composable(Screen.MemberList.route) {
                                val vm: MembersViewModel = viewModel(factory = factory)
                                MemberListScreen(
                                    vm = vm,
                                    onAddMember   = { navController.navigate(Screen.AddMember.createRoute()) },
                                    onMemberClick = { navController.navigate(Screen.MemberProfile.createRoute(it)) },
                                )
                            }
                            composable(Screen.SavingsEntry.route) {
                                val vm: SavingsEntryViewModel = viewModel(factory = factory)
                                SavingsEntryScreen(
                                    vm = vm,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable(Screen.LoanList.route) {
                                val vm: LoanViewModel = viewModel(factory = factory)
                                LoanListScreen(
                                    vm = vm,
                                    onNewLoan   = { navController.navigate(Screen.NewLoan.route) },
                                    onLoanClick = { navController.navigate(Screen.LoanDetail.createRoute(it)) },
                                )
                            }

                            // ── Member screens ──────────────────────────────────
                            composable(
                                route = Screen.AddMember.route,
                                arguments = listOf(navArgument("memberId") { type = NavType.IntType })
                            ) { back ->
                                val vm: AddEditMemberViewModel = viewModel(factory = factory)
                                AddEditMemberScreen(
                                    vm = vm,
                                    memberId = back.arguments?.getInt("memberId") ?: -1,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable(
                                route = Screen.MemberProfile.route,
                                arguments = listOf(navArgument("memberId") { type = NavType.IntType })
                            ) { back ->
                                val memberId = back.arguments?.getInt("memberId") ?: return@composable
                                val vm: MemberProfileViewModel = viewModel(
                                    factory = IdViewModelFactory(repo, memberId),
                                    key = "profile_$memberId"
                                )
                                MemberProfileScreen(
                                    vm = vm,
                                    onBack       = { navController.popBackStack() },
                                    onEdit       = { navController.navigate(Screen.AddMember.createRoute(it)) },
                                    onLoanDetail = { navController.navigate(Screen.LoanDetail.createRoute(it)) },
                                    onLogSaving  = { navController.navigate(Screen.SavingsEntry.route) },
                                )
                            }

                            // ── Loan screens ────────────────────────────────────
                            composable(Screen.NewLoan.route) {
                                val vm: NewLoanViewModel = viewModel(factory = factory)
                                NewLoanScreen(
                                    vm = vm,
                                    onBack = { navController.popBackStack() },
                                    onViewLoan = { navController.navigate(Screen.LoanDetail.createRoute(it)) },
                                )
                            }
                            composable(
                                route = Screen.LoanDetail.route,
                                arguments = listOf(navArgument("loanId") { type = NavType.IntType })
                            ) { back ->
                                val loanId = back.arguments?.getInt("loanId") ?: return@composable
                                val vm: LoanDetailViewModel = viewModel(
                                    factory = IdViewModelFactory(repo, loanId),
                                    key = "loan_$loanId"
                                )
                                LoanDetailScreen(vm = vm, onBack = { navController.popBackStack() })
                            }

                            // ── Utility screens ─────────────────────────────────
                            composable(Screen.Export.route) {
                                val vm: ExportViewModel = viewModel(factory = factory)
                                ExportPreviewScreen(vm = vm, onBack = { navController.popBackStack() })
                            }
                            composable(Screen.Settings.route) {
                                SettingsScreen(
                                    onBack = { navController.popBackStack() },
                                    onLogout = {
                                        navController.navigate(Screen.PinLogin.route) {
                                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
