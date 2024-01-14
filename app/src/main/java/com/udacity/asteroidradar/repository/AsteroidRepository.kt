package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.network.AsteroidApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AsteroidRepository(private val database: AsteroidDatabase) {


    val asteroidsAll: LiveData<List<Asteroid>> =
        Transformations.map(
            database.asteroidDao.getAllAsteroids()
        ) {
            it.asDomainModel()
        }

    val asteroidsToday: LiveData<List<Asteroid>> =
        Transformations.map(
            database.asteroidDao.getAsteroidsToday(
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)
            )
        ) {
            it.asDomainModel()
        }

    val asteroidsWeek: LiveData<List<Asteroid>> =
        Transformations.map(
            database.asteroidDao.getAsteroidsRange(
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE),
                LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ISO_DATE)
            )
        ) {
            it.asDomainModel()
        }


    private val _imageOfDayUrl = MutableLiveData<PictureOfDay>()
    val imageOfDayUrl: LiveData<PictureOfDay>
        get() = _imageOfDayUrl

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val startDate = LocalDateTime.now()
                val endDate = startDate.plusDays(7)

                val startDateString = startDate.format(DateTimeFormatter.ISO_DATE)
                val endDateString = endDate.format(DateTimeFormatter.ISO_DATE)

                val jsonString =
                    AsteroidApi.asteroidService.getCurrentAsteroids(startDateString, endDateString)
                val list = parseAsteroidsJsonResult(JSONObject(jsonString))

                database.asteroidDao.insertAll(*list.asDatabaseModel())

            } catch (e: Exception) {
                Timber.e("Error: AsteroidRepo, refreshAsteroid")
            }
        }
    }

    suspend fun refreshImageOfDay() {
        try {
            val image = AsteroidApi.asteroidService.getImageOfDay()
            if (image.mediaType == "image") {
                _imageOfDayUrl.value = image
            } else {
                _imageOfDayUrl.value = null
            }
        } catch (e: Exception) {
            Timber.e("Error: AsteroidRepo, ImageOfDay")
        }
    }
}