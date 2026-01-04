package com.example.athlekin.data


import android.R.attr.end
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import com.google.firebase.Timestamp
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import javax.inject.Inject


const val PROJECTION_TITLE_INDEX: Int = 0
const val PROJECTION_BEGIN_INDEX: Int = 1
const val PROJECTION_END_INDEX: Int = 2






const val DEBUG_TAG: String = "CALENDAR"

class CalendarRepo @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workoutRepo: WorkoutsRepo,
    private val authRepo: AuthRepository
){

    val INSTANCE_PROJECTION: Array<String> = arrayOf(
        CalendarContract.Instances.TITLE, // 0
        CalendarContract.Instances.BEGIN, // 1
        CalendarContract.Instances.END, // 2
    )




    // takes in start and end boundaries - computes the best timeslot based on past workouts
    // TODO: handle date picker time zones - currently it is off due to epoch and not local
    suspend fun queryCalendars(start : Long, end : Long) {
        Log.d(DEBUG_TAG, "${start.toReadable()} -> ${end.toReadable()}")

        val busySlots = mutableListOf<Pair<Long, Long>>()


        val selection: String = "${CalendarContract.Instances.ALL_DAY} = ?"
        val selectionArgs: Array<String> = arrayOf("0")


        // build the URI with the start/end boundaries
        val builder: Uri.Builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, start)
        ContentUris.appendId(builder, end)


        // setup context to I/O
        withContext(Dispatchers.IO) {


            context.contentResolver.query(
                builder.build(),
                INSTANCE_PROJECTION,
                selection,
                selectionArgs,
                null
            )?.use { cur ->
                if (cur.count == 0) {
                    Log.d(DEBUG_TAG, "No events found")
                }
                while (cur.moveToNext()) {


                    val titleVal = cur.getString(PROJECTION_TITLE_INDEX)
                    val beginVal = cur.getLong(PROJECTION_BEGIN_INDEX)
                    val endVal = cur.getLong(PROJECTION_END_INDEX)



                    Log.i(DEBUG_TAG, "Event: $titleVal")
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = beginVal
                    }
                    val formatter = SimpleDateFormat("MM/dd/yyyy")
                    Log.i(DEBUG_TAG, "Date: ${formatter.format(calendar.time)}")
                    Log.i(DEBUG_TAG, "start: $beginVal, end: $endVal")
                    busySlots.add(Pair(beginVal, endVal))


                }
            }
        }

        val timeSlots = findAvailableSlots(busySlots, start, end)

        if (timeSlots.isNotEmpty()) {
            Log.i(DEBUG_TAG, "list of available slots: $timeSlots")

            // getting the user's workout frequency information
            val workouts = workoutRepo.getWorkouts(authRepo.currentUserIdFlow).first()

            val preferredDays: Map<Int, Int> = workouts
                .groupingBy { it.createdAt.dayOfWeek() }
                .eachCount()

            val preferredHours: Map<Int, Int> = workouts
                .groupingBy { it.createdAt.hourOfDay() }
                .eachCount()

            println(preferredHours)
            println(preferredDays)

            var bestScore : Pair<Pair<Long, Long>, Double> = Pair(Pair(0L, 0L), 0.0)

            for (slot in timeSlots) {
                val score = scoreSlot(slot.first, preferredDays, preferredHours)
                println(score)
                if (score > bestScore.second) {
                    bestScore = Pair(slot, score)
                }
            }

            println(bestScore)





        }







        }



    }

fun scoreSlot(
    slotStart: Long,
    preferredDays: Map<Int, Int>,
    preferredHours: Map<Int, Int>
): Double {
    // Convert slotStart to Calendar to extract day and hour
    val calendar = Calendar.getInstance().apply { timeInMillis = slotStart }
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

    // Get counts from maps (default to 0)
    val dayCount = preferredDays[dayOfWeek] ?: 0
    val hourCount = preferredHours[hourOfDay] ?: 0

    // Normalize counts to fractions (so scoring is relative)
    val maxDayCount = preferredDays.values.maxOrNull() ?: 1
    val maxHourCount = preferredHours.values.maxOrNull() ?: 1

    val dayScore = dayCount.toDouble() / maxDayCount
    val hourScore = hourCount.toDouble() / maxHourCount

    // Weighted sum: for example, day preference = 40%, hour preference = 60%
    return 0.4 * dayScore + 0.6 * hourScore
}


fun findAvailableSlots(busySlots : List<Pair<Long, Long>>, start : Long, end : Long) : List<Pair<Long, Long>> {
    val sortedBusySlots = busySlots.sortedBy { it.first }

    val availableOneHourSlots = mutableListOf<Pair<Long, Long>>()
    var currentTime = start

    // for each busy event
    for ((eventStart, eventEnd) in sortedBusySlots) {
        // While there is at least 1 full hour before this event
        while (currentTime + 60 * 60 * 1000 <= eventStart) {
            val slotEnd = currentTime + 60 * 60 * 1000
            availableOneHourSlots.add(currentTime to slotEnd)
            currentTime = slotEnd
        }

        // move currentTime forward past this event if needed
        currentTime = maxOf(currentTime, eventEnd)
    }

    // After the last event, fill remaining hours until query end
    while (currentTime + 60 * 60 * 1000 <= end) {
        val slotEnd = currentTime + 60 * 60 * 1000
        availableOneHourSlots.add(currentTime to slotEnd)
        currentTime = slotEnd
    }

    return availableOneHourSlots
}




fun Timestamp.dayOfWeek(): Int {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = this@dayOfWeek.toDate().time
    }
    return calendar.get(Calendar.DAY_OF_WEEK)
}

fun Timestamp.hourOfDay(): Int {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = this@hourOfDay.toDate().time
    }
    return calendar.get(Calendar.HOUR_OF_DAY)
}


fun Long.toReadable(): String {
    val sdf = SimpleDateFormat("EEE, MMM dd yyyy HH:mm") // e.g., Mon, Jun 21 2026 15:00
    sdf.timeZone = TimeZone.getDefault() // your local timezone
    return sdf.format(Date(this))
}
