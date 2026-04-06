package com.example.athlekin


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.athlekin.ui.theme.AthlekinTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Provides
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099)
        FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080)

        enableEdgeToEdge()
        setContent {
            AthlekinTheme {
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
    AthlekinTheme {
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
    AthlekinTheme {
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
    AthlekinTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            AthlekinApp(windowSize = WindowWidthSizeClass.Expanded)
        }

    }
}


enum class AthelkinScreen(@StringRes val title: Int) {
    Tracking(title = R.string.tracking_page),
    Workouts(title = R.string.workouts_page),
    CreateAccount(title = R.string.create_account),
    SignIn(title = R.string.signin),
    Calendar(title = R.string.calendar)

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

