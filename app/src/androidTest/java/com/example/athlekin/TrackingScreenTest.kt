package com.example.athlekin

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.athlekin.model.Exercise
import com.example.athlekin.ui.components.ExerciseList
import org.junit.Rule
import org.junit.Test

class TrackingScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun listCurrentExercises_verifyContent() {

        val exercises = listOf(
            Exercise("Test1", 1, 2),
            Exercise("Test2", 3, 4)
        )

        composeTestRule.setContent {
            ExerciseList(exercises)
        }

        exercises.forEach { ex ->
            composeTestRule.onNodeWithText(ex.name).assertIsDisplayed()
        }
    }
}