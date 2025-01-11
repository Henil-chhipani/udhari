package com.example.udhari.ui.addingEntity

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.udhari.R
import com.example.udhari.navigation.GlobalNavController
import com.example.udhari.ui.commonCoponents.BackBtn
import com.example.udhari.ui.commonCoponents.TopBar

@Composable
fun AddEntityScreen(

) {
    AddEntityScreenUi()
}

@Composable
fun AddEntityScreenUi() {
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
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ineerpadding)
                    .padding(horizontal = 20.dp),
            ) {

                TextInput(
                    value = "",
                    onValueChange = {},
                    label = "Add Name",
                    keyBoardType = KeyboardType.Text,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                TextInput(
                    value = "",
                    onValueChange = {},
                    label = "Add Phone Number",
                    keyBoardType = KeyboardType.Number,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                FloatingActionButton(
                    onClick = {},
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
fun AddEntityScreenUiPreview() {
    AddEntityScreenUi()
}