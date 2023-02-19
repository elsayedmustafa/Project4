package com.udacity.project4.locationreminders.reminderdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentReminderDetailsBinding
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import com.udacity.project4.utils.setTitle
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReminderDetailsFragment : BaseFragment() {
    //use Koin to retrieve the ViewModel instance
    override val _viewModel: ReminderDetailsViewModel by viewModel()
    private lateinit var binding: FragmentReminderDetailsBinding

    //val args: ReminderDetailsFragmentArgs


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_reminder_details, container, false
            )
        binding.viewModel = _viewModel

//        setHasOptionsMenu(true)
//        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name))

        setDisplayHomeAsUpEnabled(true)

//        binding.refreshLayout.setOnRefreshListener {
        val reminderKey = requireArguments().getString("reminderKey")
            _viewModel.GetReminder(reminderKey!!)
//        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this

    }

    override fun onResume() {
        super.onResume()
    }


}
