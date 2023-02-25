package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject


class SelectLocationFragment : BaseFragment(), GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding

    private lateinit var mMap :GoogleMap

    val REQUEST_CHECK_SETTINGS = 1

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var latitude=0.0
    private var longitude=0.0

    val REQUEST_TURN_DEVICE_LOCATION_ON=1

    @SuppressLint("MissingPermission")
    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
//        when {
//            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
//                // Precise location access granted.
//            }
//            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
//                // Only approximate location access granted.
//            } else -> {
//            // No location access granted.
//        }
//        }

        when {
            (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED  ) &&
                    (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED  )-> {
                // You can use the API that requires the permission.
//                createLocationRequest()
                getUserLocationOnMapAfterPermissionGranted()
//                createLocationRequest()

//                MapsClickListner()

//                checkingGpsEnabled()

            }
            else -> {
                // You can directly ask for the permission.

                // We don't rely on the result code, but just check the location setting again
//                checkDeviceLocationSettingsAndStartGeofence(false)

                AlertDialog.Builder(requireContext())
                    .setTitle("Permission Needed!, For get your location")
                    .setMessage("Location Permission Needed!")
                    .setPositiveButton(
                        "OK"
                    ) { dialog, which ->
                        onRequestPermissionsResult()
                    }
                    .setNegativeButton(
                        "CANCEL"
                    ) { dialog, which ->
                        // Permission is denied by the user
                    }
                    .create().show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkingGpsEnabled() {
        val mLocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Checking GPS is enabled
        val mGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (mGPS)
            mMap.isMyLocationEnabled = true
        else
            checkDeviceLocationSettingsAndStartGeofence(false)
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        binding.btnSave.setOnClickListener {
            if (longitude !=0.0 && latitude !=0.0)
                ConfirmationDialog()
        }
//        TODO: add the map setup implementation
//        MapsClickListner()
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//        TODO: zoom to the user location after taking his permission
        onRequestPermissionsResult()



//        TODO: call this function after the user confirms on the selected location
//        onLocationSelected()

        return binding.root
    }

    /*@SuppressLint("MissingPermission")
    private fun MapsClickListner() {
        // Get a handle to the fragment and register the callback.
        // Get a handle to the fragment and register the callback.
        //        childFragmentManager
        //            .findFragmentById(R.id.map).getMapAsync(this)

        // Initialize map fragment
        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Async map
        supportMapFragment!!.getMapAsync {
            mMap = it


            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL


            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);

    //            if ()mMap.isMyLocationEnabled
            /* mMap.clear() //clear old markers

            val googlePlex = CameraPosition.builder()
                .target(LatLng(37.4219999, -122.0862462))
                .zoom(10f)
                .bearing(0f)
                .tilt(45f)
                .build()

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null)

            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(0.0, 0.0))
                    .title("Marker")
                    .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_location))

            )*/
            // When map is loaded
            mMap.setOnMapClickListener { latLng -> // When clicked on map
                // Initialize marker options
                val markerOptions = MarkerOptions().icon(
                    bitmapDescriptorFromVector(
                        requireContext(),
                        R.drawable.ic_location
                    )
                )

                mMap.clear() //clear old markers

                val googlePlex = CameraPosition.builder()
                    .target(latLng)
                    .zoom(10f)
                    .bearing(0f)
                    .tilt(45f)
                    .build()

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null)


                // Set position of marker
                markerOptions.position(latLng)
                // Set title of marker
                markerOptions.title(latLng.latitude.toString() + " : " + latLng.longitude)
                // Remove all marker
    //                mMap.clear()
                // Animating to zoom the marker
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                // Add marker on map
                mMap.addMarker(markerOptions)

                ConfirmationDialog(latLng.latitude, latLng.longitude)
            }
        }
        // Return view

        /*val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment!!.getMapAsync { //mMap ->
            mMap=it
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            mMap.clear() //clear old markers

            val googlePlex = CameraPosition.builder()
                .target(LatLng(37.4219999, -122.0862462))
                .zoom(10f)
                .bearing(0f)
                .tilt(45f)
                .build()

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null)

            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(0.0, 0.0))
                    .title("Marker")
                    .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_location))

            )*/


        //        }
    }*/
    @SuppressLint("MissingPermission")
    fun getUserLocationOnMapAfterPermissionGranted(){

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                location?.let {
                    latitude = it.latitude
                    longitude = it.longitude
                    zoomToUserLocation()
                }
            }
