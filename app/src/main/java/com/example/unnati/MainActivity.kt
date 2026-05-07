package com.example.unnati

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.unnati.ui.navigation.Screen
import com.example.unnati.ui.screens.splash.SplashScreen
import com.example.unnati.ui.theme.UnnatiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UnnatiTheme {
                val navController = rememberNavController()
                
                NavHost(
                    navController = navController,
                    startDestination = Screen.Splash.route
                ) {
                    composable(Screen.Splash.route) {
                        SplashScreen(
                            onNavigateNext = {
                                navController.navigate(Screen.PinLogin.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    
                    composable(Screen.PinLogin.route) {
                        PinPlaceholder(onLoginSuccess = {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.PinLogin.route) { inclusive = true }
                            }
                        })
                    }

                    composable(Screen.Dashboard.route) {
                        DashboardPlaceholder()
                    }
                }
            }
        }
    }
}

@Composable
fun PinPlaceholder(onLoginSuccess: () -> Unit) {
    Button(onClick = onLoginSuccess) {
        Text("Login Placeholder (Click to bypass)")
    }
}

@Composable
fun DashboardPlaceholder() {
    Text("Dashboard Placeholder")
}
