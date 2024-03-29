package com.udacity.project4.locationreminders

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import java.util.*

class FakeReminderssRepository : ReminderDataSource {

    var reminderDTOList = mutableListOf(
        ReminderDTO("Title 1","description 1", "location1", 2018.0,200.2, UUID.randomUUID().toString()),
        ReminderDTO("Title 2","description 2", "location2", 2016.0,2002.0, UUID.randomUUID().toString()),
        ReminderDTO("Title 3","description 3", "location3", 2016.0,2532.0, UUID.randomUUID().toString()),
        ReminderDTO("Title 4","description 4", "location4", 2014.0,541.0, UUID.randomUUID().toString()),
        ReminderDTO("Title 5","description 5", "location5", 2016.0,4512.0, UUID.randomUUID().toString()),
        ReminderDTO("Title 6", "description 6", "location6", 2014.0,1212.4, UUID.randomUUID().toString()),
        ReminderDTO("Title 7", "description 7", "location7", 2016.0,5421.0, UUID.randomUUID().toString()),
        ReminderDTO("Title 8", "description 8", "location8", 2014.0,5545.0, UUID.randomUUID().toString()),
        ReminderDTO("Title 9","description 9", "location9", 2016.0,2553.0, UUID.randomUUID().toString()),
        ReminderDTO("Title 10","description 10", "location10", 20180.0,5545.0, UUID.randomUUID().toString()))


    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (!shouldReturnError)
            return Result.Success(reminderDTOList)
        else
            return Result.Error("Unable to get retrieved!")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminderDTOList.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        val searchResults =
            reminderDTOList.filter {
                it.id.contains(id, ignoreCase = true)
            }
        if (!shouldReturnError)
            return Result.Success(searchResults.get(0))
        else
            return Result.Error("Unable to get retrieved!")

    }

    override suspend fun deleteAllReminders() {
        reminderDTOList.clear()
    }

}
