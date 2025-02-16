package com.example.udhari.ui.entityDetails.transactionForm

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compose.AppTheme
import com.example.compose.LocalExtendedColors
import com.example.udhari.R
import com.example.udhari.data.entity.TransactionType
import com.example.udhari.navigation.GlobalNavController
import com.example.udhari.ui.commonCoponents.BackBtn
import com.example.udhari.ui.commonCoponents.TextInput
import com.example.udhari.ui.commonCoponents.ToastMessage
import com.example.udhari.ui.commonCoponents.TopBar


@Composable
fun TransactionForm(
    noteBookId: Int,
    entityId: Int,
    viewModel: TransactionFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.onEvent(TransactionFormEvent.SetId(entityId = entityId, noteBookId = noteBookId))
    }
    TransactionFromUi(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun TransactionFromUi(
    uiState: TransactionFormUiState,
    onEvent: (TransactionFormEvent) -> Unit
) {
    val globalNavController = GlobalNavController.current
    var extendedColorScheme = LocalExtendedColors.current
    var expanded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        onEvent(TransactionFormEvent.GetTodayDate)
        onEvent(TransactionFormEvent.SetGlobalNavController(globalNavController))
    }

    Scaffold(
        topBar = {
            TopBar(
                title = {
                    Text(text = "Add Transaction", style = MaterialTheme.typography.titleLarge)
                }, icon = {
                    BackBtn()
                },
                onIconClick = {
                    globalNavController.popBackStack()
                }
            )
        },
        content = { paddingValue ->
            Box(
                modifier = Modifier
                    .padding(paddingValue)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    TextInput(
                        value = uiState.amount,
                        onValueChange = { onEvent(TransactionFormEvent.AmountChanged(it)) },
                        label = "Add Amount",
                        keyBoardType = KeyboardType.Decimal,
                        modifier = Modifier.padding(vertical = 10.dp),
                        isError = uiState.isAmountError,
                        errorMessage = uiState.amountError
                    )

                    TextInput(
                        value = uiState.description,
                        onValueChange = { onEvent(TransactionFormEvent.DescriptionChanged(it)) },
                        label = "Add Payment description",
                        keyBoardType = KeyboardType.Text,
                        modifier = Modifier.padding(vertical = 10.dp),
                        isError = uiState.isDescriptionError,
                        errorMessage = uiState.descriptionError
                    )

                    var option = uiState.type?.name ?: "Select Transaction Type"
                    OutlinedTextField(
                        isError = uiState.isTypeError,
                        supportingText = {
                            if (uiState.isTypeError) {
                                Text(
                                    text = uiState.typeError,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = extendedColorScheme.red.onColorContainer
                                )
                            }
                        },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        value = option,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Transaction type") },
                        enabled = false,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                // Populate the dropdown with enum options
                                TransactionType.values().forEach { transactionType ->
                                    DropdownMenuItem(
                                        onClick = {
                                            onEvent(TransactionFormEvent.TypeChange(transactionType))
                                            expanded = false
                                        },
                                        text = {
                                            Text(
                                                text = transactionType.name,
                                                color = if (transactionType.name == "OWE") extendedColorScheme.red.color else extendedColorScheme.green.color
                                            )
                                        })
                                }
                            }
                        },
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .clickable { expanded = true },
                        colors = TextFieldDefaults.colors(
                            disabledContainerColor = if(uiState.isTypeError) extendedColorScheme.red.colorContainer else MaterialTheme.colorScheme.surface,
                            disabledTextColor = if (option == "OWE") extendedColorScheme.red.color else if (option == "COLLECT") extendedColorScheme.green.color else MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledIndicatorColor = MaterialTheme.colorScheme.onSurface,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )

                    OutlinedTextField(
                        value = uiState.date,
                        modifier = Modifier.padding(vertical = 10.dp),
                        readOnly = true,
                        enabled = false,
                        onValueChange = {},
                        colors = TextFieldDefaults.colors(
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledIndicatorColor = MaterialTheme.colorScheme.onSurface,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                    )

                    FloatingActionButton(
                        onClick = {
                            onEvent(TransactionFormEvent.InsertTransaction)
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
                    ToastMessage()
                }

                FloatingActionButton(
                    onClick = {
                        if (uiState.isVoiceRecognitionStart) {
                            onEvent(TransactionFormEvent.StopVoiceRecognition)
                        } else {
                            onEvent(TransactionFormEvent.StartVoiceRecognition)
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
        bottomBar = { }
    )
}


@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, device = Devices.NEXUS_7_2013)
@Preview(showBackground = true, device = Devices.NEXUS_7_2013, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun TransactionFormUiPreview() {
    AppTheme {
        TransactionFromUi(
            uiState = TransactionFormUiState(),
            onEvent = {}
        )
    }
}