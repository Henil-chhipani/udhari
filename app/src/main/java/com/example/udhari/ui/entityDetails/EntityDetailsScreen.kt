package com.example.udhari.ui.entityDetails

import android.Manifest
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.ExtendedColorScheme
import com.example.compose.LocalExtendedColors
import com.example.udhari.R
import com.example.udhari.data.entity.PendingTransaction
import com.example.udhari.data.entity.TransactionType
import com.example.udhari.ui.commonCoponents.NotFoundCard
import com.example.udhari.ui.commonCoponents.ToastMessage
import com.example.udhari.ui.noteBookDetails.NoteBookDetailsEvent
import com.example.udhari.ui.notebook.NoteBookEvent
import com.example.udhari.utils.PermissionUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EntityDetailsScreen(
    entityId: Int,
    noteBookId: Int,
    viewModel: EntityDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val onEvent = viewModel::onEvent
    val requiredPermissions =
        listOf(
            Manifest.permission.CALL_PHONE,
        )
    val permissionState = rememberMultiplePermissionsState(permissions = requiredPermissions)

    LaunchedEffect(permissionState) {
        if (!PermissionUtils.checkArrayOfPermission(context, requiredPermissions)) {
            permissionState.launchMultiplePermissionRequest()
            onEvent(EntityDetailsUiEvent.CallButtonEnable(false))
        } else {
            onEvent(EntityDetailsUiEvent.CallButtonEnable(true))
        }
    }

    LaunchedEffect(Unit) {
        onEvent(EntityDetailsUiEvent.AddId(entityId, noteBookId))

    }
    EntityDetailsUi(uiState = uiState, onEvent = viewModel::onEvent)
}

@Composable
fun EntityDetailsUi(
    uiState: EntityDetailsUiState,
    onEvent: (EntityDetailsUiEvent) -> Unit
) {
    var context = LocalContext.current
    var globalNavController = GlobalNavController.current
    var extendedColorScheme = LocalExtendedColors.current
    val lazyListState = rememberLazyListState()
    val isScrollingUp by remember {
        derivedStateOf {
            // Track the scroll direction by comparing the current and previous scroll offsets
            lazyListState.firstVisibleItemScrollOffset <= (lazyListState.layoutInfo.visibleItemsInfo.firstOrNull()?.offset
                ?: 0)
        }
    }

    LaunchedEffect(Unit) {
        onEvent(EntityDetailsUiEvent.SetGlobalNavController(globalNavController))
    }

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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            LazyColumn(
                state = lazyListState,
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(start = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Entity Details",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(vertical = 0.dp)
                        )
                        IconButton(onClick = {
                            onEvent(EntityDetailsUiEvent.NavigateUpdateEntity)
                        }, modifier = Modifier) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "edit button",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
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
                            Row {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            color = Color.Gray,
                                            shape = RoundedCornerShape(20.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val firstChar =
                                        uiState.entity.name.firstOrNull()?.toString() ?: "N/A"
                                    Text(
                                        text = firstChar,
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.padding(horizontal = 7.dp))
                                Column {
                                    Text(
                                        uiState.entity.name,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        uiState.entity.phoneNumber,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    enabled = uiState.callButtonEnable,
                                    onClick = {
                                        if (uiState.callButtonEnable) {
                                            onEvent(EntityDetailsUiEvent.InitiateCall(context))
                                        }
                                    }) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        modifier = Modifier.padding(end = 4.dp),
                                        tint = if(uiState.callButtonEnable){
                                            Color.White
                                        }else{
                                            Color.Gray
                                        }
                                    )
                                }
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
                                    modifier = Modifier.padding(
                                        vertical = 0.dp,
                                        horizontal = 12.dp
                                    ),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = textColor
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Transactions",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding()
                    )
                }
                item {
                    if (uiState.isListEmpty) {
                        NotFoundCard(
                            titleTexts = "No Transaction Found",
                            bodyTexts = "Click on the + button to add a new transaction"
                        )
                    }
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
            if (uiState.resultMessage != "") {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 100.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        )
                ) {
                    Text(uiState.resultMessage, modifier = Modifier.padding(10.dp))
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 124.dp)
            ) {
                ToastMessage()
            }
            AnimatedVisibility(
                visible = isScrollingUp,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 15.dp, start = 20.dp, end = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FloatingActionButton(
                        onClick = {
                            if (uiState.isVoiceRecognitionStart) {
                                onEvent(EntityDetailsUiEvent.StopVoiceRecognition)
                            } else {
                                onEvent(EntityDetailsUiEvent.StartVoiceRecognition)
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .shadow(
                                elevation = 8.dp,
                                shape = CircleShape
                            )
                            .size(56.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            ),
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 12.dp
                        )
                    ) {
                        Crossfade(
                            targetState = uiState.isVoiceRecognitionStart,
                            label = ""
                        ) { isRecording ->
                            if (isRecording) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_stop),
                                    contentDescription = "Stop Voice Command",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.ic_mic),
                                    contentDescription = "Start Voice Command",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }

                    FloatingActionButton(
                        onClick = {
                            globalNavController.navigate("transactionForm/${uiState.noteBookId}/${uiState.entityId}")
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
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
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.width(230.dp)
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
