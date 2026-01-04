package com.example.athlekin.data


import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import com.example.athlekin.datasource.WorkoutsRemoteDataSource
import com.google.common.io.Files.map
import com.google.firebase.Timestamp
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.stream.Collectors.groupingBy
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




    // possible parameters can be:
    // start and end dates to query
    // duration of workouts (e.g 1 hr)
    // number of slots to return N
    suspend fun queryCalendars(start : Long, end : Long) {
        Log.d(DEBUG_TAG, "queryCalendars() firing")

        val availableTimes = mutableListOf<Pair<Long, Long>>()


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
                    availableTimes.add(Pair(beginVal, endVal))


                }
            }
        }


        if (availableTimes.isNotEmpty()) {

            Log.i(DEBUG_TAG, "list of availabilities: $availableTimes")

            // loop through available times, keeping note of day of week and time
            // rank based on previous data
            // sort these slots
            // pick top N slots

            val workouts = workoutRepo.getWorkouts(authRepo.currentUserIdFlow).first()

            val preferredDays: Map<Int, Int> = workouts
                .groupingBy { it.createdAt.dayOfWeek() }
                .eachCount()

            val preferredHours: Map<Int, Int> = workouts
                .groupingBy { it.createdAt.hourOfDay() }
                .eachCount()

            println(preferredHours)
            println(preferredDays)





        }



    }


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



