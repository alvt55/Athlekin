package com.example.athlekin.data


import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar




const val PROJECTION_TITLE_INDEX: Int = 0
const val PROJECTION_BEGIN_INDEX: Int = 1
const val PROJECTION_END_INDEX: Int = 2






const val DEBUG_TAG: String = "CALENDAR"
class CalendarRepo(
    private val context: Context
) {

    val INSTANCE_PROJECTION: Array<String> = arrayOf(
        CalendarContract.Instances.TITLE, // 0
        CalendarContract.Instances.BEGIN, // 1
        CalendarContract.Instances.END, // 2
    )


    // possible parameters can be:
    // start and end dates to query
    // duration of workouts (e.g 1 hr)
    // number of slots to return N
    suspend fun queryCalendars() {
        Log.d(DEBUG_TAG, "queryCalendars() firing")


        val availableTimes = mutableListOf<Pair<Long, Long>>()


        // set start and end boundaries
        val startMillis: Long = Calendar.getInstance().run {
            set(2025, 11, 22, 8, 0)
            timeInMillis
        }
        val endMillis: Long = Calendar.getInstance().run {
            set(2025, 11, 24, 8, 0)
            timeInMillis
        }


        val selection: String = "${CalendarContract.Instances.ALL_DAY} = ?"
        val selectionArgs: Array<String> = arrayOf("0")


        // build the URI with the start/end boundaries
        val builder: Uri.Builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, startMillis)
        ContentUris.appendId(builder, endMillis)


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

        }



    }


}




