package com.example.udhari.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.udhari.ui.home.HomeScreen

@Composable
fun Navigation(navHostController: NavHostController) {
    val context = LocalContext.current
    CompositionLocalProvider(GlobalNavController provides navHostController) {
        NavHost(navController = navHostController, startDestination = "home") {
            composable("home") {
                HomeScreen()
            }

        }
    }
}

val GlobalNavController = staticCompositionLocalOf<NavHostController> {
    error("NavController not provided")
}