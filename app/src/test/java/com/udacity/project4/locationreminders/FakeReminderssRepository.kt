package com.udacity.project4.locationreminders

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

class FakeReminderssRepository(private val reminderDTOList: List<ReminderDTO>) : ReminderDataSource {
    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return Result.Success(reminderDTOList)
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        TODO("Not yet implemented")
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        val searchResults =
            reminderDTOList.filter {
                it.id.contains(id, ignoreCase = true)
            }
        if (searchResults.size>0)
        return Result.Success(searchResults.get(0))
        else
            return Result.Error("Not Found")

    }

    override suspend fun deleteAllReminders() {

    }

}
