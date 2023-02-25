package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.util.FakeDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Executors

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    TODO: Add testing implementation to the RemindersDao.kt

    private lateinit var database: RemindersDatabase
    private lateinit var remindersDao: RemindersDao

    private lateinit var reminderDTO: ReminderDTO

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).setTransactionExecutor(Executors.newSingleThreadExecutor())
            .build()

        remindersDao = database.reminderDao()
        reminderDTO= FakeDataSource.getReminder()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun saveReminder_getReminderById() = runBlocking {
        // GIVEN - Save a Reminder.
        remindersDao.saveReminder(reminderDTO)

        // WHEN - Get the Reminder by name from the database.
        val result = remindersDao.getReminderById(reminderDTO.id)

        // THEN - The loaded data contains the expected values.
        assertThat(result, notNullValue())
        assertThat(result.title, `is`(reminderDTO.title))
        assertThat(result.description, `is`(reminderDTO.description))
        assertThat(result.latitude, `is`(reminderDTO.latitude))
    }

    @Test
    fun saveReminder_getReminders() = runBlocking {
        // GIVEN - Save Reminders.
        val remindersList = FakeDataSource.getReminders()
        remindersList.forEach {
            remindersDao.saveReminder(it)
        }

        // WHEN - Get all saved Reminders.
        val result = remindersDao.getReminders() //.getOrAwaitValue()

        // THEN - Return all saved Reminders as they were.
        assertThat(result.size, `is`(remindersList.size))
        assertThat(result, CoreMatchers.hasItem(reminderDTO))
    }

    @Test
    fun saveReminder_DeleteAllReminders() = runBlocking {
        // GIVEN - Save  Reminders.
        val remindersList = FakeDataSource.getReminders()
        remindersList.forEach {
            remindersDao.saveReminder(it)
        }

        // WHEN - Get all saved Reminders.
        remindersDao.deleteAllReminders()
        val result = remindersDao.getReminders() //.getOrAwaitValue()

        // THEN - Return all saved Reminders as they were.
        assertThat(result.size, `is`(0))
    }
}