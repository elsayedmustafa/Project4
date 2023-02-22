package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions
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

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

//    TODO: test the navigation of the fragments.
//    TODO: test the displayed data on the UI.
//    TODO: add testing for the error messages.

    private lateinit var appContext: Application

    private val dataBindingIdlingResource = DataBindingIdlingResource()

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
//    import androidx.test.core.app.ActivityScenario
//    val scenario = ActivityScenario.launch(MainActivity::class.java)
//    dataBindingIdlingResource.monitorActivity(scenario)


    /*@Test
    fun clickTask_navigateToDetailFragmentOne() = runBlockingTest {
//        repository.saveTask(Task("TITLE1", "DESCRIPTION1", false, "id1"))
//        repository.saveTask(Task("TITLE2", "DESCRIPTION2", true, "id2"))

        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        val navController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        // WHEN - Click on the first list item
        onView(withId(R.id.reminderssRecyclerView))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                hasDescendant(withText("TITLE1")), click()))

        // THEN - Verify that we navigate to the first detail screen
        verify(navController).navigate(
            TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment( "id1")
        )
    }*/

    @Test
    fun testNavigationToReminderListScreen_ScrollTotestItem() {
        // Create a TestNavHostController
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())

        // Create a graphical FragmentScenario for the TitleScreen
        val titleScenario = launchFragmentInContainer<ReminderListFragment>( null, R.style.AppTheme)

        dataBindingIdlingResource.monitorFragment(titleScenario as FragmentScenario<Fragment>)

        titleScenario.onFragment { fragment ->

//            dataBindingIdlingResource.monitorFragment(fragment)

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


    @Test
    fun GetReminderList_testGetError() {
        // Create a TestNavHostController
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())

        // Create a graphical FragmentScenario for the TitleScreen
        val titleScenario = launchFragmentInContainer<ReminderListFragment>( null, R.style.AppTheme)

        dataBindingIdlingResource.monitorFragment(titleScenario as FragmentScenario<Fragment>)

        titleScenario.onFragment { fragment ->

//            dataBindingIdlingResource.monitorFragment(fragment)

            // Set the graph on the TestNavHostController
            navController.setGraph(R.navigation.nav_graph)

            // Make the NavController available via the findNavController() APIs
            Navigation.setViewNavController(fragment.requireView(), navController)
        }



    }
}