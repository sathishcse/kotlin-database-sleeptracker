/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
    val database: SleepDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var getToNight = MutableLiveData<SleepNight?>()
    private var _navigateSleepQuality = MutableLiveData<SleepNight?>()
    val navigateSleepQuality: LiveData<SleepNight?>
        get() = _navigateSleepQuality

    fun doneNavigateToSleepQuality() {
        _navigateSleepQuality.value = null
    }
    //SleepDetails
    private var _navigateToSleepDetails = MutableLiveData<Long>()
    val navigateToSleepDetails: LiveData<Long>
        get() = _navigateToSleepDetails

    fun updateToSleepDetails(sleepNight:Long){
        _navigateToSleepDetails.value = sleepNight
    }

    fun doneNavigateToSleepDetails() {
        _navigateToSleepDetails.value = null
    }

    private var _nights = MutableLiveData<List<SleepNight>>()
    val nights : LiveData<List<SleepNight>?>
    get() = database.getAllNight()


    /*val nightString = Transformations.map(nights){ nights ->
        formatNights(nights,application.resources)
    }*/

    val startButtonVisibility = Transformations.map(getToNight) {
        null == it
    }

    val stopButtonVisibility = Transformations.map(getToNight) {
        null != it
    }

    val clearButtonVisibility = Transformations.map(nights) {
        it?.isNotEmpty()
    }

    private var _showSnackbarMessage = MutableLiveData<Boolean>()
    val showSnackbarMessage: LiveData<Boolean>
        get() = _showSnackbarMessage

    fun doneShowSnackbarMessage() {
        _showSnackbarMessage.value = null
    }

    init {
        initializeNit()
    }

    private fun initializeNit() {
        uiScope.launch {
            getToNight.value = getToNightFromDatabase()
        }
    }

    private suspend fun getToNightFromDatabase(): SleepNight? {
        return withContext(Dispatchers.IO) {
            var toNit = database.getToNight()
            if (toNit?.startSleepTime != toNit?.stopSleepTime) {
                toNit = null
            }
            toNit
        }
    }

    fun startTracking() {
        uiScope.launch {
            val newNit = SleepNight()
            insertNight(newNit)
            getToNight.value = getToNightFromDatabase()
        }
    }

    private suspend fun insertNight(newNit: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insert(newNit)
        }
    }

    fun stopTracking() {
        uiScope.launch {
            val oldNit = getToNight.value ?: return@launch
            oldNit.stopSleepTime = System.currentTimeMillis()
            updateNight(oldNit)
            _navigateSleepQuality.value = oldNit
        }
    }

    private suspend fun updateNight(oldNit: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(oldNit)
        }
    }

    fun clear() {
        uiScope.launch {
            deleteAllRecord()
            _showSnackbarMessage.value = true
            getToNight.value = null
        }
    }

    private suspend fun deleteAllRecord() {
        withContext(Dispatchers.IO) {
            database.clearAll()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


}

