package com.example.athlekin.ui.test

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.athlekin.data.AuthRepository
import com.example.athlekin.data.WorkoutsRepo
import com.example.athlekin.model.Exercise
import com.example.athlekin.model.WorkoutDoc
import com.example.athlekin.room.ExerciseEntity
import com.example.athlekin.room.OfflineExercisesRepo
import com.example.athlekin.ui.tracker.CurrExerciseState
import com.example.athlekin.ui.tracker.TrackingViewModel
import io.mockk.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test


@ExperimentalCoroutinesApi
class TrackingViewModelTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var workoutsRepo: WorkoutsRepo
    private lateinit var offlineExercisesRepo: OfflineExercisesRepo
    private lateinit var viewModel: TrackingViewModel

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var editWorkout : WorkoutDoc
    private val exerciseName1 = "Push Up"
    private val exerciseName2 = "Squat"
    private val rep1 = 10
    private val rep2 = 15
    private val set1 = 3
    private val set2 = 4
    private val weight1 = 30
    private val weight2 = 0
    private val comment1 = "Test comment"
    private val comment2 = ""
    val invalidWorkoutId = "987"

    val validExerciseState = CurrExerciseState(
        name = "test",
        reps = 5,
        sets = 3,
        weight = 50
    )

    val invalidExerciseState = CurrExerciseState(
        name = "",
        reps = 1,
        sets = 1,
        weight = 0
    )


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        authRepository = mockk(relaxed = true)
        workoutsRepo = mockk(relaxed = true)
        offlineExercisesRepo = mockk()

        editWorkout = WorkoutDoc(
            id = "123",
            ownerId = "456",
            name = "Test Workout",
            exercises = listOf(
                Exercise(1, exerciseName1, rep1, set1, weight1, comment1),
                Exercise(2, exerciseName2, rep2, set2, weight2, comment2)
            )
        )


        every { offlineExercisesRepo.getAllExercisesStream() } returns flowOf(
            listOf(
                ExerciseEntity(1, exerciseName1, rep1, set1, weight1, comment1),
                ExerciseEntity(2, exerciseName2, rep2, set2, weight2, comment2)
            )
        )

        coEvery { workoutsRepo.getWorkout(editWorkout.id) } returns editWorkout
        coEvery { workoutsRepo.getWorkout(invalidWorkoutId) } returns null

        viewModel = TrackingViewModel(authRepository, workoutsRepo, offlineExercisesRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun initEditMode_ValidWorkoutId_LoadsWorkoutAndExercises() = runTest {



        viewModel.initEditMode(editWorkout.id)
        advanceUntilIdle()

        assertEquals(editWorkout.id, viewModel.workoutId)
        assertEquals(editWorkout.name, viewModel.trackerUiState.workoutName)
        assertEquals(CurrExerciseState(), viewModel.trackerUiState.currExerciseState)
        assertEquals(null, viewModel.trackerUiState.errorMessage)

        coVerify{ workoutsRepo.getWorkout(match { it == editWorkout.id }) }
        coVerify { offlineExercisesRepo.deleteAllExercises()}
        coVerify { offlineExercisesRepo.createExercise(match { it.name == exerciseName1 }) }
        coVerify { offlineExercisesRepo.createExercise(match { it.name == exerciseName2 }) }

    }

    @Test
    fun initEditMode_InvalidWorkoutId_SetsErrorMessage() = runTest {

        viewModel.initEditMode(invalidWorkoutId)
        advanceUntilIdle()

        assertEquals("", viewModel.workoutId)
        assertEquals("", viewModel.trackerUiState.workoutName)
        assertEquals(CurrExerciseState(), viewModel.trackerUiState.currExerciseState)
        assertEquals("Invalid Workout to Edit", viewModel.trackerUiState.errorMessage)

        coVerify{ workoutsRepo.getWorkout(match { it == invalidWorkoutId }) }

    }

    @Test
    fun updateCurrentExerciseState_ValidState_UpdatesState() {
        assertEquals(CurrExerciseState(), viewModel.trackerUiState.currExerciseState)

        viewModel.updateCurrentExerciseState(validExerciseState)

        assertEquals(validExerciseState, viewModel.trackerUiState.currExerciseState)
    }

    @Test
    fun validateCurrentExerciseState_ValidState_ReturnsTrue() {

        val result = viewModel.validateCurrentExerciseState(validExerciseState)
        assertTrue(result)
    }

    @Test
    fun validateCurrentExerciseState_InvalidState_ReturnsFalse() {


        val result = viewModel.validateCurrentExerciseState(invalidExerciseState)

        assertFalse(result)
    }

    @Test
    fun updateWorkoutName_ValidName_UpdatesState() {
        assertEquals("", viewModel.trackerUiState.workoutName)

        val name = "Test Workout"

        viewModel.updateWorkoutName(name)

        assertEquals(name, viewModel.trackerUiState.workoutName)
    }

    @Test
    fun addExercise_ValidExerciseState_AddsExerciseToRoom() = runTest {

        viewModel.updateCurrentExerciseState(validExerciseState)

        viewModel.addExercise()
        advanceUntilIdle()

        coVerify (exactly = 1){
            offlineExercisesRepo.createExercise(match {
                it.name == validExerciseState.name &&
                        it.reps == validExerciseState.reps &&
                        it.sets == validExerciseState.sets &&
                        it.weight == validExerciseState.weight &&
                        it.comments == validExerciseState.comments
            })
        }

        assertEquals(CurrExerciseState(), viewModel.trackerUiState.currExerciseState)

    }


    @Test
    fun addExercise_InvalidExerciseState_NoRoomInsertionsAndErrorMessage() = runTest {

        viewModel.updateCurrentExerciseState(invalidExerciseState)

        viewModel.addExercise()
        advanceUntilIdle()

        coVerify (exactly = 0){
            offlineExercisesRepo.createExercise(any())
        }

        assertEquals(invalidExerciseState, viewModel.trackerUiState.currExerciseState)
        assertEquals("Invalid Exercise", viewModel.trackerUiState.errorMessage)
    }


//    @Test
//    fun endWorkout_ValidNewWorkout_SavesWorkoutToFirebaseAndClearsState() = runTest {
//
//        viewModel.updateCurrentExerciseState(validExerciseState)
//        viewModel.updateWorkoutName("Test Workout")
//
//        viewModel.endWorkout()
//        advanceUntilIdle()
//
//        // TODO: authentication mock
//
//        coVerify (exactly = 1){
//            offlineExercisesRepo.deleteAllExercises()
//            workoutsRepo.createWorkout(match {
//                it.name == "Test Workout" &&
//                        it.exercises.size == 2
//            })
//
//        }
//
//
//        assertEquals("", viewModel.trackerUiState.workoutName)
//        assertEquals(CurrExerciseState(), viewModel.trackerUiState.currExerciseState)
//
//
//    }




    }





}
