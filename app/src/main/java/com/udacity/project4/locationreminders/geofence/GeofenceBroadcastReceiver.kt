package com.udacity.project4.locationreminders.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Triggered by the Geofence.  Since we can have many Geofences at once, we pull the request
 * ID from the first Geofence, and locate it within the cached data in our Room DB
 *
 * Or users can add the reminders and then close the app, So our app has to run in the background
 * and handle the geofencing in the background.
 * To do that you can use https://developer.android.com/reference/android/support/v4/app/JobIntentService to do that.
 *
 */

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    private val ACTION_GEOFENCE_EVENT = "ACTION_GEOFENCE_EVENT"

    private val TAG = "GeofenceBroadcastReceiv"
    override fun onReceive(context: Context, intent: Intent) {

//TODO: implement the onReceive method to receive the geofencing events at the background
        if (intent.action == ACTION_GEOFENCE_EVENT) {
            GeofenceTransitionsJobIntentService.enqueueWork(context, intent)
        }
    }
}

/*package com.udacity.project4.locationreminders.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.udacity.project4.R
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject


/**
 * Triggered by the Geofence.  Since we can have many Geofences at once, we pull the request
 * ID from the first Geofence, and locate it within the cached data in our Room DB
 *
 * Or users can add the reminders and then close the app, So our app has to run in the background
 * and handle the geofencing in the background.
 * To do that you can use https://developer.android.com/reference/android/support/v4/app/JobIntentService to do that.
 *
 */

class GeofenceBroadcastReceiver : BroadcastReceiver()/*, CoroutineScope*/ {
    private val TAG = "GeofenceBroadcastReceiv"

//    private var coroutineJob: Job = Job()
//    overrid al coroutineScope = CoroutineScope(Dispatchers.IO + exceptionHandler)

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

//    @Inject
//    val remindersLocalRepository: ReminderDataSource? = null
    @Inject
    val _viewModel: RemindersListViewModel?=null

    override fun onReceive(context: Context, intent: Intent) {

//TODO: implement the onReceive method to receive the geofencing events at the background
        Log.d("zzzzzzzonReceive","onReceive")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent!!.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
        geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.


            val triggeringGeofences = geofencingEvent!!.triggeringGeofences


//        Interaction to the repository has to be through a coroutine scope
//            CoroutineScope(coroutineContext).launch(SupervisorJob()) {
//            CoroutineScope(coroutineContext).launch {
//            GlobalScope.launch(Dispatchers.Default) {

//            coroutineScope.launch {

            triggeringGeofences!!.forEach {
                //get the reminder with the request id
                    Log.d("zzzzzzzzzzonrecieve",it.requestId)
//                val result = remindersLocalRepository!!.getReminder(it.requestId)
                _viewModel!!.getReminder("268c14ee-11c9-4f13-bb71-e6556f1ceb3f"/*it.requestId*/)
//                if (result is com.udacity.project4.locationreminders.data.dto.Result.Success<ReminderDTO>) {
//                    val reminderDTO = result.data
//                    //send a notification to the user with the reminder details
//                    sendNotification(
//                        context, ReminderDataItem(
//                            reminderDTO.title,
//                            reminderDTO.description,
//                            reminderDTO.location,
//                            reminderDTO.latitude,
//                            reminderDTO.longitude,
//                            reminderDTO.id
//                        )
//                    )
//                    Log.i(TAG, reminderDTO.toString())
//
//                }
//                }
            }

            // Send notification and log the transition details.
//            sendNotification(geofenceTransitionDetails)
//            Log.i(TAG, geofenceTransitionDetails)
        } else {
            // Log the error.
            Log.e(TAG, context.getString(R.string.geofence_transition_invalid_type) +" "+geofenceTransition)
        }

    }


}*/