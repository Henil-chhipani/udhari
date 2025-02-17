package com.example.udhari.ui.guide

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.LocalExtendedColors
import com.example.udhari.R
import com.example.udhari.navigation.GlobalNavController
import com.example.udhari.navigation.Routes
import com.example.udhari.ui.about.YouTubeVideo
import com.example.udhari.ui.commonCoponents.BackBtn
import com.example.udhari.ui.commonCoponents.TopBar
import com.example.udhari.ui.notebook.NoteBookEvent

@Composable
fun VoiceFeatureGuideScreen() {
    VoiceFeatureGuideUi()
}

@Composable
fun VoiceFeatureGuideUi() {
    val globalNavController = GlobalNavController.current
    val extendedColorScheme = LocalExtendedColors.current
    Scaffold(
        topBar = {
            TopBar(
                icon = { BackBtn() },
                onIconClick = { globalNavController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {

                item {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {},
                            containerColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .shadow(
                                    elevation = 8.dp,
                                    shape = CircleShape
                                )
                                .size(40.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                ),
                            elevation = FloatingActionButtonDefaults.elevation(
                                defaultElevation = 8.dp,
                                pressedElevation = 12.dp
                            )
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_mic),
                                contentDescription = "Start Voice Command",
                                tint = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Voice Feature Guide", style = MaterialTheme.typography.titleLarge)
                    }
                    Text(
                        "Voice feature is used to navigate in app with your voice command. Explore this guide to know more for voice feature per screen.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "Experimental feature: Sometimes it may not work as expected",
                        style = MaterialTheme.typography.bodySmall,
                        color = extendedColorScheme.red.color
                    )
                }
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        "Common command:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text("1. Say \"Back\". It will move back from that screen.")
                }
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        "Notebook Screen :",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text("1. Say \"Add Notebook notebook name\". It will add new notebook that you had speaking name automatically.")
                    Text("2. Say \"Delete Notebook notebook name\". It will delete notebook that you had speaking name.")
                    Text("3. Say \"Open Notebook notebook name\". It will navigate to that notebooks' detail.")
                    Text("4. If you want to open notebook form to add notebook. you can say \"go to notebook form\". It will navigate to notebook form.")

                }
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        "Notebook Form Screen :",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text("1. Say \"name notebook's name\". It will add that name to the notebook name text field.")
                    Text("2. Say \"insert\". It will add notebook to the notebook's list.")
                }
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        "Notebook details Screen :",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text("1. Say \"Add Entity\" or \"Create Entity\" . It will navigate you to to entity adding form.")
                    Text("2. Say \"Delete Entity entity name\". It will delete that entity.")
                    Text("3. Say \"Open Entity entity name\". It will navigate to that entity's detail form.")
                }
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        "Entity Form Screen :",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text("1. Say \"name entity's name\". It will add that name to the entity name text field.")
                    Text("2. Say \"phone entity's phone number\". It will add that phone number to the entity phone number text field.")
                    Text("3. Say \"insert\". It will add entity to the entity's list.")
                }
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        "Entity details Screen :",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text("1. Say \"Add Transaction\" or \"Create Transaction\". It will navigate you to transaction adding form.")
                }
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        "Transaction Form Screen:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text("1. Say \"amount 1000\". It will add 1000 in amount in amount text box.")
                    Text("2. Say \"description school fees\". It will add \"school fees\" in description text box.")
                    Text("3. Add type by your hand currently not working that as voice command will work on that.")
                    Text("4. Say \"create\" or \"insert\". It will insert that transection in list.")

                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun VoiceFeatureGuideUiPreview() {
    VoiceFeatureGuideUi()
}