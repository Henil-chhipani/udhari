package com.example.udhari.ui.noteBookDetails.addingEntity

import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import com.example.compose.LocalExtendedColors
import com.example.udhari.R
import com.example.udhari.navigation.GlobalNavController
import com.example.udhari.ui.commonCoponents.BackBtn
import com.example.udhari.ui.commonCoponents.TextInput
import com.example.udhari.ui.commonCoponents.ToastMessage
import com.example.udhari.ui.commonCoponents.TopBar

@Composable
fun AddEntityScreen(
    noteBookId: Int,
    entityId: Int = -1,
    addingEnftityViewModel: AddingEnftityViewModel = hiltViewModel()
) {
    val uiState by addingEnftityViewModel.addingEntityUiState.collectAsState()

    LaunchedEffect(Unit) {
        if (entityId != -1) {
            addingEnftityViewModel.onAddingEntitiyEvent(
                AddingEntityEvent.SetEntityIdAndNotebookId(
                    entityId = entityId,
                    noteBookId = noteBookId
                )
            )
        } else {
            addingEnftityViewModel.onAddingEntitiyEvent(AddingEntityEvent.SetNoteBookId(noteBookId))
        }
    }
    AddEntityScreenUi(
        uiState = uiState,
        onEvent = addingEnftityViewModel::onAddingEntitiyEvent
    )
}

@Composable
fun AddEntityScreenUi(
    uiState: AddingEntityUiState,
    onEvent: (AddingEntityEvent) -> Unit,
) {
    val globalNavController = GlobalNavController.current
    LaunchedEffect(Unit) {
        onEvent(AddingEntityEvent.SetGlobalNavController(globalNavController))
    }
    Scaffold(

        topBar = {
            TopBar(
                title = {
                    Text(text = "Create entity", style = MaterialTheme.typography.titleLarge)
                }, icon = {
                    BackBtn()
                },
                onIconClick = {
                    globalNavController.popBackStack()
                }
            )
//            AddEntityTopBar()
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                    TextInput(
                        value = uiState.name,
                        onValueChange = { onEvent(AddingEntityEvent.AddingEntityName(it)) },
                        label = "Add Name",
                        keyBoardType = KeyboardType.Text,
                        modifier = Modifier.padding(vertical = 10.dp),
                        isError = uiState.isNameEmpty,
                        errorMessage = uiState.nameError
                    )

                    TextInput(
                        value = uiState.phoneNumber,
                        onValueChange = { onEvent(AddingEntityEvent.AddingEntityPhoneNumber(it)) },
                        label = "Add Phone Number",
                        keyBoardType = KeyboardType.Number,
                        modifier = Modifier.padding(vertical = 10.dp),
                        isError = uiState.isPhoneNumberEmpty,
                        errorMessage = uiState.phoneNumberError
                    )

                    FloatingActionButton(
                        onClick = {
                            if (uiState.isUpdate) {
                                onEvent(AddingEntityEvent.UpdateEntity)
                            } else {
                                onEvent(AddingEntityEvent.InsertEntity)
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
                            onEvent(AddingEntityEvent.StopVoiceRecognition)
                        } else {
                            onEvent(AddingEntityEvent.StartVoiceRecognition)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 15.dp)
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
                    if (uiState.isVoiceRecognitionStart) {
                        Icon(
                            painter = painterResource(R.drawable.ic_stop),
                            contentDescription = "Voice Command",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.ic_mic),
                            contentDescription = "Voice Command",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

            }


        },
        bottomBar = { }
    )
}


@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, device = Devices.NEXUS_7_2013)
@Preview(showBackground = true, device = Devices.NEXUS_7_2013, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun AddEntityScreenUiPreview() {
    AddEntityScreenUi(
        uiState = AddingEntityUiState(),
        onEvent = {}
    )
}