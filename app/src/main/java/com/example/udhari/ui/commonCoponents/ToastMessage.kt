package com.example.udhari.ui.commonCoponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.compose.LocalExtendedColors
import com.example.udhari.utils.ToastManager
import kotlinx.coroutines.delay

@Composable
fun ToastMessage(
    modifier: Modifier = Modifier.padding(12.dp),
    shape: RoundedCornerShape = CircleShape
) {
    val extendedColorScheme = LocalExtendedColors.current
    val toastMessage by ToastManager.toastMessage.collectAsState()
    val showMessage by ToastManager.showMessage.collectAsState()
    val isSuccess by ToastManager.isSuccess.collectAsState()

    if (showMessage) {
        val backgroundColor = if (isSuccess) {
            extendedColorScheme.green.colorContainer
        } else {
            extendedColorScheme.red.colorContainer
        }

        val iconColor = if (isSuccess) {
            extendedColorScheme.green.color
        } else {
            extendedColorScheme.red.color

        }

        val icon = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Close
        val contentDescription = if (isSuccess) "Success icon" else "Error icon"

        LaunchedEffect(showMessage) {
            if (showMessage) {
                delay(2000) // Hide after 2 seconds
                ToastManager.hideToast()
            }
        }

        Row(
            modifier = modifier
                .background(backgroundColor, shape)
                .padding(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconColor,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = toastMessage)
        }
    }
}
