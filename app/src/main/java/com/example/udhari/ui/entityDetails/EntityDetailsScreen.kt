package com.example.udhari.ui.entityDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.udhari.navigation.GlobalNavController
import com.example.udhari.ui.commonCoponents.AppIcon
import com.example.udhari.ui.commonCoponents.BackBtn
import com.example.udhari.ui.commonCoponents.TopBar
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EntityDetailsScreen(
    entityId: Int,
    viewModel: EntityDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val onEvent = viewModel::onEvent
    LaunchedEffect(Unit) {
        onEvent(EntityDetailsUiEvent.AddEntityId(entityId))
    }
    EntityDetailsUi(uiState = uiState, onEvent = viewModel::onEvent)
}

@Composable
fun EntityDetailsUi(
    uiState: EntityDetailsUiState,
    onEvent: (EntityDetailsUiEvent) -> Unit
) {
    var globalNavController = GlobalNavController.current

    Scaffold(
        topBar = {
            TopBar(
                onIconClick = {
                    globalNavController.popBackStack()
                },
                title = {
                    AppIcon()
                },
                icon = {
                    BackBtn()
                }
            )
        }
    ) { innerpadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerpadding)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {

                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 30.dp)) {
                    Row() {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = Color.Gray,
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            val firstChar = uiState.entity.name.firstOrNull()?.toString() ?: "N/A"
                            Text(
                                text = firstChar,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.padding(horizontal = 7.dp))
                        Column {
                            Text(uiState.entity.name)
                            Text(uiState.entity.phoneNumber)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }
            }
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(modifier = Modifier.padding()) {
                    items(items = uiState.listOfPendingTransaction) { transaction ->
                        Card(
                            modifier = Modifier.padding(innerpadding)
                        ) {
                            Text("${transaction.id}")
                        }
                    }
                }

                FloatingActionButton(
                    onClick = {
                        globalNavController.navigate("transactionForm/${uiState.entityId}")
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
        }
    }
}

@Preview
@Composable
fun ProfileUiPreview() {
    EntityDetailsUi(
        uiState = EntityDetailsUiState(),
        onEvent = {}
    )
}
