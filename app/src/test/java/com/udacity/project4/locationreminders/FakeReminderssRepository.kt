package com.udacity.project4.locationreminders

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

class FakeReminderssRepository(private val reminderDTOList: ArrayList<ReminderDTO>) : ReminderDataSource {
    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return Result.Success(reminderDTOList)
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminderDTOList.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        val searchResults =
            reminderDTOList.filter {
                it.id.contains(id, ignoreCase = true)
            }
        if (shouldReturnError)
            return Result.Success(searchResults.get(0))
        else
            return Result.Error("Not Found")

    }

    override suspend fun deleteAllReminders() {

    }

}
