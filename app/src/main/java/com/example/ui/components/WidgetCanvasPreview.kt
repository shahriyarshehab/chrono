package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.model.CityLocation
import com.example.model.CurrentWeather
import com.example.model.HourlyForecast
import com.example.model.WidgetCustomization
import com.example.model.WidgetLayoutType
import com.example.model.WidgetStyle

@Composable
fun WidgetCanvasPreview(
    timeMillis: Long,
    weather: CurrentWeather,
    hourly: List<HourlyForecast>,
    city: CityLocation,
    config: WidgetCustomization,
    batteryLevel: Int,
    modifier: Modifier = Modifier
) {
    val timeInfo = TimeFormatHelper.format(timeMillis, config.is24HourFormat)

    val widgetHeight = when (config.layoutType) {
        WidgetLayoutType.DASHBOARD_4X2 -> 175.dp
        WidgetLayoutType.PILL_COMPACT_4X1 -> 62.dp
        WidgetLayoutType.RADIAL_DIAL_2X2 -> 180.dp
        WidgetLayoutType.VERTICAL_STACK_2X3 -> 200.dp
    }

    val widgetModifier = if (config.layoutType == WidgetLayoutType.RADIAL_DIAL_2X2) {
        Modifier
            .width(190.dp)
            .height(widgetHeight)
    } else {
        Modifier
            .fillMaxWidth()
            .height(widgetHeight)
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        when (config.style) {
            WidgetStyle.AURA_GLASS -> {
                WidgetAuraGlass(
                    timeInfo = timeInfo,
                    weather = weather,
                    hourly = hourly,
                    city = city,
                    config = config,
                    batteryLevel = batteryLevel,
                    modifier = widgetModifier
                )
            }
            WidgetStyle.CYBER_NEON -> {
                WidgetCyberNeon(
                    timeInfo = timeInfo,
                    weather = weather,
                    city = city,
                    config = config,
                    batteryLevel = batteryLevel,
                    modifier = widgetModifier
                )
            }
            WidgetStyle.NEO_BRUTALIST -> {
                WidgetNeoBrutalist(
                    timeInfo = timeInfo,
                    weather = weather,
                    city = city,
                    config = config,
                    modifier = widgetModifier
                )
            }
            WidgetStyle.EDITORIAL_CHRONO -> {
                WidgetEditorial(
                    timeInfo = timeInfo,
                    weather = weather,
                    city = city,
                    config = config,
                    modifier = widgetModifier
                )
            }
            WidgetStyle.RETRO_FLIP -> {
                WidgetRetroFlip(
                    timeInfo = timeInfo,
                    weather = weather,
                    city = city,
                    config = config,
                    modifier = widgetModifier
                )
            }
            WidgetStyle.DYNAMIC_ATMOSPHERE -> {
                WidgetDynamicAtmosphere(
                    timeInfo = timeInfo,
                    weather = weather,
                    city = city,
                    config = config,
                    batteryLevel = batteryLevel,
                    modifier = widgetModifier
                )
            }
        }
    }
}
