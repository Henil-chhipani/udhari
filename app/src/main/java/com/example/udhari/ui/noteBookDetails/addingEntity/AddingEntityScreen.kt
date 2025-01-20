package com.example.udhari.ui.noteBookDetails.addingEntity

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.LocalExtendedColors
import com.example.udhari.navigation.GlobalNavController
import com.example.udhari.ui.commonCoponents.BackBtn
import com.example.udhari.ui.commonCoponents.TopBar

@Composable
fun AddEntityScreen(
    noteBookId: Int,
    addingEnftityViewModel: AddingEnftityViewModel = hiltViewModel()
) {
    val uiState by addingEnftityViewModel.addingEntityUiState.collectAsState()

    LaunchedEffect(Unit) {
        addingEnftityViewModel.onAddingEntitiyEvent(AddingEntityEvent.SetNoteBookId(noteBookId))
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
    LaunchedEffect(Unit) {
        onEvent(AddingEntityEvent.FetchFinanceEntity)
    }
    val globalNavController = GlobalNavController.current
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
        content = { ineerpadding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ineerpadding)
                    .padding(horizontal = 20.dp),
            ) {
                Column(
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
                            onEvent(AddingEntityEvent.InsertEntity)
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
                LazyColumn {
                    items(items = uiState.listOfFinanceEntity) { entity ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp,
                                pressedElevation = 1.dp,
                                focusedElevation = 3.dp,
                                hoveredElevation = 4.dp,
                                draggedElevation = 5.dp
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = entity.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = entity.phoneNumber,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { /* Handle Edit Action */ },
                                        modifier = Modifier.size(32.dp) // Uniform size for buttons
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            modifier = Modifier.size(20.dp),
                                            tint = MaterialTheme.colorScheme.primary // Themed color for the icon
                                        )
                                    }
                                    IconButton(
                                        onClick = { /* Handle Delete Action */ },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            modifier = Modifier.size(20.dp),
                                            tint = MaterialTheme.colorScheme.error // Error color for delete
                                        )
                                    }
                                }
                            }
                        }
                    }

                }
            }


        },
        bottomBar = { }
    )
}

@Composable
fun TextInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyBoardType: KeyboardType,
    isError: Boolean = false,
    errorMessage: String = "",
) {
    val extendedColorScheme = LocalExtendedColors.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = {
            Text(text = label)
        },
        textStyle = MaterialTheme.typography.bodyLarge,
        readOnly = readOnly,
        enabled = enabled,
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyBoardType,
        ),
        supportingText = {
            if (isError) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = extendedColorScheme.red.onColorContainer
                )
            }
        },
        isError = isError,
        colors = TextFieldDefaults.colors(
            errorContainerColor = extendedColorScheme.red.colorContainer,
            errorTextColor = extendedColorScheme.red.onColorContainer
        )
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