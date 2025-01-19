package com.example.udhari.ui.entityDetails.transactionForm

import android.annotation.SuppressLint
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.provider.MediaStore.Audio.Radio
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compose.AppTheme
import com.example.compose.LocalExtendedColors
import com.example.udhari.data.entity.TransactionType
import com.example.udhari.navigation.GlobalNavController
import com.example.udhari.ui.addingEntity.AddingEntityEvent
import com.example.udhari.ui.commonCoponents.BackBtn
import com.example.udhari.ui.commonCoponents.TopBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.sin
import kotlin.math.truncate


@Composable
fun TransactionForm(entityId: Int, viewModel: TransactionFormViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.onEvent(TransactionFormEvent.SetEntityId(entityId))
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
    LaunchedEffect(Unit) {
        onEvent(TransactionFormEvent.FetchNoteBookId)
        onEvent(TransactionFormEvent.GetTodayDate)
    }
    LaunchedEffect(uiState.listOfTransaction) {
        onEvent(TransactionFormEvent.FetchTransactions)
    }
    val globalNavController = GlobalNavController.current
    var extendedColorScheme = LocalExtendedColors.current
    var expanded by remember { mutableStateOf(false) }
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
                        value = uiState.amount,
                        onValueChange = { onEvent(TransactionFormEvent.AmountChanged(it)) },
                        label = "Add Amount",
                        keyBoardType = KeyboardType.Decimal,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    TextInput(
                        value = uiState.description,
                        onValueChange = { onEvent(TransactionFormEvent.DescriptionChanged(it)) },
                        label = "Add Payment description",
                        keyBoardType = KeyboardType.Text,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    var option = uiState.type?.name ?: "Select Transaction Type"
                    OutlinedTextField(
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
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                            disabledTextColor = if (option == "OWE") extendedColorScheme.red.color else if (option == "COLLECT") extendedColorScheme.green.color else MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledIndicatorColor = MaterialTheme.colorScheme.onSurface,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
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
                            onEvent(TransactionFormEvent.OnCrate)
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
    keyBoardType: KeyboardType
) {
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
        )
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