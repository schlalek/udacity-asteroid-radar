package com.udacity.asteroidradar

import com.squareup.moshi.Json

data class PictureOfDay(
    @Json(name = "media_type") var mediaType: String, val title: String,
    val url: String
)