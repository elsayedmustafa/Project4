package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.util.FakeDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Executors

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {
    //    TODO: Add testing implementation to the RemindersLocalRepository.kt


    private lateinit var database: RemindersDatabase
    private lateinit var remindersDao: RemindersDao

    private lateinit var reminderDTO: ReminderDTO


    private lateinit var repository: RemindersLocalRepository

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
        repository =RemindersLocalRepository(remindersDao)
    }

    @After
    fun closeDb() {
        database.close()
    }
    @Test
    fun SaveReminders_GetAllReminder() = runBlocking {
        // GIVEN - Save 15 Reminders.
        val remindersList = FakeDataSource.getReminders()
        remindersList.forEach {
            remindersDao.saveReminder(it)
        }


        // WHEN - Get all saved Reminders.
        var remindersListResult = repository.getReminders()

        var listResult:List<ReminderDTO> = emptyList()
        when(remindersListResult){
            is Result.Success<*> -> {
                listResult = (remindersListResult.data as List<ReminderDTO>)
//
            }
            is Result.Error ->{
            }
        }

        // THEN - Return all saved Reminders as they were.
        MatcherAssert.assertThat(listResult.size, CoreMatchers.`is`(remindersList.size))
        MatcherAssert.assertThat(listResult, CoreMatchers.hasItem(reminderDTO))
    }

    @Test
    fun SaveReminders_GetReminderByID() = runBlocking {
        // GIVEN
        val reminder = FakeDataSource.getReminder()
            remindersDao.saveReminder(reminder)

        // WHEN - Get saved Reminder.
        var reminderResult = repository.getReminder(reminder.id)

        var reminderFromResult = ReminderDTO(null,null,null,null,null,"")
        when(reminderResult){
            is Result.Success<*> -> {
                reminderFromResult = (reminderResult.data as ReminderDTO)
//
            }
            is Result.Error ->{

            }
        }

        // THEN - Return saved reminder .
        MatcherAssert.assertThat(reminderFromResult.id, CoreMatchers.`is`(reminder.id))
        MatcherAssert.assertThat(reminderFromResult.description, CoreMatchers.`is`(reminder.description))
        MatcherAssert.assertThat(reminderFromResult.title, CoreMatchers.`is`(reminder.title))
        MatcherAssert.assertThat(reminderFromResult.longitude, CoreMatchers.`is`(reminder.longitude))
        MatcherAssert.assertThat(reminderFromResult.latitude, CoreMatchers.`is`(reminder.latitude))
    }


    @Test
    fun SaveReminders_GetError() = runBlocking {
        // GIVEN
//        val reminder = FakeDataSource.getNoReminder()

        // WHEN - Get saved Reminder.
        var reminderResult = repository.getReminder("reminder.id")

        var reminderFromResult=null
        var messageResult=""
        when(reminderResult){
            is Result.Success<*> -> {
            }
            is Result.Error ->{
                messageResult = reminderResult.message.toString()

            }
        }

        // THEN - Return null .
        MatcherAssert.assertThat(reminderFromResult, CoreMatchers.nullValue())
        MatcherAssert.assertThat(messageResult, CoreMatchers.`is`("Reminder not found!"))

    }

}