package com.example.athlekin.ui.test

import com.example.athlekin.models.Exercise
import com.example.athlekin.ui.WorkoutViewModel
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse

class WorkoutViewModelTest {

    private val viewModel = WorkoutViewModel()

    @Test
    fun workoutViewModel_ValidExerciseAdded_ExerciseAddedAndInputsReset() {
        var currentWorkoutUiState = viewModel.uiState.value

        // setup fields with the test inputs
        val testName = "Test name"
        val testReps = 5
        val testSets = 3
        viewModel.updateExerciseName(testName)
        viewModel.updateReps(testReps)
        viewModel.updateSets(testSets)

        // call the addExercise to test
        viewModel.addExercise()

        // updated Ui State
        currentWorkoutUiState = viewModel.uiState.value

        val exerciseAdded = Exercise(testName, testReps, testSets)


        // assertions
        assertEquals(currentWorkoutUiState.exercises, listOf(exerciseAdded))
        assertEquals(viewModel.inputExerciseName, "")
        assertEquals(viewModel.inputReps, 0)
        assertEquals(viewModel.inputSets, 0)

    }

    @Test
    fun workoutViewModel_ValidExerciseAddedMultiple_ExerciseAddedAndInputsReset() {
        var currentWorkoutUiState = viewModel.uiState.value

        // setup fields with the test inputs
        val testName = "Test name"
        val testReps = 5
        val testSets = 3
        viewModel.updateExerciseName(testName)
        viewModel.updateReps(testReps)
        viewModel.updateSets(testSets)

        // call the addExercise to test
        viewModel.addExercise()

        // update exercise slightly and add
        val testName2 = "Test name2"
        viewModel.updateExerciseName(testName2)
        viewModel.updateReps(testReps)
        viewModel.updateSets(testSets)
        viewModel.addExercise()

        // updated Ui State
        currentWorkoutUiState = viewModel.uiState.value

        val exerciseAdded = Exercise(testName, testReps, testSets)
        val exerciseAdded2 = Exercise(testName2, testReps, testSets)


        // assertions
        assertEquals(currentWorkoutUiState.exercises, listOf(exerciseAdded, exerciseAdded2))
        assertEquals(viewModel.inputExerciseName, "")
        assertEquals(viewModel.inputReps, 0)
        assertEquals(viewModel.inputSets, 0)

    }

    @Test
    fun workoutViewModel_MissingInputsAddExercise_ExerciseNotAddedInputsStay() {
        var currentWorkoutUiState = viewModel.uiState.value

        // setup fields with the test inputs
        val testName = "Test name"
        val testReps = 0
        val testSets = 0
        viewModel.updateExerciseName(testName)
        viewModel.updateReps(testReps)
        viewModel.updateSets(testSets)

        // call the addExercise to test
        viewModel.addExercise()

        // updated Ui State
        currentWorkoutUiState = viewModel.uiState.value


        // assertions
        assertEquals(currentWorkoutUiState.exercises, emptyList<Exercise>())
        assertEquals(viewModel.inputExerciseName, testName)
        assertEquals(viewModel.inputReps, testReps)
        assertEquals(viewModel.inputSets, testSets)

    }
}