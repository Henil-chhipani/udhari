package com.example.udhari.ui.notebook.addingNoteBook

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.udhari.navigation.GlobalNavController
import com.example.udhari.ui.noteBookDetails.addingEntity.AddEntityScreenUi
import com.example.udhari.ui.commonCoponents.AppIcon
import com.example.udhari.ui.commonCoponents.BackBtn
import com.example.udhari.ui.commonCoponents.TopBar
import com.example.udhari.ui.noteBookDetails.addingEntity.AddingEntityEvent
import com.example.udhari.ui.noteBookDetails.addingEntity.TextInput
import com.example.udhari.ui.notebook.NoteBookUi
import com.example.udhari.ui.notebook.NoteBookUiState

@Composable
fun AddNoteBookScreen(
    viewModel: AddingNoteBookViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    AddNoteBookUi(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun AddNoteBookUi(
    uiState: AddingNoteBookUiState = AddingNoteBookUiState(),
    onEvent: (AddingNoteBookUiEvent) -> Unit
) {
    val globalNavController = GlobalNavController.current
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
        content = { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextInput(
                    modifier = Modifier.padding(vertical = 10.dp),
                    value = uiState.noteBookNameString,
                    onValueChange = { onEvent(AddingNoteBookUiEvent.UpdateNoteBookNameString(it)) },
                    label = "NoteBook Name",
                    keyBoardType = KeyboardType.Text,
                    isError = uiState.isNoteBookEmpty,
                    errorMessage = "NoteBook Name can't be empty"
                )

                FloatingActionButton(
                    onClick = {
                        onEvent(AddingNoteBookUiEvent.InsertNoteBook)
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 20.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 15.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .padding(horizontal = 5.dp)
                        )
                        Text(text = "Create", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        bottomBar = {

        }
    )
}


@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, device = Devices.NEXUS_7_2013)
@Preview(showBackground = true, device = Devices.NEXUS_7_2013, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun AddNoteBookScreenUiPreview() {
    AddNoteBookUi(
        uiState = AddingNoteBookUiState(),
        onEvent = {}
    )
}