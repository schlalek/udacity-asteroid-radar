package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    init {
        viewModelScope.launch {
            repo.refreshAsteroids()
            repo.refreshImageOfDay()
        }
    }

    private val database = getDatabase(application)

    val repo = AsteroidRepository(database)

    private val _imageOfDay = MutableLiveData<String>()
    val imageOfDay: LiveData<String>
        get() = _imageOfDay

}