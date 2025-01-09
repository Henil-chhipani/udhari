package com.example.udhari.ui.commonCoponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BackBtn(
) {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
        contentDescription = "BackBtn",
        modifier = Modifier
            .padding(vertical = 15.dp)
            .padding(end = 10.dp).size(100.dp),
        tint = MaterialTheme.colorScheme.onSurface
    )
}