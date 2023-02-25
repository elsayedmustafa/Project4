package com.udacity.project4.util

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import java.util.*

//Use FakeDataSource that acts as a test double to the LocalDataSource
object FakeDataSource  {

//    TODO: Create a fake data source to act as a double to the real data source
    var ReminderList = listOf(
    ReminderDTO("Title 1","description 1", "location1", 2018.0,200.2,UUID.randomUUID().toString()),
    ReminderDTO("Title 2","description 2", "location2", 2016.0,2002.0,UUID.randomUUID().toString()),
    ReminderDTO("Title 3","description 3", "location3", 2016.0,2532.0,UUID.randomUUID().toString()),
    ReminderDTO("Title 4","description 4", "location4", 2014.0,541.0,UUID.randomUUID().toString()),
    ReminderDTO("Title 5","description 5", "location5", 2016.0,4512.0,UUID.randomUUID().toString()),
    ReminderDTO("Title 6", "description 6", "location6", 2014.0,1212.4,UUID.randomUUID().toString()),
    ReminderDTO("Title 7", "description 7", "location7", 2016.0,5421.0,UUID.randomUUID().toString()),
    ReminderDTO("Title 8", "description 8", "location8", 2014.0,5545.0,UUID.randomUUID().toString()),
    ReminderDTO("Title 9","description 9", "location9", 2016.0,2553.0,UUID.randomUUID().toString()),
    ReminderDTO("Title 10","description 10", "location10", 20180.0,5545.0,UUID.randomUUID().toString()))

    fun getReminders(): List<ReminderDTO> {
            return ReminderList
    }

    fun getReminder(): ReminderDTO {
        return ReminderList.get(0)

    }
    fun getNoReminder(): ReminderDTO? {
        return null

    }
//
//    fun getReminder_ByID(reminderId: String): ReminderDTO {
//        return ReminderList.get(0)
//
//    }


}