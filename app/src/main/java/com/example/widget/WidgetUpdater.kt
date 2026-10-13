package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.model.CurrentWeather
import com.example.model.WidgetCustomization
import com.example.repository.WeatherRepository
import com.example.repository.WidgetPreferencesRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object WidgetUpdater {

    fun updateAllWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context) ?: return
        val prefsRepo = WidgetPreferencesRepository(context)
        val weatherRepo = WeatherRepository(context)
        val config = prefsRepo.loadCustomization()
        val weather = weatherRepo.getCachedWeather()
        val city = weatherRepo.getSavedCity()

        // Update Dashboard Widgets
        val dashboardComponent = ComponentName(context, ChronosDashboardWidgetProvider::class.java)
        val dashboardIds = appWidgetManager.getAppWidgetIds(dashboardComponent)
        if (dashboardIds.isNotEmpty()) {
            for (widgetId in dashboardIds) {
                updateDashboardWidget(context, appWidgetManager, widgetId, config, weather, city.name)
            }
        }

        // Update Pill Widgets
        val pillComponent = ComponentName(context, ChronosPillWidgetProvider::class.java)
        val pillIds = appWidgetManager.getAppWidgetIds(pillComponent)
        if (pillIds.isNotEmpty()) {
            for (widgetId in pillIds) {
                updatePillWidget(context, appWidgetManager, widgetId, config, weather)
            }
        }
    }

    fun updateDashboardWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        config: WidgetCustomization,
        weather: CurrentWeather,
        cityName: String
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_dashboard)

        val now = Date()
        val timePattern = if (config.is24HourFormat) "HH:mm" else "h:mm"
        val timeStr = SimpleDateFormat(timePattern, Locale.getDefault()).format(now)
        val amPmStr = if (config.is24HourFormat) "" else SimpleDateFormat("a", Locale.getDefault()).format(now)
        val dateStr = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(now)

        val temp = if (config.useFahrenheit) {
            (weather.temperatureC * 9 / 5 + 32).toInt()
        } else {
            weather.temperatureC.toInt()
        }
        val high = if (config.useFahrenheit) {
            (weather.highTempC * 9 / 5 + 32).toInt()
        } else {
            weather.highTempC.toInt()
        }
        val low = if (config.useFahrenheit) {
            (weather.lowTempC * 9 / 5 + 32).toInt()
        } else {
            weather.lowTempC.toInt()
        }

        views.setTextViewText(R.id.widget_time, timeStr)
        views.setTextViewText(R.id.widget_am_pm, amPmStr)
        views.setTextViewText(R.id.widget_date, dateStr)
        views.setTextViewText(R.id.widget_location, "📍 $cityName")
        views.setTextViewText(R.id.widget_temp, "$temp°")
        views.setTextViewText(R.id.widget_condition, "${weather.conditionEmoji} ${weather.conditionDescription}")
        views.setTextViewText(R.id.widget_high_low, "H: $high°  L: $low°")

        // Setup click action to open MainActivity
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    fun updatePillWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        config: WidgetCustomization,
        weather: CurrentWeather
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_pill)

        val now = Date()
        val timePattern = if (config.is24HourFormat) "HH:mm" else "h:mm"
        val timeStr = SimpleDateFormat(timePattern, Locale.getDefault()).format(now)
        val dateStr = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(now)

        val temp = if (config.useFahrenheit) {
            (weather.temperatureC * 9 / 5 + 32).toInt()
        } else {
            weather.temperatureC.toInt()
        }

        views.setTextViewText(R.id.widget_pill_time, timeStr)
        views.setTextViewText(R.id.widget_pill_date, dateStr)
        views.setTextViewText(R.id.widget_pill_weather, "${weather.conditionEmoji} $temp°")

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_pill_root, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
