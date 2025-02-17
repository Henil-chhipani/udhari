package com.example.udhari.ui.guide

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.example.udhari.navigation.GlobalNavController
import com.example.udhari.ui.commonCoponents.BackBtn
import com.example.udhari.ui.commonCoponents.TopBar

@Composable
fun GuideScreen() {
    GuideScreenUi()
}

@Composable
fun GuideScreenUi() {
    val globalNavController = GlobalNavController.current
    Scaffold(
        topBar = {
            TopBar(
                icon = {
                    BackBtn()
                },
                onIconClick = {
                    globalNavController.popBackStack()
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            LazyColumn(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                item {
                    Text(
                        "Guide",
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Text(
                        "This app is designed to help you manage your expenses.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        "The concept of app is just like notebook you are using manage your finance but with extra features",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    HorizontalDivider()
                    Text("Concept of app", modifier = Modifier.padding(top = 8.dp), style = MaterialTheme.typography.titleLarge)
                    Text("Very simple and user friendly")
                    Text("NoteBook:",  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(
                        "Notebook is a place where you can create your own notebooks and add entities in it. you can create different notebook for different purpose like personal, business",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text("Entity:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(
                        "Entity is a person or a thing you owe money to. you can add entity in the notebook and add your owe and collect transactions under entity",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text("Transaction:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(
                        "Transaction is the amount you owe to the entity. you can add transaction in the entity and add your owe and collect transactions under entity",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    HorizontalDivider()
                    Text("Flow of app", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Create notebook -> Add entities in the Notebook -> Add your owe and collect transactions under entity",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        "It will automatically calculate your debt and owing of debt and display ui accordingly",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun GuideScreenPreview() {
    AppTheme {
        GuideScreenUi()
    }
}