package com.example.udhari.ui.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.udhari.navigation.GlobalNavController
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()
    HomeScreenUi(
        uiState = uiState,
        onEvent = homeViewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenUi(
    uiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
) {
    var extendedColorScheme = LocalExtendedColors.current
    var cardContainerColor = extendedColorScheme.green.colorContainer
    var uiFontColor = MaterialTheme.colorScheme.onPrimaryContainer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var globalNavController = GlobalNavController.current
    fun openDrawer() {
        scope.launch {
            drawerState.open()

        }
    }

    HomeMenuDrawer(drawerState = drawerState, content = {
        Scaffold(
            topBar = {
                TopBar(
                    title = {
                        AppIcon()
                    },
                    onIconClick = {
                        openDrawer()
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            },
            content = { innerPadding ->
                Column(modifier = Modifier.fillMaxSize()) {

                    HomeCard(paddingValues = innerPadding, amount = uiState.totalAmount)
                    Text(
                        "All Entity",
                        style = MaterialTheme.typography.titleMedium,
                        color = uiFontColor,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    LazyColumn {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
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
                                        Text(
                                            text = "D",
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Dixit Jadav (Manthra)",
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
                                            text = "₹ 463",
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

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        FloatingActionButton(
                            onClick = {
                                globalNavController.navigate("addEntity")
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 55.dp, end = 20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Icon",
                            )
                        }
                    }
                }
            }
        )
    })
}

@Composable
fun HomeMenuDrawer(
    content: @Composable () -> Unit,
    drawerState: DrawerState
) {
    ModalNavigationDrawer(
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.width(200.dp)
                ) {
                    Text(
                        "NoteBook Names",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp)
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                   Row(){
                    Text(
                        "NoteBook1",
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp)
                    )
                       IconButton(onClick = {

                       }) {
                           Icon(Icons.Outlined.Edit, contentDescription = "Edit")
                       }
                   }

                    IconButton(onClick = {

                    }) {
                        Icon(Icons.Outlined.Add, contentDescription = "Add")
                    }

                }
            }
        },
        content = content,
        drawerState = drawerState,
    )
}


@Composable
fun HomeCard(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    amount: Int
) {
// You are cash-flow positive. Others owe you [amount] or you are cash-flow negative You owe [amount]
    var extendedColorScheme = LocalExtendedColors.current
    var cardContainerColor = extendedColorScheme.green.colorContainer
//    var cardFontColor = MaterialTheme.colorScheme.onSurface
    var cardFontColor = extendedColorScheme.green.onColorContainer
    var cardString = "You are cash-flow positive\nYou owe around:"
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
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 35.sp)
                )
                Icon(
                    painter = painterResource(R.drawable.ic_rupee),
                    contentDescription = "Rupee",
                    tint = cardFontColor,
                    modifier = Modifier.size(30.dp)
                )
            }
            Text(
                "20 transactions remaining",
                style = MaterialTheme.typography.bodySmall,
                color = cardFontColor
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
        HomeScreenUi(
            uiState = HomeUiState(),
            onEvent = {}
        )
    }
}