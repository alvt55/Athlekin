package com.example.athlekin.data

import com.example.athlekin.model.Exercise
import com.example.athlekin.model.WorkoutDoc
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject

class DataSeeder @Inject constructor(
    private val workoutsRepo: WorkoutsRepo,
    private val authRepository: AuthRepository
) {

    suspend fun seedWorkouts() {
        val userId = authRepository.currentUserIdFlow.first() ?: return

        val workouts = mutableListOf<WorkoutDoc>()

        // 1. Strong Progress (> 5%) - Bench Press
        // Volumes: 3000 -> 3150 -> 3450 (+15%)
        workouts.add(
            createWorkout(
                userId,
                "Push Day",
                14,
                listOf(
                    Exercise(
                        0,
                        "Bench Press",
                        reps = 10,
                        sets = 3,
                        weight = 100,
                        comments = "Felt very strong, good progression and form is perfect"
                    )
                )
            )
        )
        workouts.add(
            createWorkout(
                userId,
                "Push Day",
                10,
                listOf(Exercise(0, "Bench Press", reps = 10, sets = 3, weight = 105))
            )
        )
        workouts.add(
            createWorkout(
                userId,
                "Push Day",
                7,
                listOf(Exercise(0, "Bench Press", reps = 10, sets = 3, weight = 115))
            )
        )

        // 2. Hard Plateau (0%) - Squat
        // Volumes: 3000 -> 3000 -> 3000
        workouts.add(
            createWorkout(
                userId,
                "Leg Day",
                13,
                listOf(Exercise(0, "Squat", reps = 5, sets = 3, weight = 200))
            )
        )
        workouts.add(
            createWorkout(
                userId,
                "Leg Day",
                9,
                listOf(Exercise(0, "Squat", reps = 5, sets = 3, weight = 200))
            )
        )
        workouts.add(
            createWorkout(
                userId,
                "Leg Day",
                6,
                listOf(
                    Exercise(
                        0,
                        "Squat",
                        reps = 5,
                        sets = 3,
                        weight = 200,
                        comments = "i hate this, losing motivation i want to quit, maybe i should stop"
                    )
                )
            )
        )

        // 3. Soft Plateau (0.1% - 4.9%) - Overhead Press
        // Volumes: 3000 -> 3030 -> 3060 (+2%)
        workouts.add(
            createWorkout(
                userId,
                "Push Day",
                12,
                listOf(Exercise(0, "Overhead Press", reps = 10, sets = 3, weight = 100))
            )
        )
        workouts.add(
            createWorkout(
                userId,
                "Push Day",
                8,
                listOf(Exercise(0, "Overhead Press", reps = 10, sets = 3, weight = 101))
            )
        )
        workouts.add(
            createWorkout(
                userId,
                "Push Day",
                5,
                listOf(
                    Exercise(
                        0,
                        "Overhead Press",
                        reps = 10,
                        sets = 3,
                        weight = 102,
                        comments = "not making to much progress, losing hope, also feel like i might injure myself"
                    )
                )
            )
        )

        // 4. Regression (Negative) - Deadlift
        // Volumes: 1500 -> 1400 -> 1300 (-13.3%)
        workouts.add(
            createWorkout(
                userId,
                "Pull Day",
                11,
                listOf(
                    Exercise(
                        0,
                        "Deadlift",
                        reps = 5,
                        sets = 1,
                        weight = 300,
                        comments = "feel good about the pullups, good muscle contractions"
                    )
                )
            )
        )
        workouts.add(
            createWorkout(
                userId,
                "Pull Day",
                7,
                listOf(Exercise(0, "Deadlift", reps = 5, sets = 1, weight = 280))
            )
        )
        workouts.add(
            createWorkout(
                userId,
                "Pull Day",
                4,
                listOf(
                    Exercise(
                        0,
                        "Deadlift",
                        reps = 5,
                        sets = 1,
                        weight = 260,
                        comments = "feeling discouraged, getting weaker"
                    )
                )
            )
        )

        workouts.forEach {
            workoutsRepo.createWorkout(it)
        }
    }

    private fun createWorkout(
        userId: String,
        name: String,
        daysAgo: Int,
        exercises: List<Exercise>
    ): WorkoutDoc {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        calendar.set(Calendar.HOUR_OF_DAY, 17)
        calendar.set(Calendar.MINUTE, 0)

        return WorkoutDoc(
            ownerId = userId,
            name = name,
            createdAt = Timestamp(calendar.time),
            exercises = exercises
        )
    }
}
