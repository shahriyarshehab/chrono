package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.CityLocation
import com.example.model.FontDesign
import com.example.model.ThemePalette
import com.example.model.WeatherCategory
import com.example.model.WeatherCodeHelper
import com.example.model.WidgetCustomization
import com.example.model.WidgetStyle
import com.example.network.OpenWeatherMapClient
import com.example.repository.WeatherRepository
import com.example.repository.WidgetPreferencesRepository
import com.example.ui.components.TimeFormatHelper
import com.example.util.LocationProviderHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Chronos Widgets", appName)
    }

    @Test
    fun `test time format helper`() {
        val testMillis = 1700000000000L
        val formatted12h = TimeFormatHelper.format(testMillis, is24Hour = false)
        assertNotNull(formatted12h.hours)
        assertNotNull(formatted12h.minutes)
        assertNotNull(formatted12h.seconds)
        assertNotNull(formatted12h.fullDate)

        val formatted24h = TimeFormatHelper.format(testMillis, is24Hour = true)
        assertEquals("", formatted24h.amPm)
    }

    @Test
    fun `test weather code helper mappings`() {
        val sunnyInfo = WeatherCodeHelper.parseCondition(0, isDay = true)
        assertEquals(WeatherCategory.SUNNY, sunnyInfo.category)
        assertEquals("Clear Sky", sunnyInfo.description)

        val thunderInfo = WeatherCodeHelper.parseCondition(95, isDay = true)
        assertEquals(WeatherCategory.THUNDERSTORM, thunderInfo.category)
    }

    @Test
    fun `test openweathermap condition parsing`() {
        val clearInfo = OpenWeatherMapClient.parseOwmCondition(800, isDay = true)
        assertEquals(WeatherCategory.SUNNY, clearInfo.category)
        assertEquals("☀️", clearInfo.emoji)

        val rainInfo = OpenWeatherMapClient.parseOwmCondition(501, isDay = true)
        assertEquals(WeatherCategory.RAIN, rainInfo.category)
        assertEquals("🌧️", rainInfo.emoji)

        val stormInfo = OpenWeatherMapClient.parseOwmCondition(211, isDay = true)
        assertEquals(WeatherCategory.THUNDERSTORM, stormInfo.category)
    }

    @Test
    fun `test location helper without permissions returns false`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // By default without granting in robolectric test, returns false safely without crashing
        val hasPerm = LocationProviderHelper.hasLocationPermission(context)
        assertEquals(false, hasPerm)
    }

    @Test
    fun `test weather repository city persistence`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = WeatherRepository(context)

        val testCity = CityLocation("Seattle", "United States", 47.6062, -122.3321)
        repo.saveCity(testCity)

        val saved = repo.getSavedCity()
        assertEquals("Seattle", saved.name)
        assertEquals(47.6062, saved.latitude, 0.001)
        assertEquals(-122.3321, saved.longitude, 0.001)
    }

    @Test
    fun `test widget preferences save and load`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = WidgetPreferencesRepository(context)

        val custom = WidgetCustomization(
            style = WidgetStyle.CYBER_NEON,
            palette = ThemePalette.SOLAR_AMBER,
            fontDesign = FontDesign.CYBER_MONO,
            is24HourFormat = true,
            useFahrenheit = false
        )

        repo.saveCustomization(custom)
        val loaded = repo.loadCustomization()

        assertEquals(WidgetStyle.CYBER_NEON, loaded.style)
        assertEquals(ThemePalette.SOLAR_AMBER, loaded.palette)
        assertEquals(FontDesign.CYBER_MONO, loaded.fontDesign)
        assertEquals(true, loaded.is24HourFormat)
        assertEquals(false, loaded.useFahrenheit)
    }
}
