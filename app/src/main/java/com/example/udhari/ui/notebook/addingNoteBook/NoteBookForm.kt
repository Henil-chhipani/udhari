package com.example.udhari.ui.notebook.addingNoteBook

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.udhari.R
import com.example.udhari.navigation.GlobalNavController
import com.example.udhari.ui.commonCoponents.AppIcon
import com.example.udhari.ui.commonCoponents.BackBtn
import com.example.udhari.ui.commonCoponents.TextInput
import com.example.udhari.ui.commonCoponents.ToastMessage
import com.example.udhari.ui.commonCoponents.TopBar

@Composable
fun NoteBookFormScreen(
    viewModel: NoteBookFormViewModel = hiltViewModel(),
    noteBookId: Int? = null,
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        if (noteBookId != null) {
            viewModel.onEvent(AddingNoteBookUiEvent.IsNoteBookUpdate(true, noteBookId = noteBookId))
        }
    }
    NoteBookFormUi(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun NoteBookFormUi(
    uiState: AddingNoteBookUiState = AddingNoteBookUiState(),
    onEvent: (AddingNoteBookUiEvent) -> Unit
) {
    val globalNavController = GlobalNavController.current

    LaunchedEffect(Unit) {
        onEvent(AddingNoteBookUiEvent.SetGlobalNavController(globalNavController))
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
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextInput(
                        modifier = Modifier.padding(vertical = 10.dp),
                        value = uiState.noteBookNameString,
                        onValueChange = { onEvent(AddingNoteBookUiEvent.UpdateNoteBookNameString(it)) },
                        label = "NoteBook Name",
                        keyBoardType = KeyboardType.Text,
                        isError = uiState.isNoteBookError,
//                        errorMessage = "NoteBook Name can't be empty"
                        errorMessage = uiState.noteBookErrorMessage
                    )


                    FloatingActionButton(
                        onClick = {
                            if (uiState.isUpdate) {
                                onEvent(AddingNoteBookUiEvent.UpdateNoteBook)
                            } else {
                                onEvent(AddingNoteBookUiEvent.InsertNoteBook)
                            }
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
                            if (uiState.isUpdate) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .padding(horizontal = 5.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .padding(horizontal = 5.dp)
                                )
                            }
                            Text(
                                text = if (uiState.isUpdate) "Update" else "Create",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    ToastMessage()
                }


                FloatingActionButton(
                    onClick = {
                        if (uiState.isVoiceRecognitionStart) {
                            onEvent(AddingNoteBookUiEvent.StopVoiceRecognition)
                        } else {
                            onEvent(AddingNoteBookUiEvent.StartVoiceRecognition)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 15.dp)
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
    NoteBookFormUi(
        uiState = AddingNoteBookUiState(),
        onEvent = {}
    )
}