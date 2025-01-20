package com.example.udhari.ui.notebook

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compose.LocalExtendedColors
import com.example.udhari.R
import com.example.udhari.navigation.GlobalNavController
import com.example.udhari.ui.commonCoponents.AppIcon
import com.example.udhari.ui.commonCoponents.TopBar

@Composable
fun NoteBookScreen(
    viewModel: NoteBookViewModel = hiltViewModel()
) {
    val uiState by viewModel.

    uiState.collectAsState()
    NoteBookUi(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun NoteBookUi(
    uiState: NoteBookUiState,
    onEvent: (NoteBookEvent) -> Unit,
) {
    val globalNavController = GlobalNavController.current
    val extendedColorScheme = LocalExtendedColors.current

    LaunchedEffect(uiState.listOfNoteBook) {
        onEvent(NoteBookEvent.FetchNoteBooks)
    }

    Scaffold(topBar = {
        TopBar(
            title = { AppIcon() },
            onIconClick = {},
            icon = {}
        )
    },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "Your Notebooks",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(items = uiState.listOfNoteBook) { notebook ->
                        NoteBookCard(
                            noteBookName = notebook.name,
                            totalOwe = notebook.totalOwe,
                            totalCollect = notebook.totalCollect,
                            onCardClick = {
                                globalNavController.navigate("noteBookDetails/${notebook.id}")
                            }
                        )
                    }
                }

                FloatingActionButton(
                    onClick = {
                        globalNavController.navigate("addingNoteBook")
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 55.dp, end = 20.dp)
                        .shadow(
                            elevation = 8.dp, // Add shadow for depth
                            shape = CircleShape
                        )
                        .size(56.dp) // Standard FAB size
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ),
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 8.dp, // Elevation for the FAB
                        pressedElevation = 12.dp // Elevation when pressed
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Icon",
                        tint = MaterialTheme.colorScheme.onPrimary, // Ensure the icon color contrasts with the background
                        modifier = Modifier.size(24.dp) // Adjust icon size
                    )
                }

            }
        })
}

@Composable
fun NoteBookCard(
    noteBookName: String ,
    totalOwe: Double,
    totalCollect: Double,
    onCardClick : () -> Unit,
) {
    val extendedColorScheme = LocalExtendedColors.current
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Subtle shadow
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_book),
                contentDescription = "Notebook Icon",
                modifier = Modifier
                    .align(Alignment.Top)
                    .size(60.dp)
                    .padding(top = 12.dp)
                    .padding(horizontal = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = noteBookName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Total Collect: ₹${totalCollect}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = extendedColorScheme.green.color
                )
                Text(
                    text = "Total Owe: ₹${totalOwe}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = extendedColorScheme.red.color
                )
            }

            IconButton(onClick =  onCardClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "View Details",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}


@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, device = Devices.NEXUS_7_2013)
@Preview(showBackground = true, device = Devices.NEXUS_7_2013, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun NoteBookScreenPreview() {
    NoteBookUi(
        uiState = NoteBookUiState(),
        onEvent = {}
    )
}