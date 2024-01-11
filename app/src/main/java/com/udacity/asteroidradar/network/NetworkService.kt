package com.udacity.asteroidradar.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.PictureOfDay
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AsteroidService {
    @GET("neo/rest/v1/feed")
    suspend fun getCurrentAsteroids(
        @Query("start_date") start: String,
        @Query("end_date") end: String,
        @Query("api_key") apiKey: String = API_KEY
    ): String

    @GET("planetary/apod")
    suspend fun getImageOfDay(@Query("api_key") apiKey: String = API_KEY): PictureOfDay
}

object AsteroidApi {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()

    val asteroidService: AsteroidService by lazy {
        retrofit.create(AsteroidService::class.java)
    }
}