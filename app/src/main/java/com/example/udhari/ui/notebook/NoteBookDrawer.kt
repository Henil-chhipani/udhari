package com.example.udhari.ui.notebook

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorProperty
import androidx.compose.ui.unit.dp
import com.example.udhari.navigation.GlobalNavController
import com.example.udhari.navigation.Routes
import com.example.udhari.ui.commonCoponents.AppIcon

@Composable
fun NoteBookDrawer() {
    val globalNavController = GlobalNavController.current
    // The ModalDrawerSheet provides the Material3 drawer look and feel
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .width(200.dp)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            AppIcon()
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider()
            TextButton(onClick = {
                globalNavController.navigate(Routes.About.route) {
                    launchSingleTop = true
                }
            }) {
                Text(text = "About", style = MaterialTheme.typography.titleSmall)
            }
            HorizontalDivider()
            TextButton(onClick = {
                globalNavController.navigate(Routes.Guide.route) {
                    launchSingleTop = true
                }
            }) {
                Text(text = "Guide", style = MaterialTheme.typography.titleSmall)
            }
            HorizontalDivider()
            TextButton(onClick = {
                globalNavController.navigate(Routes.VoiceFeatureGuide.route) {
                    launchSingleTop = true
                }
            }) {
                Text(text = "Voice features Guide", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}