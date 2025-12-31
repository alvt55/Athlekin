package com.example.athlekin

import SignInScreen
import android.R.attr.name
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.athlekin.ui.createAccount.CreateAccountScreen
import com.example.athlekin.ui.createAccount.CreateAccountViewModel
import com.example.athlekin.ui.login.SignInViewModel
import com.example.athlekin.ui.tracker.TrackingViewModel
import com.example.athlekin.ui.tracker.EndWorkoutScreen
import com.example.athlekin.ui.tracker.TrackingScreen
import com.example.athlekin.ui.workouts.WorkoutsScreen
import com.example.athlekin.ui.utils.AthelkinContentType



@Composable
fun AthlekinApp(
    windowSize: WindowWidthSizeClass,
    trackingViewModel: TrackingViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {

    val contentType: AthelkinContentType
    if (windowSize == WindowWidthSizeClass.Medium || windowSize == WindowWidthSizeClass.Expanded) {
        contentType = AthelkinContentType.TRACKER_WITH_WORKOUTS
    } else {
        contentType = AthelkinContentType.TRACKER_DEFAULT
    }


    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AthelkinScreen.valueOf(
        backStackEntry?.destination?.route ?: AthelkinScreen.Tracking.name
    )



    Scaffold(
        topBar = {
            AthelkinAppBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                currentScreen = currentScreen,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = AthelkinScreen.SignIn.name,
            modifier = Modifier.Companion.padding(innerPadding)
        ) {
            composable(route = AthelkinScreen.SignIn.name) {
                val viewModel = hiltViewModel<SignInViewModel>()

                SignInScreen(openTrackerScreen = {navController.navigate(AthelkinScreen.Tracking.name)},
                            onToCreateAccount = {navController.navigate(AthelkinScreen.CreateAccount.name)},
                    viewModel = viewModel)
            }
            composable(route = AthelkinScreen.CreateAccount.name) {
                val viewModel = hiltViewModel<CreateAccountViewModel>()

                CreateAccountScreen(openTrackerScreen = {navController.navigate(AthelkinScreen.Tracking.name)},
                        onToSignIn= {navController.navigate(AthelkinScreen.SignIn.name)},
                                viewModel = viewModel)
            }
            composable(route = AthelkinScreen.Tracking.name) {
                TrackingScreen(
                    onToWorkoutsClicked = { navController.navigate(AthelkinScreen.Workouts.name) },
                    onToEndWorkout = { navController.navigate(AthelkinScreen.EndWorkout.name) },
                    onToSignIn = { navController.navigate(AthelkinScreen.SignIn.name) },
                    modifier = Modifier,
                    viewModel = trackingViewModel,
                    contentType = contentType
                )
            }
            composable(route = AthelkinScreen.EndWorkout.name) {
                EndWorkoutScreen(
                    onToWorkoutsClicked = { navController.navigate(AthelkinScreen.Workouts.name) },
                    onToTrackingClicked = { navController.navigate(AthelkinScreen.Tracking.name) },
                    viewModel = trackingViewModel,
                    modifier = Modifier
                )
            }
            composable(route = AthelkinScreen.Workouts.name) {
                val context = LocalContext.current
                // Unfinished screen
                WorkoutsScreen(
                    onToTrackingClicked = { navController.navigate(AthelkinScreen.Tracking.name) },
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
