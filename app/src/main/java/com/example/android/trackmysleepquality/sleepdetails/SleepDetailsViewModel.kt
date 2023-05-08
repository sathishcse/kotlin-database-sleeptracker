package com.example.android.trackmysleepquality.sleepdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import kotlinx.coroutines.*

class SleepDetailsViewModel(
    private val sleepNightKey: Long,
    private val dataSource: SleepDatabaseDao
) :
    ViewModel() {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private var _sleepNightItem = MutableLiveData<SleepNight>()
    val sleepNightItem: LiveData<SleepNight>
        get() = _sleepNightItem

    init {
        getNight(sleepNightKey)
    }

    private fun getNight(nightId: Long) {
        uiScope.launch {
            var night: SleepNight?
            withContext(Dispatchers.IO) {
                night = dataSource.get(nightId)
            }
            _sleepNightItem.value = night
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}