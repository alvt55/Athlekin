package com.example.athlekin.ui.test

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

        coEvery { workoutsRepo.getWorkout(any()) } returns editWorkout

        viewModel = TrackingViewModel(authRepository, workoutsRepo, offlineExercisesRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun initEditMode_ValidWorkoutId_LoadsWorkoutAndExercises() {



        viewModel.initEditMode(editWorkout.id)

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
    fun validateCurrentExerciseState_ValidState_ReturnsTrue() {
        val state = CurrExerciseState(
            name = "Bench Press",
            reps = 12,
            sets = 3,
            weight = 50
        )

        val result = viewModel.validateCurrentExerciseState(state)

        assertTrue(result)
    }

    @Test
    fun validateCurrentExerciseState_InvalidState_ReturnsFalse() {
        val state = CurrExerciseState(
            name = "",
            reps = 0,
            sets = 3,
            weight = 50
        )

        val result = viewModel.validateCurrentExerciseState(state)

        assertFalse(result)
    }



}
