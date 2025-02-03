package com.example.udhari.ui.noteBookDetails

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import com.example.udhari.navigation.GlobalNavController
import com.example.udhari.ui.commonCoponents.BackBtn
import com.example.udhari.ui.noteBookDetails.addingEntity.TextInput
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
    NoteBookDetailsScreenUi(
        uiState = homeUiState,
        onEvent = viewModel::onEvent
    )
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
    val globalNavController = GlobalNavController.current

    LaunchedEffect(Unit) {
        onEvent(NoteBookDetailsEvent.FetchFinanceEntity)
        onEvent(NoteBookDetailsEvent.FetchTotalAmount)
    }

    fun openDrawer() {
        scope.launch {
            drawerState.open()
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = {
                    AppIcon()
                },
                onIconClick = {
                    globalNavController.popBackStack()
                },
                icon = {
                    BackBtn()
                }
            )
        },
        content = { innerPadding ->
            Column(modifier = Modifier.fillMaxSize()) {

                HomeCard(
                    paddingValues = innerPadding,
                    amount = uiState.totalAmount,
                    totalCollect = uiState.totalOwe,
                    totalOwe = uiState.totalCollect
                )
                Text(
                    "All Entity",
                    style = MaterialTheme.typography.titleMedium,
                    color = uiFontColor,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    LazyColumn {

                        items(items = uiState.listOfFinanceEntity) { entity ->

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        globalNavController.navigate("entityDetails/${uiState.selectedNoteBookId}/${entity.id}")
                                    }
                                    .padding(8.dp)
                                    .padding(horizontal = 10.dp),
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
                                                color = Color.Gray,
                                                shape = RoundedCornerShape(20.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val firstChar = entity.name.first()
                                        Text(
                                            text = firstChar.toString(),
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = entity.name,
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
                                            text = (entity.totalCollect - entity.totalOwe).toString(),
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
                    }

                    FloatingActionButton(
                        onClick = {
                            globalNavController.navigate("addEntity/${uiState.selectedNoteBookId}")
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
        })
}


@Composable
fun HomeCard(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    amount: Double,
    totalOwe: Double,
    totalCollect: Double
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
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = cardContainerColor,
            contentColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 30.dp)) {
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
                    amount.toString(),
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