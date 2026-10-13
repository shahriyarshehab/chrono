package com.example.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OwmCurrentWeatherResponse(
    @Json(name = "coord") val coord: OwmCoord?,
    @Json(name = "weather") val weather: List<OwmWeatherItem>?,
    @Json(name = "main") val main: OwmMain?,
    @Json(name = "wind") val wind: OwmWind?,
    @Json(name = "clouds") val clouds: OwmClouds?,
    @Json(name = "sys") val sys: OwmSys?,
    @Json(name = "name") val name: String?,
    @Json(name = "dt") val dt: Long?
)

@JsonClass(generateAdapter = true)
data class OwmCoord(
    @Json(name = "lon") val lon: Double,
    @Json(name = "lat") val lat: Double
)

@JsonClass(generateAdapter = true)
data class OwmWeatherItem(
    @Json(name = "id") val id: Int,
    @Json(name = "main") val main: String,
    @Json(name = "description") val description: String,
    @Json(name = "icon") val icon: String
)

@JsonClass(generateAdapter = true)
data class OwmMain(
    @Json(name = "temp") val temp: Double,
    @Json(name = "feels_like") val feelsLike: Double,
    @Json(name = "temp_min") val tempMin: Double?,
    @Json(name = "temp_max") val tempMax: Double?,
    @Json(name = "pressure") val pressure: Int?,
    @Json(name = "humidity") val humidity: Int
)

@JsonClass(generateAdapter = true)
data class OwmWind(
    @Json(name = "speed") val speed: Double,
    @Json(name = "deg") val deg: Int?
)

@JsonClass(generateAdapter = true)
data class OwmClouds(
    @Json(name = "all") val all: Int?
)

@JsonClass(generateAdapter = true)
data class OwmSys(
    @Json(name = "country") val country: String?,
    @Json(name = "sunrise") val sunrise: Long?,
    @Json(name = "sunset") val sunset: Long?
)

@JsonClass(generateAdapter = true)
data class OwmForecastResponse(
    @Json(name = "list") val list: List<OwmForecastItem>?,
    @Json(name = "city") val city: OwmCity?
)

@JsonClass(generateAdapter = true)
data class OwmForecastItem(
    @Json(name = "dt") val dt: Long,
    @Json(name = "main") val main: OwmMain,
    @Json(name = "weather") val weather: List<OwmWeatherItem>?,
    @Json(name = "wind") val wind: OwmWind?,
    @Json(name = "pop") val pop: Double?,
    @Json(name = "dt_txt") val dtTxt: String?
)

@JsonClass(generateAdapter = true)
data class OwmCity(
    @Json(name = "id") val id: Long?,
    @Json(name = "name") val name: String?,
    @Json(name = "country") val country: String?,
    @Json(name = "coord") val coord: OwmCoord?,
    @Json(name = "sunrise") val sunrise: Long?,
    @Json(name = "sunset") val sunset: Long?
)

@JsonClass(generateAdapter = true)
data class OwmGeocodingItem(
    @Json(name = "name") val name: String,
    @Json(name = "lat") val lat: Double,
    @Json(name = "lon") val lon: Double,
    @Json(name = "country") val country: String?,
    @Json(name = "state") val state: String?
)
