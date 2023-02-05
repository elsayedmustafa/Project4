package com.udacity.project4.locationreminders.welcomreminderapp

import android.app.Activity
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentWelcomeReminderAppBinding
import com.udacity.project4.locationreminders.reminderslist.RemindersListAdapter
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import com.udacity.project4.utils.setTitle
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class WelcomeReminderAppFragment : BaseFragment() {
    //use Koin to retrieve the ViewModel instance
    override val _viewModel: WelcomeRemindersViewModel by viewModel()
    private lateinit var binding: FragmentWelcomeReminderAppBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                com.udacity.project4.R.layout.fragment_welcome_reminder_app, container, false
            )
        binding.viewModel = _viewModel

//        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(com.udacity.project4.R.string.app_name))

//        binding.refreshLayout.setOnRefreshListener { _viewModel.loadReminders() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        setupRecyclerView()
        binding.btnLogin.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser != null) {
                // already signed in
                navigateToReminderList()
            } else {
                // not signed in
                navigateToLoginReminder()

            }
        }
    }

    override fun onResume() {
        super.onResume()
        //load the reminders list on the ui
//        _viewModel.loadReminders()
    }

    private fun navigateToReminderList() {
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                WelcomeReminderAppFragmentDirections.toLoginReminder()
            ))
    }
    private fun navigateToLoginReminder() {
        //use the navigationCommand live data to navigate between the fragments
//        _viewModel.navigationCommand.postValue(
//            NavigationCommand.To(
//                WelcomeReminderAppFragmentDirections.toLoginReminder()
//            )
//        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder() // ... options ...
            .setLogo(R.drawable.map)
            .setAvailableProviders(
                Arrays.asList(
                    GoogleBuilder().build(),
//                    FacebookBuilder().build(),
//                    TwitterBuilder().build(),
//                    MicrosoftBuilder().build(),
//                    YahooBuilder().build(),
//                    AppleBuilder().build(),
                    EmailBuilder().build(),
//                    PhoneBuilder().build(),
//                    AnonymousBuilder().build()
                )
            )
            .build()

        signInLauncher.launch(signInIntent)
    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result: FirebaseAuthUIAuthenticationResult? ->
        val response: IdpResponse? = result!!.idpResponse

        if (result.resultCode === Activity.RESULT_OK) {
            // Successfully signed in
            _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                WelcomeReminderAppFragmentDirections.toLoginReminder()
            )
        )
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                _viewModel.showErrorMessage.postValue(getString(com.udacity.project4.R.string.sign_in_canceled))
            }
            if (response!!.getError()!!.getErrorCode() === ErrorCodes.NO_NETWORK) {
                _viewModel.showErrorMessage.postValue(getString(com.udacity.project4.R.string.no_internet_connection))

            }
            _viewModel.showErrorMessage.postValue(getString(com.udacity.project4.R.string.unknown_error))

            Log.e(TAG, "Sign-in error: ", response!!.getError())
            Log.e(TAG, "Sign-in Code error: "+result.resultCode)

        }
    }

    private fun setupRecyclerView() {
        val adapter = RemindersListAdapter {
        }

//        setup the recycler view using the extension function
//        binding.reminderssRecyclerView.setup(adapter)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.logout -> {
////                TODO: add the logout implementation
//            }
//        }
//        return super.onOptionsItemSelected(item)
//
//    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
////        display logout as menu item
//        inflater.inflate(R.menu.main_menu, menu)
//    }

}
