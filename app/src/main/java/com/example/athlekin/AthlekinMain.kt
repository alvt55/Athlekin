package com.example.athlekin

import android.R.attr.onClick
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.athlekin.models.ExercisesViewModel
import com.example.athlekin.models.Workout

import com.example.athlekin.models.WorkoutsViewModel
import com.example.athlekin.ui.theme.AthlekinTheme




@Preview
@Composable
// list of exercises for the current workout
fun ExerciseList(modifier: Modifier = Modifier, viewModel: ExercisesViewModel = viewModel(), ) {
    val exercises by viewModel.exercises.collectAsState()

    Column(modifier = modifier
        .fillMaxWidth()
        .padding(15.dp)) {

        // header
        Row(modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(text= stringResource(R.string.reps), modifier = Modifier.weight(1f), textAlign = TextAlign.Left)
            Text(text = stringResource(R.string.sets), modifier = Modifier.weight(1f))
            Text(text = stringResource(R.string.exercise), modifier = Modifier.weight(1f))
        }

        for (exercise in exercises) {
            ExerciseItem(modifier, exercise.name, exercise.reps, exercise.sets)
        }
    }

}

@Composable
// ui for an exercise item
fun ExerciseItem(modifier: Modifier = Modifier, name: String = "", reps: Int = 0, sets: Int = 0 ) {
    Row(modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly) {
        Text(text= "$reps", modifier = Modifier.weight(1f), textAlign = TextAlign.Left)
        Text(text = "$sets", modifier = Modifier.weight(1f))
        Text(name, modifier = Modifier.weight(1f))

    }

}



// add preview
@Composable
fun AthlekinApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var inputText by rememberSaveable { mutableStateOf("") }  // ADD THIS LINE

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = it.label
                        )
                    }, label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    )
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Input field with Add button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Enter text") },
                    singleLine = true
                )
                Button(
                    onClick = {
                        // Handle add action here
                        println("Added: $inputText")
                        inputText = "" // Clear after adding
                    }
                ) {
                    Text("Add")
                }
            }

            // Your existing content

        }
    }
}



enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),

    FAVORITES("Favorites", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox),
}

