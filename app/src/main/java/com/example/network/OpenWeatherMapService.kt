package com.example.network

import com.example.model.OwmCurrentWeatherResponse
import com.example.model.OwmForecastResponse
import com.example.model.OwmGeocodingItem
import com.example.model.WeatherCategory
import com.example.model.WeatherConditionInfo
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface OpenWeatherMapApi {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String,
        @Query("units") units: String = "metric"
    ): OwmCurrentWeatherResponse

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String,
        @Query("units") units: String = "metric"
    ): OwmForecastResponse

    @GET("geo/1.0/reverse")
    suspend fun reverseGeocode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("limit") limit: Int = 1,
        @Query("appid") appid: String
    ): List<OwmGeocodingItem>

    @GET("geo/1.0/direct")
    suspend fun directGeocode(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") appid: String
    ): List<OwmGeocodingItem>
}

object OpenWeatherMapClient {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    val api: OpenWeatherMapApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OpenWeatherMapApi::class.java)
    }

    fun parseOwmCondition(weatherId: Int, isDay: Boolean = true): WeatherConditionInfo {
        return when (weatherId) {
            in 200..232 -> WeatherConditionInfo("Thunderstorm", "⛈️", WeatherCategory.THUNDERSTORM)
            in 300..321 -> WeatherConditionInfo("Drizzle", "🌦️", WeatherCategory.DRIZZLE)
            in 500..531 -> WeatherConditionInfo("Rain Showers", "🌧️", WeatherCategory.RAIN)
            in 600..622 -> WeatherConditionInfo("Snow Fall", "🌨️", WeatherCategory.SNOW)
            in 701..781 -> WeatherConditionInfo("Mist & Fog", "🌫️", WeatherCategory.FOG)
            800 -> if (isDay) WeatherConditionInfo("Clear Sky", "☀️", WeatherCategory.SUNNY)
                   else WeatherConditionInfo("Clear Night", "🌙", WeatherCategory.SUNNY)
            801, 802 -> if (isDay) WeatherConditionInfo("Partly Cloudy", "⛅", WeatherCategory.PARTLY_CLOUDY)
                        else WeatherConditionInfo("Partly Cloudy", "☁️", WeatherCategory.PARTLY_CLOUDY)
            803, 804 -> WeatherConditionInfo("Overcast", "☁️", WeatherCategory.CLOUDY)
            else -> WeatherConditionInfo("Fair Weather", "🌤️", WeatherCategory.PARTLY_CLOUDY)
        }
    }
}
