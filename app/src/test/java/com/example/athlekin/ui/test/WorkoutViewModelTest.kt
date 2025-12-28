package com.example.athlekin.ui.test

import com.example.athlekin.data.Exercise
import com.example.athlekin.ui.TrackingViewModel
import com.example.athlekin.ui.TrackerUiState
import com.example.athlekin.ui.CurrExerciseState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Before

class TrackingViewModelTest {

    private lateinit var viewModel: TrackingViewModel
    private lateinit var currentTrackerUiState: TrackerUiState

    // Test data
    private val testName = "Test name"
    private val testName2 = "Test name2"
    private val testReps = 5
    private val testSets = 3
    private lateinit var validExercise: Exercise
    private lateinit var validExercise2: Exercise
    private lateinit var validListExercise1and2: List<Exercise>

    @Before
    fun setup() {
        viewModel = TrackingViewModel()
        currentTrackerUiState = viewModel.uiState.value

        validExercise = Exercise(testName, testReps, testSets)
        validExercise2 = Exercise(testName2, testReps, testSets)
        validListExercise1and2 = listOf(validExercise, validExercise2)
    }


    fun trackingViewModel_UpdateCurrentExerciseState_ValidInput_SetsStateValid() {
        val newExercise = CurrExerciseState("Pushups", 10, 3)
        viewModel.updateCurrentExerciseState(newExercise)

        assertEquals("Pushups", viewModel.currExerciseState.name)
        assertEquals(10, viewModel.currExerciseState.reps)
        assertEquals(3, viewModel.currExerciseState.sets)
        assertEquals(true, viewModel.currExerciseState.isEntryValid)
    }

    @Test
    fun trackingViewModel_UpdateCurrentExerciseState_EmptyName_SetsStateInvalid() {
        val newExercise = CurrExerciseState("", 10, 3)
        viewModel.updateCurrentExerciseState(newExercise)

        assertEquals("", viewModel.currExerciseState.name)
        assertEquals(10, viewModel.currExerciseState.reps)
        assertEquals(3, viewModel.currExerciseState.sets)
        assertEquals(false, viewModel.currExerciseState.isEntryValid)
    }

    @Test
    fun trackingViewModel_ValidExerciseAdded_ExerciseAddedAndInputsReset() {
        viewModel.updateCurrentExerciseState(CurrExerciseState(testName, testReps, testSets))
        viewModel.addExercise()

        currentTrackerUiState = viewModel.uiState.value
        assertEquals(currentTrackerUiState.exercises, listOf(validExercise))
        assertEquals(viewModel.currExerciseState, CurrExerciseState())
    }

    @Test
    fun trackingViewModel_ValidExerciseAddedMultiple_ExerciseAddedAndInputsReset() {
        // Add first exercise
        viewModel.updateCurrentExerciseState(CurrExerciseState(testName, testReps, testSets))
        viewModel.addExercise()

        // Add second exercise
        viewModel.updateCurrentExerciseState(CurrExerciseState(testName2, testReps, testSets))
        viewModel.addExercise()

        currentTrackerUiState = viewModel.uiState.value
        assertEquals(currentTrackerUiState.exercises, validListExercise1and2)
        assertEquals(viewModel.currExerciseState, CurrExerciseState())
    }

    @Test
    fun trackingViewModel_MissingName_ExerciseNotAddedInputsStay() {
        viewModel.updateCurrentExerciseState(CurrExerciseState("", testReps, testSets))
        viewModel.addExercise()

        currentTrackerUiState = viewModel.uiState.value
        assertEquals(currentTrackerUiState.exercises, emptyList<Exercise>())
        assertEquals(viewModel.currExerciseState, CurrExerciseState("", testReps, testSets, false))
    }

    @Test
    fun trackingViewModel_MissingReps_ExerciseNotAddedInputsStay() {
        viewModel.updateCurrentExerciseState(CurrExerciseState(testName, 0, testSets))
        viewModel.addExercise()

        currentTrackerUiState = viewModel.uiState.value
        assertEquals(currentTrackerUiState.exercises, emptyList<Exercise>())
        assertEquals(viewModel.currExerciseState, CurrExerciseState(testName, 0, testSets, false))
    }

