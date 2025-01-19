package com.example.udhari.ui.entityDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.ExtendedColorScheme
import com.example.compose.LocalExtendedColors
import com.example.udhari.data.entity.PendingTransaction
import com.example.udhari.data.entity.TransactionType

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
    var extendedColorScheme = LocalExtendedColors.current
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
                .padding(horizontal = 20.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                ) {
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
                            Text(uiState.entity.name, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                uiState.entity.phoneNumber,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp)
                        .padding(start = 16.dp, end = 12.dp)
                ) {
//                    val totalstr = if (uiState.totalAmount < 0) {
//                        "Overall balance"
//                    } else {
//                        "They owe you"
//                    }
                    val textColor = if (uiState.totalAmount < 0) {
                        extendedColorScheme.red.color
                    } else {
                        extendedColorScheme.green.color
                    }
                    Text(
                        "Total Amount:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                    )
                    Spacer(Modifier.weight(1f))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text(
                            "${uiState.totalAmount}",
                            modifier = Modifier.padding(vertical = 0.dp, horizontal = 12.dp),
                            style = MaterialTheme.typography.titleLarge,
                            color = textColor
                        )
                    }

                }
            }
            Box(modifier = Modifier.fillMaxSize()) {

                LazyColumn(modifier = Modifier) {
                    item {
                        Text("Transactions")
                    }
                    items(items = uiState.listOfPendingTransaction) { transaction ->
                        TransactionCard(
                            type = transaction.type,
                            amount = transaction.amount.toString(),
                            description = transaction.description,
                            extendedColorScheme = extendedColorScheme,
                            onDelete = { onEvent(EntityDetailsUiEvent.DeleteTransaction(transaction)) }
                        )
                    }
                }

                FloatingActionButton(
                    onClick = {
                        globalNavController.navigate("transactionForm/${uiState.entityId}")
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 35.dp, end = 0.dp)
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

@Composable
fun TransactionCard(
    type: TransactionType,
    amount: String,
    description: String,
    extendedColorScheme: ExtendedColorScheme,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
            if (type == TransactionType.OWE) extendedColorScheme.red.colorContainer else extendedColorScheme.green.colorContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = amount,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onDelete() }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                )
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
