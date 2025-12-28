package com.example.athlekin


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold


import androidx.compose.runtime.Composable

import androidx.compose.ui.tooling.preview.Preview



import com.example.athlekin.ui.theme.AthlekinTheme
import androidx.compose.ui.Modifier



import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.example.athlekin.ui.TrackingViewModel
import com.example.athlekin.ui.screens.EndWorkoutScreen
import com.example.athlekin.ui.screens.TrackingScreen
import com.example.athlekin.ui.screens.WorkoutsScreen
import com.example.athlekin.ui.utils.AthelkinContentType


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AthlekinTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val windowSize = calculateWindowSizeClass(this)
                    AthlekinApp(
                        windowSize = windowSize.widthSizeClass
                    )
                }

            }
        }
    }
}
@Preview(showBackground = true)
@Composable
// preview of main page
fun CompactScreenPreview() {
    AthlekinTheme() {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            AthlekinApp(windowSize = WindowWidthSizeClass.Compact)
        }

    }
}

@Preview(showBackground = true, widthDp = 700)
@Composable
// preview of main page
fun MediumScreenPreview() {
    AthlekinTheme() {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            AthlekinApp(windowSize = WindowWidthSizeClass.Medium)
        }

    }
}

@Preview(showBackground = true, widthDp = 1000)
@Composable
// preview of main page
fun ExpandedScreenPreview() {
    AthlekinTheme() {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            AthlekinApp(windowSize = WindowWidthSizeClass.Expanded)
        }

    }
}



enum class AthelkinScreen(@StringRes val title: Int) {
    Tracking(title = R.string.tracking_page),
    EndWorkout(title = R.string.end_page),
    Workouts(title = R.string.workouts_page),

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AthelkinAppBar(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    currentScreen: AthelkinScreen
) {

    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,

        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}


@Composable
fun AthlekinApp(
    windowSize: WindowWidthSizeClass,
    viewModel: TrackingViewModel = viewModel(),
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
        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = AthelkinScreen.Tracking.name,
            modifier = Modifier.Companion.padding(innerPadding)
        ) {
            composable(route = AthelkinScreen.Tracking.name) {
                TrackingScreen(
                    onToWorkoutsClicked = { navController.navigate(AthelkinScreen.Workouts.name) },
                    onToEndWorkout = { navController.navigate(AthelkinScreen.EndWorkout.name) },
                    modifier = Modifier,
                    viewModel = viewModel,
                    contentType = contentType
                )
            }
            composable(route = AthelkinScreen.EndWorkout.name) {
                EndWorkoutScreen(
                    onToWorkoutsClicked = { navController.navigate(AthelkinScreen.Workouts.name) },
                    onToTrackingClicked = { navController.navigate(AthelkinScreen.Tracking.name) },
                    viewModel = viewModel,
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