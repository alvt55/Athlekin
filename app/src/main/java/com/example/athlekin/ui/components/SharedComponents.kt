package com.example.athlekin.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.athlekin.R
import com.example.athlekin.data.Exercise


@Composable
// exercise input field
fun TextInput(
    @StringRes label: Int,
    value : String,
    onValueChange : (String) -> Unit,
    modifier: Modifier = Modifier
) {

    TextField(
        value,
        onValueChange,
        label = { Text(stringResource(label)) }
    )

}

@Composable
fun NumberStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    min: Int = 1,
    max: Int = Int.MAX_VALUE,
    label: String = ""
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(label)
        IconButton(
            onClick = {
                if (value > min) onValueChange(value - 1)
            },
            enabled = value > min
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrement")
        }

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        IconButton(
            onClick = {
                if (value < max) onValueChange(value + 1)
            },
            enabled = value < max
        ) {
            Icon(Icons.Default.Add, contentDescription = "Increment")
        }
    }
}




@Composable
// list of exercises for the current workout
fun ExerciseList(exerciseList : List<Exercise>, modifier: Modifier = Modifier) {

    Column(modifier = modifier.fillMaxHeight(0.3f)) {
        // header
        Row(modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly) {

            ProvideTextStyle(
                value = MaterialTheme.typography.displayMedium
            ) {
                Text(text= stringResource(R.string.reps),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Left)
                Text(text = stringResource(R.string.sets), modifier = Modifier.weight(1f))
                Text(text = stringResource(R.string.exercise), modifier = Modifier.weight(3f))
            }

        }


        LazyColumn() {
            items(exerciseList) {
                ExerciseItem(
                    exercise = it,
//                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
                    modifier = Modifier.padding(10.dp)
                )
            }

        }

    }
}


// dropdown button
@Composable
private fun ExpandButton(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ){
        Icon(
            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = stringResource(R.string.expand_button_content_description),
            tint = MaterialTheme.colorScheme.secondary
        )
    }

}

@Composable
// ui for an exercise item
fun ExerciseItem(exercise : Exercise, modifier: Modifier = Modifier) {

    var expanded by remember {mutableStateOf(false)}

    Column() {
        Card(modifier = modifier) {
            Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(text= exercise.reps.toString(), modifier = Modifier.weight(1f), textAlign = TextAlign.Left)
                Text(text = exercise.sets.toString(), modifier = Modifier.weight(1f))
                Text(text=exercise.name, modifier = Modifier.weight(3f))
                ExpandButton(expanded = expanded, onClick = {expanded = !expanded})

            }

            // expanded section
            if (expanded) {
                Column(
                    modifier = modifier
                ) {
                    Text(
                        text = "Total volume: ${exercise.reps * exercise.sets}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

        }
    }



}
