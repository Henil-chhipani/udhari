package com.example.udhari.ui.notebook

import android.Manifest
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compose.LocalExtendedColors
import com.example.udhari.R
import com.example.udhari.navigation.GlobalNavController
import com.example.udhari.navigation.Routes
import com.example.udhari.ui.commonCoponents.AppIcon
import com.example.udhari.ui.commonCoponents.NotFoundCard
import com.example.udhari.ui.commonCoponents.ToastMessage
import com.example.udhari.ui.commonCoponents.TopBar
import com.example.udhari.utils.PermissionUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NoteBookScreen(
    viewModel: NoteBookViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val requiredPermissions =
        listOf(
            Manifest.permission.RECORD_AUDIO,
        )
    val permissionState = rememberMultiplePermissionsState(permissions = requiredPermissions)

    LaunchedEffect(permissionState) {
        if (!PermissionUtils.checkArrayOfPermission(context, requiredPermissions)) {
            permissionState.launchMultiplePermissionRequest()
        } else {
            Log.d("NoteBookScreen", "Permission Granted")
        }
    }
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
    val selectedNoteBooks = remember { mutableStateListOf<Int>() }
    val lazyListState = rememberLazyListState()
    val isScrollingUp by remember {
        derivedStateOf {
            // Track the scroll direction by comparing the current and previous scroll offsets
            lazyListState.firstVisibleItemScrollOffset <= (lazyListState.layoutInfo.visibleItemsInfo.firstOrNull()?.offset
                ?: 0)
        }
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        onEvent(NoteBookEvent.FetchNoteBooks)
        onEvent(NoteBookEvent.SetGlobalNavController(globalNavController))
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NoteBookDrawer()
        }
    ) {
        Scaffold(topBar = {
            TopBar(
                title = {
                    if (selectedNoteBooks.isEmpty()) {
                        AppIcon()
                    } else {
                        Text(text = "${selectedNoteBooks.size} Selected")
                    }
                },
                onIconClick = {
                    if (selectedNoteBooks.isNotEmpty()) {
                        onEvent(NoteBookEvent.DeleteNoteBooks(selectedNoteBooks))
                        selectedNoteBooks.clear()
                    } else {
                        coroutineScope.launch { drawerState.open() }
                    }
                },
                icon = {
                    if (selectedNoteBooks.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Notebooks"
                        )
                    } else {
                        Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Menu")
                    }
                }
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
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        state = lazyListState
                    ) {
                        item {
                            Text(
                                text = "Your Notebooks",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        item {
                            if (uiState.isListEmpty) {
                                NotFoundCard(
                                    titleTexts = "No Notebooks Found",
                                    bodyTexts = "Click on the + icon to add a new notebook"
                                )
                            }
                        }
                        items(items = uiState.listOfNoteBook) { notebook ->
                            val isSelected = notebook.id in selectedNoteBooks
                            NoteBookCard(
                                noteBookName = notebook.name,
                                totalOwe = notebook.totalOwe,
                                totalCollect = notebook.totalCollect,
                                isSelected = isSelected,
                                onCardClick = {
                                    if (selectedNoteBooks.isEmpty()) {
                                        Log.e("notebook id", "${notebook.id}")
                                        globalNavController.navigate("noteBookDetails/${notebook.id}") {
                                            launchSingleTop = true
                                        }
                                    } else {
                                        toggleSelection(selectedNoteBooks, notebook.id)
                                    }
                                },
                                onLongPress = {
                                    toggleSelection(selectedNoteBooks, notebook.id)
                                }
                            )
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
                                        onEvent(NoteBookEvent.StopVoiceRecognition)
                                    } else {
                                        onEvent(NoteBookEvent.StartVoiceRecognition)
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
                                    globalNavController.navigate(Routes.AddingNoteBook.route){
                                        launchSingleTop = true
                                        restoreState = true
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
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Icon",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            })
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteBookCard(
    noteBookName: String,
    totalOwe: Double,
    totalCollect: Double,
    isSelected: Boolean,
    onCardClick: () -> Unit,
    onLongPress: () -> Unit,
) {
    val extendedColorScheme = LocalExtendedColors.current
    Card(
        modifier = Modifier
            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = onCardClick,
                onLongClick = onLongPress
            ),
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    modifier = Modifier
                        .align(Alignment.Top)
                        .size(60.dp)
                        .padding(top = 12.dp)
                        .padding(horizontal = 16.dp),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "check Icon",
                )
            } else {
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
            }

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
        }
    }
}

fun toggleSelection(selectedNoteBooks: MutableList<Int>, notebookId: Int) {
    if (notebookId in selectedNoteBooks) {
        selectedNoteBooks.remove(notebookId)
    } else {
        selectedNoteBooks.add(notebookId)
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