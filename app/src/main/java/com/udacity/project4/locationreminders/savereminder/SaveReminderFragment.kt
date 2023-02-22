package com.udacity.project4.locationreminders.savereminder

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.Constants
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()

    private lateinit var binding: FragmentSaveReminderBinding

    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.Q

    private val TAG = "SaveReminderFragment"

    private val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE=12
    private val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE=1

    val REQUEST_TURN_DEVICE_LOCATION_ON=1

    //Geofence
    lateinit var geofencingClient: GeofencingClient
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    var geofenceList= ArrayList<Geofence>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)

        setDisplayHomeAsUpEnabled(true)

        binding.viewModel = _viewModel

        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.selectLocation.setOnClickListener {
            //            Navigate to another fragment to get the user location
            _viewModel.navigationCommand.value =
                NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }

        requestForegroundAndBackgroundLocationPermissions()

        binding.saveReminder.setOnClickListener {
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription.value
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude.value
            val longitude = _viewModel.longitude.value
            val key = UUID.randomUUID().toString()
//            TODO: use the user entered reminder details to:
//             1) add a geofencing request
            AddingGeofence(key , latitude!!.toDouble(),longitude!!.toDouble() )

            geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent)?.run {
                addOnSuccessListener {
                    // Geofences added
                    // ...
                    Log.d("zaddOnSuccessListener->","Geofences added")
                }
                addOnFailureListener {
                    // Failed to add geofences
                    Log.d("zaddOnFailureListener->","Failed to add geofences")

                }
            }

//             2) save the reminder to the local db
            _viewModel.validateAndSaveReminder(ReminderDataItem(title, description,location,latitude,longitude , key))
        }

        //Geofence
        geofencingClient = LocationServices.getGeofencingClient(requireContext())


    }


    @TargetApi(29)
    private fun foregroundAndBackgroundLocationPermissionApproved(): Boolean {
        val foregroundLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION))
        val backgroundPermissionApproved =
            if (runningQOrLater) {
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )
            } else {
                true
            }
        return foregroundLocationApproved && backgroundPermissionApproved
    }

    @TargetApi(29 )
    private fun requestForegroundAndBackgroundLocationPermissions() {
        if (foregroundAndBackgroundLocationPermissionApproved())
            return
        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val resultCode = when {
            runningQOrLater -> {
                permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
            }
            else ->{
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            }
        }
        Log.d(TAG, "Request foreground only location permission + ->permissionsArraysize"+permissionsArray.size)
//        ActivityCompat.requestPermissions(
//            requireActivity(),
//            permissionsArray,
//            resultCode
//        )
        locationPermissionRequest.launch(permissionsArray)
    }

    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
//                 You can use the API that requires the Manifest.permission.
        when {
            (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED) -> {
                        //TODO
                    checkDeviceLocationSettingsAndStartGeofence()

            }
            else -> {
//                Snackbar.make(
//                    binding.parentSaveReminder,
//                    R.string.permission_denied_explanation,
//                    Snackbar.LENGTH_INDEFINITE
//                )
//                    .setAction(R.string.settings) {
//                        startActivity(Intent().apply {
//                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
//                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        })
//                    }.show()
//                requestForegroundAndBackgroundLocationPermissions()


            }
        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        Log.d(TAG, "onRequestPermissionResult")
//
//        if (
//            grantResults.isEmpty() ||
//            grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
//            (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
//                    grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
//                    PackageManager.PERMISSION_DENIED))
//        {
//            Snackbar.make(
//                binding.activityMapsMain,
//                R.string.permission_denied_explanation,
//                Snackbar.LENGTH_INDEFINITE
//            )
//                .setAction(R.string.settings) {
//                    startActivity(Intent().apply {
//                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
//                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    })
//                }.show()
//        } else {
//            checkDeviceLocationSettingsAndStartGeofence()
//        }
//    }

    //Geofence
    private fun AddingGeofence(key:String ,latitude:Double ,longitude:Double ){
        geofenceList.add(
            Geofence.Builder()
            // Set the request ID of the geofence. This is a string to identify this
            // geofence.
            .setRequestId(key)

            // Set the circular region of this geofence.
            .setCircularRegion(
                latitude,
                longitude,
                Constants.GEOFENCE_RADIUS_IN_METERS
            )

            // Set the expiration duration of the geofence. This geofence gets automatically
            // removed after this period of time.
            .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

            // Set the transition types of interest. Alerts are only generated for these
            // transition. We track entry and exit transitions in this sample.
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)

            // Create the geofence.
            .build())
    }
    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofenceList)
        }.build()
    }
    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            // We don't rely on the result code, but just check the location setting again
            checkDeviceLocationSettingsAndStartGeofence(false)
        }
    }

    private fun checkDeviceLocationSettingsAndStartGeofence(resolve:Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(requireActivity(),
                        REQUEST_TURN_DEVICE_LOCATION_ON)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d("TAG", "Error geting location settings resolution: " + sendEx.message)
                }
            } else {
                Snackbar.make(
                    binding.root,
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettingsAndStartGeofence()
                }.show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if ( it.isSuccessful ) {
//              TODO
//               getUserLocationOnMapAfterPermissionGranted()

            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkingGpsEnabled() {
        val mLocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Checking GPS is enabled
        val mGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (mGPS)
            //TODO

        else
            checkDeviceLocationSettingsAndStartGeofence(false)
    }
}
