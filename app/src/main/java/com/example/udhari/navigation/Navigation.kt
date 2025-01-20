package com.example.udhari.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.udhari.ui.noteBookDetails.addingEntity.AddEntityScreen
import com.example.udhari.ui.entityDetails.EntityDetailsScreen
import com.example.udhari.ui.entityDetails.transactionForm.TransactionForm
import com.example.udhari.ui.notebook.NoteBookScreen
import com.example.udhari.ui.noteBookDetails.NoteBookDetailsScreen
import com.example.udhari.ui.notebook.addingNoteBook.AddNoteBookScreen
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun Navigation(navHostController: NavHostController) {
    val context = LocalContext.current

    CompositionLocalProvider(GlobalNavController provides navHostController) {
        NavHost(navController = navHostController, startDestination = "noteBook") {
            composable(
                route = Routes.NoteBook.route,
                enterTransition = ::slideIntoLeft,
                exitTransition = ::slideOutRight,
                popEnterTransition = ::slideIntoRight,
                popExitTransition = ::slideOutLeft
            ) {
                NoteBookScreen()
            }
            composable(
                route = Routes.AddingNoteBook.route,
                enterTransition = ::slideIntoLeft,
                exitTransition = ::slideOutRight,
                popEnterTransition = ::slideIntoRight,
                popExitTransition = ::slideOutLeft
            ) {
                AddNoteBookScreen()
            }
            composable(
                route = Routes.NoteBookDetails.route,
                enterTransition = ::slideIntoLeft,
                exitTransition = ::slideOutRight,
                popEnterTransition = ::slideIntoRight,
                popExitTransition = ::slideOutLeft
            ) {
                val noteBookId =
                    navHostController.currentBackStackEntry?.arguments?.getString("noteBookId")
                if (noteBookId != null) {
                    NoteBookDetailsScreen(noteBookId = noteBookId.toInt())
                } else {
                    EntityIdNotFound("NoteBookId not found")
                }
            }
            composable(
                route = Routes.AddEntity.route,
                enterTransition = ::slideIntoLeft,
                exitTransition = ::slideOutRight,
                popEnterTransition = ::slideIntoRight,
                popExitTransition = ::slideOutLeft
            ) {
                val noteBookId =
                    navHostController.currentBackStackEntry?.arguments?.getString("noteBookId")
                if (noteBookId != null) {

                    AddEntityScreen(noteBookId = noteBookId.toInt())
                } else {
                    EntityIdNotFound("NoteBookId note found")
                }
            }
            composable(
                route = Routes.EntityDetails.route,
                enterTransition = ::slideIntoLeft,
                exitTransition = ::slideOutRight,
                popEnterTransition = ::slideIntoRight,
                popExitTransition = ::slideOutLeft
            ) { navBackStackEntry ->
                val noteBookId = navBackStackEntry.arguments?.getString("noteBookId")
                val entityId = navBackStackEntry.arguments?.getString("entityId")
                if (entityId != null && noteBookId != null) {
                    EntityDetailsScreen(entityId = entityId.toInt(), noteBookId = noteBookId.toInt())
                } else {
                    EntityIdNotFound()
                }
            }
            composable(
                route = Routes.TransactionForm.route,
                enterTransition = ::slideIntoLeft,
                exitTransition = ::slideOutRight,
                popEnterTransition = ::slideIntoRight,
                popExitTransition = ::slideOutLeft
            ) { navBackStackEntry ->
                val entityId = navBackStackEntry.arguments?.getString("entityId")
                val noteBookId = navBackStackEntry.arguments?.getString("noteBookId")
                if (entityId != null && noteBookId != null) {
                    TransactionForm(entityId = entityId.toInt(), noteBookId =  noteBookId.toInt())
                } else {
                    EntityIdNotFound()
                }
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
        animationSpec = tween(700, easing = FastOutSlowInEasing)
    )
}

fun slideIntoRight(direction: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition {
    return direction.slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(700, easing = FastOutSlowInEasing)
    )
}

fun slideOutLeft(direction: AnimatedContentTransitionScope<NavBackStackEntry>): ExitTransition {
    return direction.slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(700, easing = FastOutSlowInEasing)
    )
}

fun slideOutRight(direction: AnimatedContentTransitionScope<NavBackStackEntry>): ExitTransition {
    return direction.slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(700, easing = FastOutSlowInEasing)
    )
}

@Composable
fun EntityIdNotFound(
    error: String = "Id not found"
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(error, style = MaterialTheme.typography.bodyLarge)
    }
}
