package com.example.udhari.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.udhari.ui.addingEntity.AddEntityScreen
import com.example.udhari.ui.home.HomeScreen

@Composable
fun Navigation(navHostController: NavHostController) {
    val context = LocalContext.current
    CompositionLocalProvider(GlobalNavController provides navHostController) {
        NavHost(navController = navHostController, startDestination = "home") {
            composable(
                route = "home",
                enterTransition = ::slideIntoLeft,
                exitTransition = ::slideOutLeft,
                popEnterTransition = ::slideIntoRight,
                popExitTransition = ::slideOutLeft
            ) {
                HomeScreen()
            }
            composable(
                route = "addEntity",
                enterTransition = ::slideIntoLeft,
                exitTransition = ::slideOutLeft,
                popExitTransition = ::slideOutRight,
                popEnterTransition = ::slideIntoLeft
            ) {
                AddEntityScreen()
            }
        }
    }
}

val GlobalNavController = staticCompositionLocalOf<NavHostController> {
    error("NavController not provided")
}

fun slideIntoLeft(direction: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition {
    return direction.slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(700)
    )
}

fun slideIntoRight(direction: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition {
    return direction.slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(700)
    )
}

fun slideOutLeft(direction: AnimatedContentTransitionScope<NavBackStackEntry>): ExitTransition {
    return direction.slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(700)
    )
}

fun slideOutRight(direction: AnimatedContentTransitionScope<NavBackStackEntry>): ExitTransition {
    return direction.slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(700)
    )
}