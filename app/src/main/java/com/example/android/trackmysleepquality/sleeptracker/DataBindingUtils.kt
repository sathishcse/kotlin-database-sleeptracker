package com.example.android.trackmysleepquality.sleeptracker

import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertLongToDateString
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight

@BindingAdapter("sleepTime")
fun AppCompatTextView.setDay(sleep: SleepNight?) {
    text = sleep?.let {
        convertLongToDateString(sleep.startSleepTime) +" "+convertLongToDateString(sleep.stopSleepTime)
    }
}

@BindingAdapter("sleepQuality")
fun AppCompatTextView.setSleepQuality(sleep: SleepNight?) {
    text = sleep?.let {
        convertNumericQualityToString(sleep.qualityOfSleep, resources)
    }
}


@BindingAdapter("sleepQualityImage")
fun AppCompatImageView.setSleepQualityImg(sleep: SleepNight?) {
    setImageResource(
        when (sleep?.qualityOfSleep) {
            0 -> R.drawable.ic_sleep_0
            1 -> R.drawable.ic_sleep_1
            2 -> R.drawable.ic_sleep_2
            3 -> R.drawable.ic_sleep_3
            4 -> R.drawable.ic_sleep_4
            5 -> R.drawable.ic_sleep_5
            else -> R.drawable.ic_sleep_0
        }
    )
}