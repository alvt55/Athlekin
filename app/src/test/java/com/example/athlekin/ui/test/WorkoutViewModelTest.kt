package com.example.athlekin.ui.test

import com.example.athlekin.models.Exercise
import com.example.athlekin.data.WorkoutUiState
import com.example.athlekin.ui.WorkoutViewModel
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Before

class WorkoutViewModelTest {

    private lateinit var viewModel: WorkoutViewModel
    private lateinit var currentWorkoutUiState: WorkoutUiState

    // Test data as class properties
    private val testName = "Test name"
    private val testName2 = "Test name2"
    private val testReps = 5
    private val testSets = 3
    private lateinit var validExercise: Exercise
    private lateinit var validExercise2: Exercise
    private lateinit var validListExercise1and2: List<Exercise>

    @Before
    fun setup() {
        viewModel = WorkoutViewModel()
        currentWorkoutUiState = viewModel.uiState.value

        // Initialize test data
        validExercise = Exercise(testName, testReps, testSets)
        validExercise2 = Exercise(testName2, testReps, testSets)
        validListExercise1and2 = listOf(validExercise, validExercise2)
    }


    @Test
    fun workoutViewModel_ValidExerciseAdded_ExerciseAddedAndInputsReset() {

        // setup fields with the test inputs
        viewModel.updateExerciseName(testName)
        viewModel.updateReps(testReps)
        viewModel.updateSets(testSets)

        // call the addExercise to test
        viewModel.addExercise()

        // assertions
        currentWorkoutUiState = viewModel.uiState.value
        assertEquals(currentWorkoutUiState.exercises, listOf(validExercise))
        assertEquals(viewModel.inputExerciseName, "")
        assertEquals(viewModel.inputReps, 0)
        assertEquals(viewModel.inputSets, 0)

    }

    @Test
    fun workoutViewModel_ValidExerciseAddedMultiple_ExerciseAddedAndInputsReset() {


        // setup fields with the test inputs
        viewModel.updateExerciseName(testName)
        viewModel.updateReps(testReps)
        viewModel.updateSets(testSets)

        // call the addExercise to test
        viewModel.addExercise()

        // update exercise slightly and add
        viewModel.updateExerciseName(testName2)
        viewModel.updateReps(testReps)
        viewModel.updateSets(testSets)
        viewModel.addExercise()


        // assertions
        currentWorkoutUiState = viewModel.uiState.value
        assertEquals(currentWorkoutUiState.exercises, validListExercise1and2)
        assertEquals(viewModel.inputExerciseName, "")
        assertEquals(viewModel.inputReps, 0)
        assertEquals(viewModel.inputSets, 0)

    }

    @Test
    fun workoutViewModel_MissingNameAddExercise_ExerciseNotAddedInputsStay() {


        // setup fields with no reps or sets
        viewModel.updateExerciseName("")
        viewModel.updateReps(testReps)
        viewModel.updateSets(testSets)

        // call the addExercise to test
        viewModel.addExercise()

        // assertions
        currentWorkoutUiState = viewModel.uiState.value
        assertEquals(currentWorkoutUiState.exercises, emptyList<Exercise>())
        assertEquals(viewModel.inputExerciseName, "")
        assertEquals(viewModel.inputReps, testReps)
        assertEquals(viewModel.inputSets, testSets)

    }

    @Test
    fun workoutViewModel_MissingRepsAddExercise_ExerciseNotAddedInputsStay() {


        // setup fields with no reps or sets
        viewModel.updateExerciseName(testName)
        viewModel.updateReps(0)
        viewModel.updateSets(testSets)

        // call the addExercise to test
        viewModel.addExercise()

        // assertions
        currentWorkoutUiState = viewModel.uiState.value
        assertEquals(currentWorkoutUiState.exercises, emptyList<Exercise>())
        assertEquals(viewModel.inputExerciseName, testName)
        assertEquals(viewModel.inputReps, 0)
        assertEquals(viewModel.inputSets, testSets)

    }

    @Test
    fun workoutViewModel_MissingSetsAddExercise_ExerciseNotAddedInputsStay() {


        // setup fields with no reps or sets
        viewModel.updateExerciseName(testName)
        viewModel.updateReps(testReps)
        viewModel.updateSets(0)

        // call the addExercise to test
        viewModel.addExercise()

        // assertions
        currentWorkoutUiState = viewModel.uiState.value
        assertEquals(currentWorkoutUiState.exercises, emptyList<Exercise>())
        assertEquals(viewModel.inputExerciseName, testName)
        assertEquals(viewModel.inputReps, testReps)
        assertEquals(viewModel.inputSets, 0)

    }



    @Test
    fun workoutViewModel_UiStateSetter_ShouldSet() {

        // call setter function
        viewModel.setUiStateForTest(WorkoutUiState(testName, validListExercise1and2))

        // sanity check that the setting uistate works for testing
        currentWorkoutUiState = viewModel.uiState.value
        assertEquals(currentWorkoutUiState.name, testName)
        assertEquals(currentWorkoutUiState.exercises, validListExercise1and2)

    }

    @Test
    fun workoutViewModel_ValidEndWorkout_ResetState() {

        // setup viewModel to have a "submittable" state
        viewModel.setUiStateForTest(WorkoutUiState(testName, validListExercise1and2))
        viewModel.endWorkout()

        // assertions
        currentWorkoutUiState = viewModel.uiState.value
        assertEquals(currentWorkoutUiState.name, "")
        assertEquals(currentWorkoutUiState.exercises, emptyList<Exercise>())
        assertEquals(viewModel.inputWorkoutName, "")

    }

    @Test
    fun workoutViewModel_NoNameEndWorkout_DontResetState() {

        // setup viewModel to have a invalid submit - no exercises
        viewModel.setUiStateForTest(WorkoutUiState("", validListExercise1and2))
        viewModel.endWorkout()

        currentWorkoutUiState = viewModel.uiState.value
        assertEquals(currentWorkoutUiState.name, "")
        assertEquals(currentWorkoutUiState.exercises, validListExercise1and2)


    }

    @Test
    fun workoutViewModel_NoExercisesEndWorkout_DontResetState() {

        // setup viewModel to have a invalid submit - no exercises
        viewModel.setUiStateForTest(WorkoutUiState(testName, emptyList<Exercise>()))
        viewModel.updateWorkoutName(testName) // update input variable too
        viewModel.endWorkout()

        currentWorkoutUiState = viewModel.uiState.value
        assertEquals(currentWorkoutUiState.name, testName)
        assertEquals(viewModel.inputWorkoutName, testName) // test ui not reset
        assertEquals(currentWorkoutUiState.exercises, emptyList<Exercise>())


    }


}