package com.example.android.trackmysleepquality.sleepdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepDetailsBinding

class SleepDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentSleepDetailsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_sleep_details, container, false)

        val sleepNight = SleepDetailsFragmentArgs.fromBundle(arguments!!)

        val context = requireNotNull(this.activity).application

        val dataSource = SleepDatabase.getInstance(context).sleepDatabaseDao

        val viewModelFactory = SleepDetailsViewModelFactory(sleepNight.sleepKey, dataSource)

        val sleepDetailsViewModel =
            ViewModelProvider(this, viewModelFactory).get(SleepDetailsViewModel::class.java)
        binding.lifecycleOwner = this
        binding.sleepDetailsViewModel = sleepDetailsViewModel
        sleepDetailsViewModel.sleepNightItem.observe(viewLifecycleOwner, Observer { it ->
            binding.sleep = it
        })

        return binding.root
    }

}