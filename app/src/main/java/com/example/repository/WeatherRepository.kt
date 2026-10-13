package com.example.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.BuildConfig
import com.example.model.CityLocation
import com.example.model.CurrentWeather
import com.example.model.DailyForecast
import com.example.model.HourlyForecast
import com.example.model.WeatherCategory
import com.example.model.WeatherCodeHelper
import com.example.network.NetworkClient
import com.example.network.OpenWeatherMapClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("chronos_weather_prefs", Context.MODE_PRIVATE)

    companion object {
        val DEFAULT_CITIES = listOf(
            CityLocation("San Francisco", "United States", 37.7749, -122.4194),
            CityLocation("New York", "United States", 40.7128, -74.0060),
            CityLocation("London", "United Kingdom", 51.5074, -0.1278),
            CityLocation("Tokyo", "Japan", 35.6762, 139.6503),
            CityLocation("Paris", "France", 48.8566, 2.3522),
            CityLocation("Sydney", "Australia", -33.8688, 151.2093),
            CityLocation("Dubai", "United Arab Emirates", 25.2048, 55.2708),
            CityLocation("Singapore", "Singapore", 1.3521, 103.8198)
        )
    }

    fun isOwmConfigured(): Boolean {
        val key = try { BuildConfig.OPENWEATHERMAP_API_KEY } catch (e: Exception) { "" }
        return key.isNotBlank() && key != "YOUR_OPENWEATHERMAP_API_KEY"
    }

    fun getSavedCity(): CityLocation {
        val name = prefs.getString("city_name", DEFAULT_CITIES[0].name) ?: DEFAULT_CITIES[0].name
        val country = prefs.getString("city_country", DEFAULT_CITIES[0].country) ?: DEFAULT_CITIES[0].country
        val lat = prefs.getFloat("city_lat", DEFAULT_CITIES[0].latitude.toFloat()).toDouble()
        val lon = prefs.getFloat("city_lon", DEFAULT_CITIES[0].longitude.toFloat()).toDouble()
        return CityLocation(name, country, lat, lon)
    }

    fun saveCity(city: CityLocation) {
        prefs.edit()
            .putString("city_name", city.name)
            .putString("city_country", city.country)
            .putFloat("city_lat", city.latitude.toFloat())
            .putFloat("city_lon", city.longitude.toFloat())
            .apply()
    }

    fun getCachedWeather(): CurrentWeather {
        val temp = prefs.getFloat("cached_temp", 21.5f).toDouble()
        val feelsLike = prefs.getFloat("cached_feels_like", 21.0f).toDouble()
        val humidity = prefs.getInt("cached_humidity", 58)
        val wind = prefs.getFloat("cached_wind", 12.0f).toDouble()
        val code = prefs.getInt("cached_code", 1)
        val isDay = prefs.getBoolean("cached_is_day", true)
        val precip = prefs.getFloat("cached_precip", 0.0f).toDouble()
        val high = prefs.getFloat("cached_high", 24.0f).toDouble()
        val low = prefs.getFloat("cached_low", 15.0f).toDouble()
        val uv = prefs.getFloat("cached_uv", 5.2f).toDouble()

        val condition = WeatherCodeHelper.parseCondition(code, isDay)
        return CurrentWeather(
            temperatureC = temp,
            feelsLikeC = feelsLike,
            humidityPercent = humidity,
            windSpeedKmh = wind,
            weatherCode = code,
            isDay = isDay,
            precipitationMm = precip,
            highTempC = high,
            lowTempC = low,
            uvIndex = uv,
            conditionDescription = condition.description,
            conditionEmoji = condition.emoji,
            conditionCategory = condition.category
        )
    }

    private fun cacheWeather(weather: CurrentWeather) {
        prefs.edit()
            .putFloat("cached_temp", weather.temperatureC.toFloat())
            .putFloat("cached_feels_like", weather.feelsLikeC.toFloat())
            .putInt("cached_humidity", weather.humidityPercent)
            .putFloat("cached_wind", weather.windSpeedKmh.toFloat())
            .putInt("cached_code", weather.weatherCode)
            .putBoolean("cached_is_day", weather.isDay)
            .putFloat("cached_precip", weather.precipitationMm.toFloat())
            .putFloat("cached_high", weather.highTempC.toFloat())
            .putFloat("cached_low", weather.lowTempC.toFloat())
            .putFloat("cached_uv", weather.uvIndex.toFloat())
            .putLong("cached_timestamp", System.currentTimeMillis())
            .apply()
    }

    suspend fun fetchWeather(
        latitude: Double,
        longitude: Double
    ): Pair<CurrentWeather, Pair<List<HourlyForecast>, List<DailyForecast>>> {
        return withContext(Dispatchers.IO) {
            val key = try { BuildConfig.OPENWEATHERMAP_API_KEY } catch (e: Exception) { "" }
            if (key.isNotBlank() && key != "YOUR_OPENWEATHERMAP_API_KEY") {
                try {
                    return@withContext fetchFromOpenWeatherMap(latitude, longitude, key)
                } catch (e: Exception) {
                    Log.w("WeatherRepository", "OpenWeatherMap call failed, falling back to Open-Meteo", e)
                }
            }
            // Fallback or default provider
            fetchFromOpenMeteo(latitude, longitude)
        }
    }

    private suspend fun fetchFromOpenWeatherMap(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): Pair<CurrentWeather, Pair<List<HourlyForecast>, List<DailyForecast>>> {
        val currentRes = OpenWeatherMapClient.api.getCurrentWeather(latitude, longitude, apiKey)
        val forecastRes = OpenWeatherMapClient.api.getForecast(latitude, longitude, apiKey)

        val main = currentRes.main
        val weatherItem = currentRes.weather?.firstOrNull()
        val weatherId = weatherItem?.id ?: 800

        val nowSec = System.currentTimeMillis() / 1000
        val sunrise = currentRes.sys?.sunrise ?: (nowSec - 21600)
        val sunset = currentRes.sys?.sunset ?: (nowSec + 21600)
        val isDay = nowSec in sunrise..sunset

        val conditionInfo = OpenWeatherMapClient.parseOwmCondition(weatherId, isDay)
        val temp = main?.temp ?: 20.0
        val feelsLike = main?.feelsLike ?: temp
        val humidity = main?.humidity ?: 50
        val windSpeedKmh = (currentRes.wind?.speed ?: 3.5) * 3.6 // m/s to km/h

        val tempMax = main?.tempMax ?: (temp + 3.0)
        val tempMin = main?.tempMin ?: (temp - 3.0)

        val currentWeather = CurrentWeather(
            temperatureC = temp,
            feelsLikeC = feelsLike,
            humidityPercent = humidity,
            windSpeedKmh = windSpeedKmh,
            weatherCode = weatherId,
            isDay = isDay,
            precipitationMm = 0.0,
            highTempC = tempMax,
            lowTempC = tempMin,
            uvIndex = 5.0,
            conditionDescription = weatherItem?.description?.replaceFirstChar { it.uppercase() } ?: conditionInfo.description,
            conditionEmoji = conditionInfo.emoji,
            conditionCategory = conditionInfo.category
        )

        cacheWeather(currentWeather)

        // Parse hourly from forecast (3-hour slots)
        val hourlyList = mutableListOf<HourlyForecast>()
        val timeFormat = SimpleDateFormat("h a", Locale.getDefault())
        forecastRes.list?.take(8)?.forEach { item ->
            val itemDate = Date(item.dt * 1000)
            val label = timeFormat.format(itemDate)
            val itemWeather = item.weather?.firstOrNull()
            val itemCond = OpenWeatherMapClient.parseOwmCondition(itemWeather?.id ?: 800, true)
            val precipProb = ((item.pop ?: 0.0) * 100).toInt()
            hourlyList.add(
                HourlyForecast(
                    timeLabel = label,
                    tempC = item.main.temp,
                    weatherCode = itemWeather?.id ?: 800,
                    emoji = itemCond.emoji,
                    precipProb = precipProb
                )
            )
        }

        // Parse 5-day daily forecast
        val dailyMap = mutableMapOf<String, MutableList<com.example.model.OwmForecastItem>>()
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val todayStr = dayFormat.format(Date())

        forecastRes.list?.forEach { item ->
            val dayName = dayFormat.format(Date(item.dt * 1000))
            dailyMap.getOrPut(dayName) { mutableListOf() }.add(item)
        }

        val dailyList = mutableListOf<DailyForecast>()
        dailyMap.entries.take(5).forEach { (dayName, items) ->
            val maxT = items.maxOfOrNull { it.main.tempMax ?: it.main.temp } ?: temp
            val minT = items.minOfOrNull { it.main.tempMin ?: it.main.temp } ?: temp
            val repWeather = items.firstOrNull()?.weather?.firstOrNull()
            val repCond = OpenWeatherMapClient.parseOwmCondition(repWeather?.id ?: 800, true)
            val label = if (dayName == todayStr) "Today" else dayName
            dailyList.add(
                DailyForecast(
                    dayLabel = label,
                    maxTempC = maxT,
                    minTempC = minT,
                    weatherCode = repWeather?.id ?: 800,
                    emoji = repCond.emoji,
                    conditionDesc = repWeather?.description?.replaceFirstChar { it.uppercase() } ?: repCond.description
                )
            )
        }

        return Pair(currentWeather, Pair(hourlyList, dailyList))
    }

    private suspend fun fetchFromOpenMeteo(
        latitude: Double,
        longitude: Double
    ): Pair<CurrentWeather, Pair<List<HourlyForecast>, List<DailyForecast>>> {
        try {
            val res = NetworkClient.weatherApi.getForecast(latitude, longitude)
            val cur = res.current
            val daily = res.daily
            val hourly = res.hourly

            val temp = cur?.temperature ?: 21.0
            val feelsLike = cur?.apparentTemperature ?: temp
            val humidity = cur?.humidity?.toInt() ?: 60
            val wind = cur?.windSpeed ?: 10.0
            val code = cur?.weatherCode ?: 1
            val isDay = (cur?.isDay ?: 1) == 1
            val precip = cur?.precipitation ?: 0.0

            val high = daily?.temperatureMax?.firstOrNull() ?: (temp + 3.0)
            val low = daily?.temperatureMin?.firstOrNull() ?: (temp - 4.0)
            val uv = daily?.uvIndexMax?.firstOrNull() ?: 4.5

            val condition = WeatherCodeHelper.parseCondition(code, isDay)
            val currentWeather = CurrentWeather(
                temperatureC = temp,
                feelsLikeC = feelsLike,
                humidityPercent = humidity,
                windSpeedKmh = wind,
                weatherCode = code,
                isDay = isDay,
                precipitationMm = precip,
                highTempC = high,
                lowTempC = low,
                uvIndex = uv,
                conditionDescription = condition.description,
                conditionEmoji = condition.emoji,
                conditionCategory = condition.category
            )

            cacheWeather(currentWeather)

            val hourlyList = mutableListOf<HourlyForecast>()
            if (hourly != null && hourly.time.isNotEmpty()) {
                val count = minOf(12, hourly.time.size)
                for (i in 0 until count) {
                    val timeStr = hourly.time[i]
                    val hourLabel = try {
                        timeStr.substringAfter("T").take(5)
                    } catch (e: Exception) {
                        "$i:00"
                    }
                    val hTemp = hourly.temperature.getOrElse(i) { temp }
                    val hCode = hourly.weatherCode.getOrElse(i) { code }
                    val hCond = WeatherCodeHelper.parseCondition(hCode, true)
                    val hPrecip = hourly.precipitationProbability?.getOrNull(i) ?: 0
                    hourlyList.add(HourlyForecast(hourLabel, hTemp, hCode, hCond.emoji, hPrecip))
                }
            }

            val dailyList = mutableListOf<DailyForecast>()
            if (daily != null && daily.time.isNotEmpty()) {
                val count = minOf(5, daily.time.size)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
                for (i in 0 until count) {
                    val dateStr = daily.time[i]
                    val dayLabel = try {
                        val parsedDate = sdf.parse(dateStr)
                        if (i == 0) "Today" else dayFormat.format(parsedDate ?: Date())
                    } catch (e: Exception) {
                        "Day $i"
                    }
                    val dHigh = daily.temperatureMax.getOrElse(i) { high }
                    val dLow = daily.temperatureMin.getOrElse(i) { low }
                    val dCode = daily.weatherCode.getOrElse(i) { code }
                    val dCond = WeatherCodeHelper.parseCondition(dCode, true)
                    dailyList.add(DailyForecast(dayLabel, dHigh, dLow, dCode, dCond.emoji, dCond.description))
                }
            }

            return Pair(currentWeather, Pair(hourlyList, dailyList))
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Failed to fetch weather, using cache", e)
            val cached = getCachedWeather()
            val mockHourly = listOf(
                HourlyForecast("Now", cached.temperatureC, cached.weatherCode, cached.conditionEmoji, 10),
                HourlyForecast("+1h", cached.temperatureC + 0.5, cached.weatherCode, cached.conditionEmoji, 10),
                HourlyForecast("+2h", cached.temperatureC + 1.2, cached.weatherCode, cached.conditionEmoji, 15),
                HourlyForecast("+3h", cached.temperatureC + 0.8, cached.weatherCode, cached.conditionEmoji, 20),
                HourlyForecast("+4h", cached.temperatureC - 0.5, cached.weatherCode, cached.conditionEmoji, 10)
            )
            val mockDaily = listOf(
                DailyForecast("Today", cached.highTempC, cached.lowTempC, cached.weatherCode, cached.conditionEmoji, cached.conditionDescription),
                DailyForecast("Tomorrow", cached.highTempC + 1.0, cached.lowTempC + 0.5, 1, "☀️", "Mostly Sunny"),
                DailyForecast("Wed", cached.highTempC - 1.5, cached.lowTempC - 1.0, 2, "⛅", "Partly Cloudy"),
                DailyForecast("Thu", cached.highTempC - 2.0, cached.lowTempC - 2.5, 61, "🌧️", "Showers"),
                DailyForecast("Fri", cached.highTempC + 0.5, cached.lowTempC, 0, "☀️", "Clear Sky")
            )
            return Pair(cached, Pair(mockHourly, mockDaily))
        }
    }

    suspend fun reverseGeocodeLocation(latitude: Double, longitude: Double): CityLocation {
        return withContext(Dispatchers.IO) {
            val key = try { BuildConfig.OPENWEATHERMAP_API_KEY } catch (e: Exception) { "" }
            if (key.isNotBlank() && key != "YOUR_OPENWEATHERMAP_API_KEY") {
                try {
                    val res = OpenWeatherMapClient.api.reverseGeocode(latitude, longitude, 1, key)
                    val first = res.firstOrNull()
                    if (first != null) {
                        val countryStr = listOfNotNull(first.state, first.country).joinToString(", ")
                        return@withContext CityLocation(first.name, countryStr, latitude, longitude)
                    }
                } catch (e: Exception) {
                    Log.w("WeatherRepository", "OWM reverse geocode failed", e)
                }
            }

            // Fallback reverse geocoding representation
            CityLocation("Current Location", String.format(Locale.US, "%.2f°, %.2f°", latitude, longitude), latitude, longitude)
        }
    }

    suspend fun searchCities(query: String): List<CityLocation> {
        return withContext(Dispatchers.IO) {
            val key = try { BuildConfig.OPENWEATHERMAP_API_KEY } catch (e: Exception) { "" }
            if (key.isNotBlank() && key != "YOUR_OPENWEATHERMAP_API_KEY") {
                try {
                    val owmResults = OpenWeatherMapClient.api.directGeocode(query.trim(), 5, key)
                    if (owmResults.isNotEmpty()) {
                        return@withContext owmResults.map {
                            val stateCountry = listOfNotNull(it.state, it.country).joinToString(", ")
                            CityLocation(it.name, stateCountry, it.lat, it.lon)
                        }
                    }
                } catch (e: Exception) {
                    Log.w("WeatherRepository", "OWM geocode search failed, fallback to Open-Meteo", e)
                }
            }

            try {
                if (query.isBlank()) return@withContext emptyList()
                val res = NetworkClient.geocodingApi.searchCity(query.trim())
                res.results?.map {
                    val countryText = if (!it.admin1.isNullOrBlank()) "${it.admin1}, ${it.country ?: ""}" else (it.country ?: "")
                    CityLocation(it.name, countryText, it.latitude, it.longitude)
                } ?: emptyList()
            } catch (e: Exception) {
                Log.e("WeatherRepository", "Failed to search cities", e)
                emptyList()
            }
        }
    }
}
