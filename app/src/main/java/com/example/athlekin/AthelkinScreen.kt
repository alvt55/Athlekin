package com.example.athlekin

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.athlekin.ui.TrackingScreen
import com.example.athlekin.ui.WorkoutViewModel
import com.example.athlekin.ui.WorkoutsScreen


enum class AthelkinScreen() {
    Tracking,
    Workouts,
}




@Composable
fun AthlekinApp(
    viewModel: WorkoutViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {

    Scaffold(
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()



        NavHost(
            navController = navController,
            startDestination = AthelkinScreen.Tracking.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = AthelkinScreen.Tracking.name) {
                TrackingScreen(
                    onToWorkoutsClicked = {navController.navigate(AthelkinScreen.Workouts.name)},
                    viewModel = viewModel,
                    modifier = Modifier
                )
            }
            composable(route = AthelkinScreen.Workouts.name) {
                val context = LocalContext.current
                // Unfinished screen
                WorkoutsScreen(
                    onToTrackingClicked = {navController.navigate(AthelkinScreen.Tracking.name)},
                    onShareButton = { summary: String ->
                shareWorkouts(context, summary)
            },
                    modifier = Modifier
                )
            }

        }

    }

}

private fun shareWorkouts(context : Context, summary : String) {

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, summary)
    }

    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.workout_share)
        )
    )
}







// // for the LLM input later
//@Composable
//// exercise input field along with submit button
//fun InputSection(modifier: Modifier = Modifier) {
//
//    var textValue by remember { mutableStateOf("") }
//
//    Column(modifier
//        .fillMaxWidth()
//        .padding(15.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.spacedBy(10.dp)
//    ) {
//        TextField(
//            value = textValue,
//            onValueChange = { textValue = it },
//            label = {stringResource(R.string.exercise_input)}
//        )
//        Button(
//            onClick = {}
//
//        ) {
//            Text("Parse")
//        }
//    }
//
//}




