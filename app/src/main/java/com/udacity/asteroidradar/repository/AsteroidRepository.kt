package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.network.AsteroidApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AsteroidRepository(private val database: AsteroidDatabase) {
    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    private val _imageOfDayUrl = MutableLiveData<String?>()
    val imageOfDayUrl: LiveData<String?>
        get() = _imageOfDayUrl

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val startDate = LocalDateTime.now()
            val endDate = startDate.plusDays(7)

            val startDateString = startDate.format(DateTimeFormatter.ISO_DATE)
            val endDateString = endDate.format(DateTimeFormatter.ISO_DATE)

            val jsonString =
                AsteroidApi.asteroidService.getCurrentAsteroids(startDateString, endDateString)
            val list = parseAsteroidsJsonResult(JSONObject(jsonString))

            database.asteroidDao.insertAll(*list.asDatabaseModel())
        }
    }

    suspend fun refreshImageOfDay() {
        val image = AsteroidApi.asteroidService.getImageOfDay()
        if (image.mediaType == "image") {
            _imageOfDayUrl.value = image.url
        } else {
            _imageOfDayUrl.value = null
        }
    }
}