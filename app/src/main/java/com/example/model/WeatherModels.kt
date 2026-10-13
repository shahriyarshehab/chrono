package com.example.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Open-Meteo Weather API response models
 */
@JsonClass(generateAdapter = true)
data class WeatherApiResponse(
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "timezone") val timezone: String,
    @Json(name = "current") val current: CurrentWeatherDto?,
    @Json(name = "hourly") val hourly: HourlyWeatherDto?,
    @Json(name = "daily") val daily: DailyWeatherDto?
)

@JsonClass(generateAdapter = true)
data class CurrentWeatherDto(
    @Json(name = "time") val time: String,
    @Json(name = "temperature_2m") val temperature: Double,
    @Json(name = "relative_humidity_2m") val humidity: Double?,
    @Json(name = "apparent_temperature") val apparentTemperature: Double?,
    @Json(name = "is_day") val isDay: Int?,
    @Json(name = "precipitation") val precipitation: Double?,
    @Json(name = "weather_code") val weatherCode: Int,
    @Json(name = "wind_speed_10m") val windSpeed: Double?
)

@JsonClass(generateAdapter = true)
data class HourlyWeatherDto(
    @Json(name = "time") val time: List<String>,
    @Json(name = "temperature_2m") val temperature: List<Double>,
    @Json(name = "weather_code") val weatherCode: List<Int>,
    @Json(name = "precipitation_probability") val precipitationProbability: List<Int>?
)

@JsonClass(generateAdapter = true)
data class DailyWeatherDto(
    @Json(name = "time") val time: List<String>,
    @Json(name = "weather_code") val weatherCode: List<Int>,
    @Json(name = "temperature_2m_max") val temperatureMax: List<Double>,
    @Json(name = "temperature_2m_min") val temperatureMin: List<Double>,
    @Json(name = "sunrise") val sunrise: List<String>?,
    @Json(name = "sunset") val sunset: List<String>?,
    @Json(name = "uv_index_max") val uvIndexMax: List<Double>?
)

/**
 * Open-Meteo Geocoding API response
 */
@JsonClass(generateAdapter = true)
data class GeocodingResponse(
    @Json(name = "results") val results: List<GeocodingResultDto>?
)

@JsonClass(generateAdapter = true)
data class GeocodingResultDto(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "country") val country: String?,
    @Json(name = "admin1") val admin1: String?
)

/**
 * Domain Weather Models used throughout UI and Widget
 */
data class CurrentWeather(
    val temperatureC: Double,
    val feelsLikeC: Double,
    val humidityPercent: Int,
    val windSpeedKmh: Double,
    val weatherCode: Int,
    val isDay: Boolean,
    val precipitationMm: Double,
    val highTempC: Double,
    val lowTempC: Double,
    val uvIndex: Double,
    val conditionDescription: String,
    val conditionEmoji: String,
    val conditionCategory: WeatherCategory
)

data class HourlyForecast(
    val timeLabel: String,
    val tempC: Double,
    val weatherCode: Int,
    val emoji: String,
    val precipProb: Int
)

data class DailyForecast(
    val dayLabel: String,
    val maxTempC: Double,
    val minTempC: Double,
    val weatherCode: Int,
    val emoji: String,
    val conditionDesc: String
)

data class CityLocation(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
)

enum class WeatherCategory {
    SUNNY,
    PARTLY_CLOUDY,
    CLOUDY,
    FOG,
    DRIZZLE,
    RAIN,
    SNOW,
    THUNDERSTORM
}

object WeatherCodeHelper {
    fun parseCondition(code: Int, isDay: Boolean = true): WeatherConditionInfo {
        return when (code) {
            0 -> if (isDay) WeatherConditionInfo("Clear Sky", "☀️", WeatherCategory.SUNNY)
                 else WeatherConditionInfo("Clear Night", "🌙", WeatherCategory.SUNNY)
            1, 2 -> if (isDay) WeatherConditionInfo("Partly Cloudy", "⛅", WeatherCategory.PARTLY_CLOUDY)
                    else WeatherConditionInfo("Partly Cloudy", "☁️", WeatherCategory.PARTLY_CLOUDY)
            3 -> WeatherConditionInfo("Overcast", "☁️", WeatherCategory.CLOUDY)
            45, 48 -> WeatherConditionInfo("Foggy Mist", "🌫️", WeatherCategory.FOG)
            51, 53, 55 -> WeatherConditionInfo("Drizzle", "🌦️", WeatherCategory.DRIZZLE)
            61, 63, 65 -> WeatherConditionInfo("Rain Showers", "🌧️", WeatherCategory.RAIN)
            71, 73, 75 -> WeatherConditionInfo("Snow Fall", "🌨️", WeatherCategory.SNOW)
            77 -> WeatherConditionInfo("Snow Grains", "❄️", WeatherCategory.SNOW)
            80, 81, 82 -> WeatherConditionInfo("Heavy Rain", "🌧️", WeatherCategory.RAIN)
            85, 86 -> WeatherConditionInfo("Snow Showers", "🌨️", WeatherCategory.SNOW)
            95, 96, 99 -> WeatherConditionInfo("Thunderstorm", "⛈️", WeatherCategory.THUNDERSTORM)
            else -> WeatherConditionInfo("Fair Weather", "🌤️", WeatherCategory.PARTLY_CLOUDY)
        }
    }
}

data class WeatherConditionInfo(
    val description: String,
    val emoji: String,
    val category: WeatherCategory
)
