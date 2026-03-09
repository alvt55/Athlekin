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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
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

    val testUserId = "testUserId"


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        authRepository = mockk(relaxed = true)
        workoutsRepo = mockk(relaxed = true)
        offlineExercisesRepo = mockk(relaxed = true)

        editWorkout = WorkoutDoc(
            id = "123",
            ownerId = "456",
            name = "Test Workout",
            exercises = listOf(
                Exercise(1, exerciseName1, rep1, set1, weight1, comment1),
                Exercise(2, exerciseName2, rep2, set2, weight2, comment2)
            )
        )


//        coEvery { workoutsRepo.createWorkout(any())} returns "123"
        coEvery { workoutsRepo.updateWorkout(any())} just runs



        coEvery { workoutsRepo.getWorkout(editWorkout.id) } returns editWorkout
        coEvery { workoutsRepo.getWorkout(invalidWorkoutId) } returns null

        viewModel = TrackingViewModel(authRepository, workoutsRepo, offlineExercisesRepo)

        coEvery {offlineExercisesRepo.deleteAllExercises()} just runs
        coEvery { offlineExercisesRepo.createExercise(any()) } just Runs
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

        assertEquals(editWorkout.id, viewModel.workoutEditId)
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

        assertEquals("", viewModel.workoutEditId)
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


    @Test
    fun endWorkout_ValidNewWorkout_SavesWorkoutToFirebaseAndClearsState() = runTest {

        coEvery{offlineExercisesRepo.getAllExercisesStream()} returns flowOf(listOf(
            ExerciseEntity(
                name = validExerciseState.name,
                reps = validExerciseState.reps,
                sets = validExerciseState.sets,
                weight = validExerciseState.weight,
            )))


        coEvery { authRepository.currentUserIdFlow } returns flowOf(testUserId)

        viewModel = TrackingViewModel(authRepository, workoutsRepo, offlineExercisesRepo)


        viewModel.updateWorkoutName("Test Workout")


        viewModel.endWorkout()
        advanceUntilIdle()


        coVerify (exactly = 1){
            workoutsRepo.createWorkout(match {
                it.name == "Test Workout" &&
                        it.ownerId == testUserId
                        it.exercises.size == 1 &&
                        it.exercises[0].name == validExerciseState.name
            })
            offlineExercisesRepo.deleteAllExercises()


        }


        assertEquals("", viewModel.trackerUiState.workoutName)
        assertEquals(CurrExerciseState(), viewModel.trackerUiState.currExerciseState)
        assertEquals(null, viewModel.trackerUiState.errorMessage)

    }

    @Test
    fun endWorkout_InValidNewWorkout_ErrorMessageAndNoWorkoutSaved() = runTest {

        coEvery{offlineExercisesRepo.getAllExercisesStream()} returns flowOf(listOf(
            ExerciseEntity(
                name = validExerciseState.name,
                reps = validExerciseState.reps,
                sets = validExerciseState.sets,
                weight = validExerciseState.weight,
            )))

        coEvery { authRepository.currentUserIdFlow } returns flowOf(testUserId)
        viewModel = TrackingViewModel(authRepository, workoutsRepo, offlineExercisesRepo)


        viewModel.updateWorkoutName("")
        viewModel.endWorkout()
        advanceUntilIdle()


        coVerify(exactly = 0) { workoutsRepo.createWorkout(any()) }
        coVerify(exactly = 0) { offlineExercisesRepo.deleteAllExercises() }


        assertEquals("", viewModel.trackerUiState.workoutName)
        assertEquals(listOf(Exercise(roomId = 0, name = validExerciseState.name, reps = validExerciseState.reps, sets = validExerciseState.sets, weight = validExerciseState.weight, comments = "")),viewModel.exercises.value)
        assertEquals("Please enter a workout name and add at least one exercise", viewModel.trackerUiState.errorMessage)


    }


    @Test
    fun endWorkout_UserNotSignedIn_ErrorMessageAndNoWorkoutSaved() = runTest {

        coEvery { authRepository.currentUserIdFlow } returns flowOf("")

        viewModel.updateWorkoutName("Test Workout")

        viewModel.endWorkout()
        advanceUntilIdle()


        coVerify(exactly = 0) { workoutsRepo.createWorkout(any()) }
        coVerify(exactly = 0) { offlineExercisesRepo.deleteAllExercises() }


        assertEquals("Test Workout", viewModel.trackerUiState.workoutName)
        assertEquals("Please login first", viewModel.trackerUiState.errorMessage)


    }


    @Test
    fun endWorkout_FirebaseErrorCreate_ErrorMessageAndNoUpdate() = runTest {

        coEvery{offlineExercisesRepo.getAllExercisesStream()} returns flowOf(listOf(
            ExerciseEntity(
                name = validExerciseState.name,
                reps = validExerciseState.reps,
                sets = validExerciseState.sets,
                weight = validExerciseState.weight,
            )))

        coEvery { authRepository.currentUserIdFlow } returns flowOf(testUserId)
        coEvery {workoutsRepo.createWorkout(any())} throws Exception("Firebase error")

        viewModel = TrackingViewModel(authRepository, workoutsRepo, offlineExercisesRepo)


        viewModel.updateWorkoutName("Test Workout")
        viewModel.endWorkout()
        advanceUntilIdle()


        coVerify (exactly = 1){
            workoutsRepo.createWorkout(match {
                it.name == "Test Workout" &&
                        it.ownerId == testUserId &&
                        it.exercises.size == 1 &&
                        it.exercises[0].name == validExerciseState.name
            })

        }

        coVerify (exactly = 0) { offlineExercisesRepo.deleteAllExercises() }


        assertEquals("Test Workout", viewModel.trackerUiState.workoutName)
        assertEquals("Error saving workout to database", viewModel.trackerUiState.errorMessage)


    }

    @Test
    fun endWorkout_ValidEditWorkout_WorkoutUpdatedAndFieldsCleared() = runTest {

        coEvery { authRepository.currentUserIdFlow } returns flowOf(testUserId)

        coEvery{offlineExercisesRepo.getAllExercisesStream()} returns flowOf(listOf(
            ExerciseEntity(
                name = validExerciseState.name,
                reps = validExerciseState.reps,
                sets = validExerciseState.sets,
                weight = validExerciseState.weight,
            )))

        viewModel = TrackingViewModel(authRepository, workoutsRepo, offlineExercisesRepo)

        viewModel.initEditMode(editWorkout.id)
        advanceUntilIdle()

        viewModel.updateWorkoutName("Test Workout")


        viewModel.endWorkout()
        advanceUntilIdle()


        coVerify (exactly = 1){
            workoutsRepo.updateWorkout(match {
                it.id == editWorkout.id &&
                it.name == "Test Workout" &&
                        it.ownerId == testUserId &&
                        it.exercises.size == 1
            })
        }

        // once from init, once from endWorkout
        coVerify (exactly = 2){
            offlineExercisesRepo.deleteAllExercises()
        }

        assertEquals("", viewModel.workoutEditId)
        assertEquals("", viewModel.trackerUiState.workoutName)
        assertEquals(CurrExerciseState(), viewModel.trackerUiState.currExerciseState)
        assertEquals(null, viewModel.trackerUiState.errorMessage)

    }


    @Test
    fun endWorkout_FirebaseErrorEdit_ErrorMessageAndNoUpdate() = runTest {

        coEvery { authRepository.currentUserIdFlow } returns flowOf(testUserId)
        coEvery {workoutsRepo.updateWorkout(any())} throws Exception("Firebase error")
        coEvery{offlineExercisesRepo.getAllExercisesStream()} returns flowOf(listOf(
            ExerciseEntity(
                name = validExerciseState.name,
                reps = validExerciseState.reps,
                sets = validExerciseState.sets,
                weight = validExerciseState.weight,
            )))

        viewModel = TrackingViewModel(authRepository, workoutsRepo, offlineExercisesRepo)

        viewModel.initEditMode(editWorkout.id)
        advanceUntilIdle()
        viewModel.updateWorkoutName("Test Workout")

        viewModel.endWorkout()
        advanceUntilIdle()


        coVerify (exactly = 1){
            workoutsRepo.updateWorkout(match {
                it.name == "Test Workout" &&
                        it.ownerId == testUserId &&
                        it.exercises.size == 1 &&
                        it.exercises[0].name == validExerciseState.name
            })

        }

        coVerify (exactly = 1) { offlineExercisesRepo.deleteAllExercises() }


        assertEquals("Test Workout", viewModel.trackerUiState.workoutName)
        assertEquals("Error saving workout to database", viewModel.trackerUiState.errorMessage)


    }










}






