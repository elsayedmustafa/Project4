package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.locationreminders.welcomreminderapp.WelcomeRemindersViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorFragment
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : KoinTest {

//    TODO: test the navigation of the fragments.
//    TODO: test the displayed data on the UI.
//    TODO: add testing for the error messages.

    private lateinit var viewModelz: RemindersListViewModel


    private lateinit var appContext: Application

    private val dataBindingIdlingResource = DataBindingIdlingResource()

//    @get:Rule
//    var instantExecutorRule = ActivityTestRule(RemindersActivityTest::class.java as Activity)
    /**
     * As we use Koin as a Service Locator library to develop the app, we'll also
     * use Koin to test the code. At this step we will initialize Koin related
     * code to be able to use it in our testing.
     */
    @Before
    fun init() {
        // Stop the original app Koin.
        stopKoin()
        appContext = ApplicationProvider.getApplicationContext()

        // Declare a new Koin module.
        /**
         * use Koin Library as a service locator
         */
        val myModule = module {
            //Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
            viewModel {

                WelcomeRemindersViewModel(
                    get()
                )
            }
            viewModel {
                RemindersListViewModel(
                    get(),
                    get() as ReminderDataSource
                )

            }
            //Declare singleton definitions to be later injected using by inject()
            single {
                //This view model is declared singleton to be used across multiple fragments
                SaveReminderViewModel(
                    get(),
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        startKoin {
            androidContext(appContext)
            modules(
                listOf(
                    myModule
                )
            )
        }

        viewModelz = get()

    }

    @Before
    fun registerIdlingResources() {
        IdlingRegistry.getInstance().apply {
            register(EspressoIdlingResource.countingIdlingResource)
            register(dataBindingIdlingResource)
        }
    }

    @After
    fun unregisterIdlingResources() {
        IdlingRegistry.getInstance().apply {
            unregister(EspressoIdlingResource.countingIdlingResource)
            unregister(dataBindingIdlingResource)
        }
    }

    @Test
    fun testNavigationToReminderListScreen_ScrollTotestItem() {
        // Create a TestNavHostController
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())

        // Create a graphical FragmentScenario for the TitleScreen
        val titleScenario = launchFragmentInContainer<ReminderListFragment>( null, R.style.AppTheme)

        dataBindingIdlingResource.monitorFragment(titleScenario as FragmentScenario<Fragment>)

        titleScenario.onFragment { fragment ->

            // Set the graph on the TestNavHostController
            navController.setGraph(R.navigation.nav_graph)

            // Make the NavController available via the findNavController() APIs
            Navigation.setViewNavController(fragment.requireView(), navController)
        }


//        // MasterFragment: Attempt to scroll to the movie with the name "test".
        Espresso.onView(withId(R.id.reminderssRecyclerView)).perform(
            // scrollTo will fail the test if no item matches.
            RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                ViewMatchers.hasDescendant(ViewMatchers.withText("test"))
            ),click()
        )
    }

    private lateinit var activity: FragmentActivity
    @Test
    fun GetReminderList_testGetError() {
        val testMessage = "Test toast message"
        val toastLiveData = MutableLiveData<String>()

        // Create a TestNavHostController
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())

        // Create a graphical FragmentScenario for the TitleScreen
        val titleScenario = launchFragmentInContainer<ReminderListFragment>( null, R.style.AppTheme)


//        var activity: FragmentActivity? =null
        titleScenario.onFragment { fragment ->

//            dataBindingIdlingResource.monitorFragment(fragment)

            // Set the graph on the TestNavHostController
            navController.setGraph(R.navigation.nav_graph)

            // Make the NavController available via the findNavController() APIs
            Navigation.setViewNavController(fragment.requireView(), navController)

            fragment._viewModel.showErrorMessage.postValue(testMessage)
            fragment._viewModel.showErrorMessage.observe(fragment.viewLifecycleOwner, {
                toastLiveData.value = it
            })

            dataBindingIdlingResource.monitorFragment(titleScenario as FragmentScenario<Fragment>)
            activity = fragment.requireActivity()


        }

        // Set the LiveData value to trigger the toast message
        toastLiveData.postValue(testMessage)

        // Wait for the toast message to appear
        Espresso.onView(withText(testMessage)).inRoot(
            RootMatchers.withDecorView(CoreMatchers.not(CoreMatchers.`is`(activity.window.decorView)))
        ).check(ViewAssertions.matches(isDisplayed()))
    }
}