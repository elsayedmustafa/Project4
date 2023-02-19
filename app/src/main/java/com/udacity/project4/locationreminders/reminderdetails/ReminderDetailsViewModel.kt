package com.udacity.project4.locationreminders.reminderdetails

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.launch

class ReminderDetailsViewModel(val app: Application, val dataSource: ReminderDataSource) :
    BaseViewModel(app) {
    val reminderTitle = MutableLiveData<String>()
    val reminderDescription = MutableLiveData<String>()
    val reminderSelectedLocationStr = MutableLiveData<String>()
    val selectedPOI = MutableLiveData<PointOfInterest>()
    val latitude = MutableLiveData<Double>()
    val longitude = MutableLiveData<Double>()



    fun GetReminder(reminderKey: String) {
        showLoading.value = true
        viewModelScope.launch {
            val result = dataSource.getReminder(
                reminderKey
            )
            showLoading.value = false

            showLoading.postValue(false)
            when (result) {
                is Result.Success<*> -> {
                    var reminderDTO = result.data as ReminderDTO
                    reminderTitle.value=reminderDTO.title!!
                    reminderSelectedLocationStr.value=reminderDTO.latitude!!.toString() +"\n "+reminderDTO.longitude.toString()
                    reminderDescription.value=reminderDTO.description!!
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }

//            showToast.value = app.getString(R.string.reminder_saved)
//            navigationCommand.value = NavigationCommand.Back
        }
    }


}