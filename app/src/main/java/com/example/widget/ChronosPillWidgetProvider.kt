package com.example.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent

class ChronosPillWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        WidgetUpdater.updateAllWidgets(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE ||
            intent.action == Intent.ACTION_TIME_TICK ||
            intent.action == Intent.ACTION_TIME_CHANGED ||
            intent.action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            WidgetUpdater.updateAllWidgets(context)
        }
    }
}
