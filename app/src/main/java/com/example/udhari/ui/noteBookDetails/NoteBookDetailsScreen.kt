package com.example.udhari.ui.noteBookDetails

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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compose.AppTheme
import com.example.compose.LocalExtendedColors
import com.example.udhari.R
import com.example.udhari.ui.commonCoponents.AppIcon
import com.example.udhari.ui.commonCoponents.TopBar
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import com.example.udhari.navigation.GlobalNavController
import com.example.udhari.ui.commonCoponents.BackBtn
import com.example.udhari.ui.commonCoponents.NotFoundCard
import com.example.udhari.ui.commonCoponents.ToastMessage
import com.example.udhari.ui.notebook.NoteBookEvent
import kotlinx.coroutines.launch

@Composable
fun NoteBookDetailsScreen(
    viewModel: NoteBookDetailsViewModel = hiltViewModel(),
    noteBookId: Int
) {
    val homeUiState by viewModel.homeUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setNoteBookId(noteBookId)
    }
    if (homeUiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        }
    } else {
        NoteBookDetailsScreenUi(
            uiState = homeUiState,
            onEvent = viewModel::onEvent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteBookDetailsScreenUi(
    uiState: NoteBookDetailsUiState,
    onEvent: (NoteBookDetailsEvent) -> Unit,
) {
    val extendedColorScheme = LocalExtendedColors.current
    val uiFontColor = MaterialTheme.colorScheme.onPrimaryContainer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val selectedEntity = remember { mutableStateListOf<Int>() }
    val globalNavController = GlobalNavController.current
    val lazyListState = rememberLazyListState()
    val isScrollingUp by remember {
        derivedStateOf {
            // Track the scroll direction by comparing the current and previous scroll offsets
            lazyListState.firstVisibleItemScrollOffset <= (lazyListState.layoutInfo.visibleItemsInfo.firstOrNull()?.offset
                ?: 0)
        }
    }
    val previousScrollOffset = remember { mutableStateOf(0) }


    LaunchedEffect(Unit) {
//        onEvent(NoteBookDetailsEvent.FetchFinanceEntity)
//        onEvent(NoteBookDetailsEvent.FetchTotalAmount)
        onEvent(NoteBookDetailsEvent.SetGlobalNavController(globalNavController))
    }

    Scaffold(
        topBar = {
            TopBar(
                title = {
                    if (selectedEntity.isEmpty()) {
                        AppIcon()
                    } else {
                        Text(text = "${selectedEntity.size} Selected")
                    }
                },
                onIconClick = {
                    if (selectedEntity.isEmpty()) {
                        globalNavController.popBackStack()
                    } else {
                        onEvent(NoteBookDetailsEvent.DeleteSelectedEntity(selectedEntity))
                        selectedEntity.clear()
                    }
                },
                icon = {
                    if (selectedEntity.isEmpty()) {
                        BackBtn()

                    } else {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Notebooks"
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                LazyColumn(
                    state = lazyListState,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
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
                                "NoteBook Details",
                                style = MaterialTheme.typography.titleLarge
                            )
                            IconButton(onClick = {
                                onEvent(NoteBookDetailsEvent.NavigateUpdateNoteBook(uiState.selectedNoteBookId))
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
                        HomeCard(
                            amount = uiState.totalAmount,
                            totalCollect = uiState.totalCollect,
                            totalOwe = uiState.totalOwe,
                            noteBookName = uiState.noteBook.name
                        )
                    }
                    item {
                        Text(
                            "All Entity",
                            style = MaterialTheme.typography.titleLarge,
                            color = uiFontColor,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                    item {
                        if (uiState.listOfFinanceEntity.isEmpty()) {
                            NotFoundCard(
                                titleTexts = "No Entity Found",
                                bodyTexts = "Click on the + button to add a new entity",
                            )
                        }
                    }
                    items(items = uiState.listOfFinanceEntity) { entity ->
                        if (uiState.listOfFinanceEntity.isNotEmpty()) {
                            val isSelected = selectedEntity.contains(entity.id)
                            EntityCard(
                                name = entity.name,
                                amount = (entity.totalCollect - entity.totalOwe).toString(),
                                isSelected = isSelected,
                                onCardClick = {
                                    if (selectedEntity.isEmpty()) {
                                        globalNavController.navigate("entityDetails/${uiState.selectedNoteBookId}/${entity.id}") {
                                            launchSingleTop = true
                                        }
                                    } else {
                                        toggleSelection(selectedEntity, entityId = entity.id)
                                    }
                                },
                                onLongPress = {
                                    toggleSelection(selectedEntity, entityId = entity.id)
                                }
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 124.dp)
                ) {
                    ToastMessage()
                }
                // Animated Visibility for Floating Action Buttons
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
                                    onEvent(NoteBookDetailsEvent.StopVoiceRecognition)
                                } else {
                                    onEvent(NoteBookDetailsEvent.StartVoiceRecognition)
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            elevation = FloatingActionButtonDefaults.elevation(
                                defaultElevation = 8.dp,
                                pressedElevation = 12.dp,
                            ),
                            modifier = Modifier
                                .shadow(
                                    elevation = 8.dp,
                                    shape = CircleShape
                                )
                                .size(56.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
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
                                globalNavController.navigate("addEntity/${uiState.selectedNoteBookId}"){
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            elevation = FloatingActionButtonDefaults.elevation(
                                defaultElevation = 8.dp,
                                pressedElevation = 12.dp
                            ),
                            modifier = Modifier
                                .shadow(
                                    elevation = 8.dp,
                                    shape = CircleShape
                                )
                                .size(56.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
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
        }

    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EntityCard(
    name: String,
    amount: String,
    isSelected: Boolean,
    onCardClick: () -> Unit,
    onLongPress: () -> Unit,

    ) {
    val extendedColorScheme = LocalExtendedColors.current
    Card(
        modifier = Modifier
            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else Color.Transparent)
            .fillMaxWidth()
            .combinedClickable(
                onClick = onCardClick,
                onLongClick = onLongPress,
            )
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else Color.Gray,
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                val firstChar = name.first()
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Done",
                        tint = MaterialTheme.colorScheme.surface
                    )
                } else {
                    Text(
                        text = firstChar.toString(),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "18 months ago",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
//                    text = (entity.totalCollect - entity.totalOwe).toString(),
                    text = amount,
                    color = extendedColorScheme.red.color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "REMIND",
                    color = Color.Blue,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}


@Composable
fun HomeCard(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    amount: Double,
    totalOwe: Double,
    totalCollect: Double,
    noteBookName: String
) {
// You are cash-flow positive. Others owe you [amount] or you are cash-flow negative You owe [amount]
    val extendedColorScheme = LocalExtendedColors.current
    val cardContainerColor =
        if (amount >= 0) extendedColorScheme.green.colorContainer else extendedColorScheme.red.colorContainer
//    var cardFontColor = MaterialTheme.colorScheme.onSurface
    val cardFontColor =
        if (amount >= 0) extendedColorScheme.green.onColorContainer else extendedColorScheme.red.onColorContainer
    val cardString =
        if (amount >= 0) "You are cash-flow positive\nOthers owe you:" else "You are cash-flow negative\nYou owe others :"
    Card(
        modifier = Modifier
            .padding(top = paddingValues.calculateTopPadding())
            .padding(bottom = 10.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = cardContainerColor,
            contentColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {
            Text(
                noteBookName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    cardString,
                    modifier = Modifier.width(200.dp),
                    color = cardFontColor,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.weight(1f))
                // intoduce alias like 100k or 1M if trasectiion react that stage
                Text(
                    formatAmount(amount),
                    color = cardFontColor,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
                )
                Icon(
                    painter = painterResource(R.drawable.ic_rupee),
                    contentDescription = "Rupee",
                    tint = cardFontColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                "Total Owe: ${totalOwe}",
                color = extendedColorScheme.red.color,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                "Total Collect: ${totalCollect}",
                color = extendedColorScheme.green.color,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}


fun formatAmount(amount: Double): String {
    return when {
        amount >= 1_000_000 || amount <= -1_000_000 -> "${amount / 1_000_000}M" // Convert to millions
        amount >= 1_000 || amount <= -1_000 -> "${amount / 1_000}k"             // Convert to thousands
        else -> amount.toString()                                              // Use the original value
    }
}

fun toggleSelection(selectedNoteBooks: MutableList<Int>, entityId: Int) {
    if (entityId in selectedNoteBooks) {
        selectedNoteBooks.remove(entityId)
    } else {
        selectedNoteBooks.add(entityId)
    }
}


@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, device = Devices.NEXUS_7_2013)
@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun HomeScreenPreview() {
    AppTheme {
        NoteBookDetailsScreenUi(
            uiState = NoteBookDetailsUiState(),
            onEvent = {}
        )
    }
}