//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location: Location? ->
//                // Got last known location. In some rare situations this can be null.
//                location?.let {
//                    latitude = it.latitude
//                    longitude = it.longitude
//                    zoomToUserLocation()
//                }
//            }
    }

    private fun zoomToUserLocation() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 10f))
        mMap.addMarker(MarkerOptions().position(LatLng(latitude, longitude)))
    }

    private fun onLocationSelected() {
        //        TODO: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence


        _viewModel.navigationCommand.value =
            NavigationCommand.Back

    }

    private fun ConfirmationDialog(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmation")
        builder.setMessage("Are you sure that this location is what you need it?")
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            Toast.makeText(requireContext(),
                android.R.string.yes, Toast.LENGTH_SHORT).show()
            _viewModel.latitude.value=latitude
            _viewModel.longitude.value=longitude
            _viewModel.reminderSelectedLocationStr.value=latitude.toString()+""+longitude.toString()
            onLocationSelected()

        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            Toast.makeText(requireContext(),
                android.R.string.no, Toast.LENGTH_SHORT).show()
        }

//        builder.setNeutralButton("Maybe") { dialog, which ->
//            Toast.makeText(requireContext(),
//                "Maybe", Toast.LENGTH_SHORT).show()
//        }
        builder.show()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO: Change the map type based on the user's selection.
        R.id.normal_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)

    }

    // Get a handle to the GoogleMap object and display marker.
//    override fun onMapReady(googleMap: GoogleMap) {
//        googleMap.addMarker(
//            MarkerOptions()
//                .position(LatLng(0.0, 0.0))
//                .title("Marker")
//                .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_location))
//
//        )
//    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap =
            Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun onRequestPermissionsResult() {
        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show a permission
        // rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }



    /*fun createLocationRequest() {
       /* val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)


        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())


        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
//            locationSettingsResponse.locationSettingsStates.

 //        TODO: add style to the map
//        TODO: put a marker to location that the user selected

//            MapsClickListner()
            getUserLocationOnMapAfterPermissionGranted()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().

                    exception.startResolutionForResult(requireActivity(),
                        REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }*/

    }*/

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
                getUserLocationOnMapAfterPermissionGranted()
            }
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        Log.d("zzzzzonMyLocationClick",""+true)
        return true
    }

    override fun onMyLocationClick(p0: Location) {
        Log.d("zzzzzonMyLocationClick",""+p0)

        Toast.makeText(requireContext(), "Current location:\n" + p0.longitude +" "+p0.latitude, Toast.LENGTH_LONG)
            .show()
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap=p0
        if((ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED  ) &&
                (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED  )) {
            checkingGpsEnabled()
        }


        mMap.setOnMapClickListener { latLng -> // When clicked on map
            // Initialize marker options
            val markerOptions = MarkerOptions().icon(
                bitmapDescriptorFromVector(
                    requireContext(),
                    R.drawable.ic_location
                )
            )

            mMap.clear() //clear old markers

            val googlePlex = CameraPosition.builder()
                .target(latLng)
                .zoom(10f)
                .bearing(0f)
                .tilt(45f)
                .build()

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null)


            // Set position of marker
            markerOptions.position(latLng)
            // Set title of marker
            markerOptions.title(latLng.latitude.toString() + " : " + latLng.longitude)
            // Remove all marker
            //                mMap.clear()
            // Animating to zoom the marker
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
            // Add marker on map
            mMap.addMarker(markerOptions)

            latitude=latLng.latitude
            longitude=latLng.longitude
//            ConfirmationDialog(latLng.latitude, latLng.longitude)
        }

    }
}
