package com.udacity.asteroidradar.network

import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AsteroidService {
    @GET("neo/rest/v1/feed")
    suspend fun getCurrentAsteroids(@Query("start_date") start: String, @Query("end_date") end: String, @Query("api_key") apiKey: String = API_KEY): String

    @GET("planetary/apod")
    suspend fun getImageOfDay(@Query("api_key") apiKey: String = API_KEY)
}

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

object AsteroidApi {
    val asteroidService : AsteroidService by lazy {
        retrofit.create(AsteroidService::class.java)
    }
}