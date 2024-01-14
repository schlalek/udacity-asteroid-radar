package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

enum class AsteroidFilter {
    ALL, TODAY, WEEK
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    val repo = AsteroidRepository(database)

    var listAsteroid = MediatorLiveData<List<Asteroid>>()
    val imageOfDay = repo.imageOfDayUrl

    init {
        listAsteroid.addSource(repo.asteroidsAll) { listAsteroid.value = it }
        viewModelScope.launch {
            repo.refreshAsteroids()
            repo.refreshImageOfDay()
        }
    }


    private val _navigateDetail = MutableLiveData<Asteroid>()
    val navigateDetail: LiveData<Asteroid>
        get() = _navigateDetail

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateDetail.value = asteroid
    }

    fun onNavigateDetailComplete() {
        _navigateDetail.value = null
    }

    fun updateFilter(asteroidFilter: AsteroidFilter) {
        listAsteroid.removeSource(repo.asteroidsAll)
        listAsteroid.removeSource(repo.asteroidsToday)
        listAsteroid.removeSource(repo.asteroidsWeek)
        listAsteroid.addSource(
            when (asteroidFilter) {
                AsteroidFilter.ALL -> repo.asteroidsAll
                AsteroidFilter.TODAY -> repo.asteroidsToday
                AsteroidFilter.WEEK -> repo.asteroidsWeek
            }
        ) { listAsteroid.value = it }


    }

}