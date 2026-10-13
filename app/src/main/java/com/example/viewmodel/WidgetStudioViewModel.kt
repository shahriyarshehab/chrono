package com.example.viewmodel

import android.app.Application
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MainActivity
import com.example.model.CityLocation
import com.example.model.CurrentWeather
import com.example.model.DailyForecast
import com.example.model.FontDesign
import com.example.model.HourlyForecast
import com.example.model.ThemePalette
import com.example.model.WidgetCustomization
import com.example.model.WidgetLayoutType
import com.example.model.WidgetStyle
import com.example.repository.WeatherRepository
import com.example.repository.WidgetPreferencesRepository
import com.example.widget.ChronosDashboardWidgetProvider
import com.example.widget.ChronosPillWidgetProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class WallpaperPreset(val title: String) {
    COSMIC_AURORA("Cosmic Aurora"),
    ALPINE_SUNSET("Alpine Sunset"),
    CYBER_NIGHT("Cyber Night"),
    MINIMAL_OBSIDIAN("Minimal Obsidian"),
    PASTEL_DAWN("Pastel Dawn")
}

class WidgetStudioViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsRepo = WidgetPreferencesRepository(application)
    private val weatherRepo = WeatherRepository(application)

    private val _customization = MutableStateFlow(prefsRepo.loadCustomization())
    val customization: StateFlow<WidgetCustomization> = _customization.asStateFlow()

    private val _currentWeather = MutableStateFlow(weatherRepo.getCachedWeather())
    val currentWeather: StateFlow<CurrentWeather> = _currentWeather.asStateFlow()

    private val _hourlyForecast = MutableStateFlow<List<HourlyForecast>>(emptyList())
    val hourlyForecast: StateFlow<List<HourlyForecast>> = _hourlyForecast.asStateFlow()

    private val _dailyForecast = MutableStateFlow<List<DailyForecast>>(emptyList())
    val dailyForecast: StateFlow<List<DailyForecast>> = _dailyForecast.asStateFlow()

    private val _selectedCity = MutableStateFlow(weatherRepo.getSavedCity())
    val selectedCity: StateFlow<CityLocation> = _selectedCity.asStateFlow()

    private val _searchResults = MutableStateFlow<List<CityLocation>>(emptyList())
    val searchResults: StateFlow<List<CityLocation>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _isLoadingWeather = MutableStateFlow(false)
    val isLoadingWeather: StateFlow<Boolean> = _isLoadingWeather.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _batteryLevel = MutableStateFlow(84)
    val batteryLevel: StateFlow<Int> = _batteryLevel.asStateFlow()

    private val _liveTimeMillis = MutableStateFlow(System.currentTimeMillis())
    val liveTimeMillis: StateFlow<Long> = _liveTimeMillis.asStateFlow()

    private val _selectedWallpaper = MutableStateFlow(WallpaperPreset.COSMIC_AURORA)
    val selectedWallpaper: StateFlow<WallpaperPreset> = _selectedWallpaper.asStateFlow()

    private val _isStandbyMode = MutableStateFlow(false)
    val isStandbyMode: StateFlow<Boolean> = _isStandbyMode.asStateFlow()

    private val _pinWidgetMessage = MutableStateFlow<String?>(null)
    val pinWidgetMessage: StateFlow<String?> = _pinWidgetMessage.asStateFlow()

    private var tickerJob: Job? = null
    private var weatherAutoRefreshJob: Job? = null

    init {
        readBatteryLevel()
        startLiveTicker()
        fetchWeatherData()
        startWeatherAutoRefresh()
    }

    private fun readBatteryLevel() {
        try {
            val bm = getApplication<Application>().getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
            val level = bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 85
            if (level in 1..100) {
                _batteryLevel.value = level
            }
        } catch (e: Exception) {
            _batteryLevel.value = 85
        }
    }

    private fun startLiveTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (isActive) {
                _liveTimeMillis.value = System.currentTimeMillis()
                delay(1000L)
            }
        }
    }

    private fun startWeatherAutoRefresh() {
        weatherAutoRefreshJob?.cancel()
        weatherAutoRefreshJob = viewModelScope.launch {
            while (isActive) {
                delay(15 * 60 * 1000L) // Refresh every 15 minutes
                fetchWeatherData()
            }
        }
    }

    fun fetchWeatherData() {
        viewModelScope.launch {
            _isLoadingWeather.value = true
            _errorMessage.value = null
            try {
                val city = _selectedCity.value
                val (cur, forecasts) = weatherRepo.fetchWeather(city.latitude, city.longitude)
                _currentWeather.value = cur
                _hourlyForecast.value = forecasts.first
                _dailyForecast.value = forecasts.second
            } catch (e: Exception) {
                Log.e("WidgetStudioVM", "Error fetching weather", e)
                _errorMessage.value = "Using cached weather"
            } finally {
                _isLoadingWeather.value = false
            }
        }
    }

    fun updateCustomization(updater: (WidgetCustomization) -> WidgetCustomization) {
        val updated = updater(_customization.value)
        _customization.value = updated
        prefsRepo.saveCustomization(updated)
    }

    fun setStyle(style: WidgetStyle) {
        updateCustomization { it.copy(style = style) }
    }

    fun setPalette(palette: ThemePalette) {
        updateCustomization { it.copy(palette = palette) }
    }

    fun setFont(font: FontDesign) {
        updateCustomization { it.copy(fontDesign = font) }
    }

    fun setLayout(layout: WidgetLayoutType) {
        updateCustomization { it.copy(layoutType = layout) }
    }

    fun setWallpaper(wallpaper: WallpaperPreset) {
        _selectedWallpaper.value = wallpaper
    }

    fun setStandbyMode(active: Boolean) {
        _isStandbyMode.value = active
    }

    fun detectAndFetchLocationWeather(context: Context) {
        viewModelScope.launch {
            _isLoadingWeather.value = true
            try {
                val coords = com.example.util.LocationProviderHelper.getCurrentLocation(context)
                if (coords != null) {
                    val city = weatherRepo.reverseGeocodeLocation(coords.first, coords.second)
                    _selectedCity.value = city
                    weatherRepo.saveCity(city)
                    val (cur, forecasts) = weatherRepo.fetchWeather(coords.first, coords.second)
                    _currentWeather.value = cur
                    _hourlyForecast.value = forecasts.first
                    _dailyForecast.value = forecasts.second
                    _pinWidgetMessage.value = "Updated weather for ${city.name}!"
                } else {
                    _pinWidgetMessage.value = "GPS location unavailable. Please ensure location is enabled."
                }
            } catch (e: Exception) {
                Log.e("WidgetStudioVM", "Error fetching user location", e)
                _pinWidgetMessage.value = "Failed to detect location: ${e.localizedMessage}"
            } finally {
                _isLoadingWeather.value = false
            }
        }
    }

    fun isOwmConfigured(): Boolean = weatherRepo.isOwmConfigured()

    fun selectCity(city: CityLocation) {
        _selectedCity.value = city
        weatherRepo.saveCity(city)
        _searchResults.value = emptyList()
        fetchWeatherData()
    }

    fun searchCities(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            _isSearching.value = true
            try {
                val results = weatherRepo.searchCities(query)
                _searchResults.value = results
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun clearCitySearch() {
        _searchResults.value = emptyList()
    }

    fun clearPinMessage() {
        _pinWidgetMessage.value = null
    }

    fun requestPinWidget(isDashboard: Boolean) {
        val context = getApplication<Application>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                val providerClass = if (isDashboard) {
                    ChronosDashboardWidgetProvider::class.java
                } else {
                    ChronosPillWidgetProvider::class.java
                }
                val myProvider = ComponentName(context, providerClass)
                val successIntent = Intent(context, MainActivity::class.java)
                val successPendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    successIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                appWidgetManager.requestPinAppWidget(myProvider, null, successPendingIntent)
                _pinWidgetMessage.value = "Widget pin request sent to home launcher!"
            } else {
                _pinWidgetMessage.value = "Launcher does not support 1-tap pinning. Long-press home screen to add manually."
            }
        } else {
            _pinWidgetMessage.value = "Long-press your home screen to add Chronos Widget."
        }
    }
}