    @Test
    fun trackingViewModel_MissingSets_ExerciseNotAddedInputsStay() {
        viewModel.updateCurrentExerciseState(CurrExerciseState(testName, testReps, 0))
        viewModel.addExercise()

        currentTrackerUiState = viewModel.uiState.value
        assertEquals(currentTrackerUiState.exercises, emptyList<Exercise>())
        assertEquals(viewModel.currExerciseState, CurrExerciseState(testName, testReps, 0, false))
    }

    @Test
    fun trackingViewModel_WorkoutNameUpdate_ReflectedInState() {
        viewModel.updateWorkoutName("Workout 1")

        currentTrackerUiState = viewModel.uiState.value
        assertEquals(currentTrackerUiState.workoutName, "Workout 1")
    }

    @Test
    fun trackingViewModel_ValidEndWorkout_ResetState() {
        // Setup workout using public API
        viewModel.updateWorkoutName("Workout 1")
        viewModel.updateCurrentExerciseState(CurrExerciseState(testName, testReps, testSets))
        viewModel.addExercise()
        viewModel.updateCurrentExerciseState(CurrExerciseState(testName2, testReps, testSets))
        viewModel.addExercise()

        // End workout
        viewModel.endWorkout()

        currentTrackerUiState = viewModel.uiState.value
        assertEquals(currentTrackerUiState.workoutName, "")
        assertEquals(currentTrackerUiState.exercises, emptyList<Exercise>())
        assertEquals(viewModel.currExerciseState, CurrExerciseState())
    }

    @Test
    fun trackingViewModel_EndWorkoutWithoutNameOrExercises_DoesNotReset() {
        // No workout name, no exercises
        viewModel.endWorkout()
        currentTrackerUiState = viewModel.uiState.value
        assertEquals(currentTrackerUiState.workoutName, "")
        assertEquals(currentTrackerUiState.exercises, emptyList<Exercise>())

        // Only workout name, no exercises
        viewModel.updateWorkoutName("Workout 1")
        viewModel.endWorkout()
        currentTrackerUiState = viewModel.uiState.value
        assertEquals(currentTrackerUiState.workoutName, "Workout 1")
        assertEquals(currentTrackerUiState.exercises, emptyList<Exercise>())

        // Only exercises, no workout name
        viewModel.updateCurrentExerciseState(CurrExerciseState(testName, testReps, testSets))
        viewModel.addExercise()
        viewModel.updateWorkoutName("") // reset name
        viewModel.endWorkout()
        currentTrackerUiState = viewModel.uiState.value
        assertEquals(currentTrackerUiState.workoutName, "")
        assertEquals(currentTrackerUiState.exercises.size, 1)
    }

    @Test
    fun trackingViewModel_ValidateInput_ValidExercise_ReturnsTrue() {
        val validExercise = CurrExerciseState("Pushups", 10, 3)
        val isValid = viewModel.validateInput(validExercise)
        assertEquals(true, isValid)
    }

    @Test
    fun trackingViewModel_ValidateInput_EmptyName_ReturnsFalse() {
        val invalidExercise = CurrExerciseState("", 10, 3)
        val isValid = viewModel.validateInput(invalidExercise)
        assertEquals(false, isValid)
    }

    @Test
    fun trackingViewModel_ValidateInput_ZeroReps_ReturnsFalse() {
        val invalidExercise = CurrExerciseState("Squats", 0, 3)
        val isValid = viewModel.validateInput(invalidExercise)
        assertEquals(false, isValid)
    }

    @Test
    fun trackingViewModel_ValidateInput_ZeroSets_ReturnsFalse() {
        val invalidExercise = CurrExerciseState("Squats", 10, 0)
        val isValid = viewModel.validateInput(invalidExercise)
        assertEquals(false, isValid)
    }

}
