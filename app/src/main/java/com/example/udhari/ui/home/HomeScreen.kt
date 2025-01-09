package com.example.udhari.ui.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compose.AppTheme
import com.example.compose.LocalExtendedColors
import com.example.udhari.R

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
    Scaffold(
        topBar = {
            HomeTopBar()
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
                        Card {

                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    FloatingActionButton(
                        onClick = { },
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(26.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onMenuIconClick: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        modifier = Modifier.padding(horizontal = 5.dp),
        title = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(text = "उधारी", style = MaterialTheme.typography.titleLarge)
                Icon(
                    painter = painterResource(R.drawable.ic_note),
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onMenuIconClick
            ) {

                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {},
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState()),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.primary,
        )
    )
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