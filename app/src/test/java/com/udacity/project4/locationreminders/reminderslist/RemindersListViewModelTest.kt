package com.udacity.project4.locationreminders.reminderslist

//import androidx.lifecycle.ProcessLifecycleOwner.get
//import org.koin.core.context.GlobalContext.get
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.FakeDataSource
import com.udacity.project4.locationreminders.FakeReminderssRepository
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.utils.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.*
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest : KoinTest{

    //TODO: provide testing to the RemindersListViewModel and its live data objects

    private lateinit var viewModelz: RemindersListViewModel

    private lateinit var reminderssRepository: FakeReminderssRepository
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @After
    fun stopKoinTTT(){
        stopKoin()
    }
    @Before
    fun init() {
        val testModule = module {
            viewModel {
                RemindersListViewModel(
                    get(),
                    get() as ReminderDataSource
                )

            }
            single<ReminderDataSource> {
                FakeReminderssRepository(
                    // GIVEN - Reminder repository.
                    FakeDataSource.ReminderList
                )
            }
        }

        stopKoin()
        startKoin {
            androidContext(ApplicationProvider.getApplicationContext())
            modules(listOf(testModule))
        }


        viewModelz = get()

        reminderssRepository = get<ReminderDataSource>() as FakeReminderssRepository


    }

    @Test
    fun getAllReminders_RemindersExist()= mainCoroutineRule.runBlockingTest  {
        // GIVEN - Reminder repository.

        // WHEN - Get all Reminders.
        viewModelz.loadReminders()
        val result = viewModelz.remindersList.getOrAwaitValue()

        // THEN - Verify that Reminders exist and are fetched successfully.
        MatcherAssert.assertThat(result.isNullOrEmpty(), CoreMatchers.`is`(false))
    }

    @Test
    fun getAllReminders_TestLoading() = mainCoroutineRule.runBlockingTest {
        // GIVEN - Reminder repository.
        mainCoroutineRule.pauseDispatcher()

        viewModelz.loadReminders()
        MatcherAssert.assertThat(viewModelz.showLoading.getOrAwaitValue(), CoreMatchers.`is`(true))

        mainCoroutineRule.resumeDispatcher()
        MatcherAssert.assertThat(viewModelz.showLoading.getOrAwaitValue(), CoreMatchers.`is`(false))



    }

    @Test
    fun SaveReminders_GetAllReminderWithError() = runBlocking {
        // GIVEN
        reminderssRepository.setReturnError(true)

        // WHEN - Get saved Reminder.

        var reminderResult = reminderssRepository.getReminders()

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