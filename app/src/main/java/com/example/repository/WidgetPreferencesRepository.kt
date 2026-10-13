package com.example.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.model.FontDesign
import com.example.model.ThemePalette
import com.example.model.WidgetCustomization
import com.example.model.WidgetLayoutType
import com.example.model.WidgetStyle
import com.example.widget.WidgetUpdater

class WidgetPreferencesRepository(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("chronos_widget_customization", Context.MODE_PRIVATE)

    fun loadCustomization(): WidgetCustomization {
        val styleName = prefs.getString("style", WidgetStyle.AURA_GLASS.name) ?: WidgetStyle.AURA_GLASS.name
        val style = try { WidgetStyle.valueOf(styleName) } catch (e: Exception) { WidgetStyle.AURA_GLASS }

        val paletteName = prefs.getString("palette", ThemePalette.COSMIC_NIGHT.name) ?: ThemePalette.COSMIC_NIGHT.name
        val palette = try { ThemePalette.valueOf(paletteName) } catch (e: Exception) { ThemePalette.COSMIC_NIGHT }

        val fontName = prefs.getString("font", FontDesign.MODERN_SANS.name) ?: FontDesign.MODERN_SANS.name
        val font = try { FontDesign.valueOf(fontName) } catch (e: Exception) { FontDesign.MODERN_SANS }

        val layoutName = prefs.getString("layout", WidgetLayoutType.DASHBOARD_4X2.name) ?: WidgetLayoutType.DASHBOARD_4X2.name
        val layout = try { WidgetLayoutType.valueOf(layoutName) } catch (e: Exception) { WidgetLayoutType.DASHBOARD_4X2 }

        val is24h = prefs.getBoolean("is_24h", false)
        val useFahrenheit = prefs.getBoolean("use_fahrenheit", true)
        val showSeconds = prefs.getBoolean("show_seconds", true)
        val showBattery = prefs.getBoolean("show_battery", true)
        val showLocation = prefs.getBoolean("show_location", true)
        val showFeelsLike = prefs.getBoolean("show_feels_like", true)
        val showDetailedStats = prefs.getBoolean("show_detailed_stats", true)
        val showMiniForecast = prefs.getBoolean("show_mini_forecast", true)
        val bgOpacity = prefs.getFloat("bg_opacity", 0.85f)
        val cornerRadius = prefs.getInt("corner_radius", 24)

        return WidgetCustomization(
            style = style,
            palette = palette,
            fontDesign = font,
            layoutType = layout,
            is24HourFormat = is24h,
            useFahrenheit = useFahrenheit,
            showSeconds = showSeconds,
            showBatteryLevel = showBattery,
            showLocationName = showLocation,
            showFeelsLike = showFeelsLike,
            showDetailedStats = showDetailedStats,
            showMiniForecast = showMiniForecast,
            backgroundOpacity = bgOpacity,
            cornerRadiusDp = cornerRadius
        )
    }

    fun saveCustomization(config: WidgetCustomization) {
        prefs.edit()
            .putString("style", config.style.name)
            .putString("palette", config.palette.name)
            .putString("font", config.fontDesign.name)
            .putString("layout", config.layoutType.name)
            .putBoolean("is_24h", config.is24HourFormat)
            .putBoolean("use_fahrenheit", config.useFahrenheit)
            .putBoolean("show_seconds", config.showSeconds)
            .putBoolean("show_battery", config.showBatteryLevel)
            .putBoolean("show_location", config.showLocationName)
            .putBoolean("show_feels_like", config.showFeelsLike)
            .putBoolean("show_detailed_stats", config.showDetailedStats)
            .putBoolean("show_mini_forecast", config.showMiniForecast)
            .putFloat("bg_opacity", config.backgroundOpacity)
            .putInt("corner_radius", config.cornerRadiusDp)
            .apply()

        // Sync with active Android home screen widgets immediately
        try {
            WidgetUpdater.updateAllWidgets(context)
        } catch (e: Exception) {
            // Non-fatal if manager not available
        }
    }
}
