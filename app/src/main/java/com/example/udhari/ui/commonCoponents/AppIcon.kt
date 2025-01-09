package com.example.udhari.ui.commonCoponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.udhari.R

@Composable
fun AppIcon() {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "उधारी", style = MaterialTheme.typography.displaySmall)
        Icon(
            painter = painterResource(R.drawable.ic_note),
            contentDescription = "Edit",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(30.dp)
        )
    }
}
