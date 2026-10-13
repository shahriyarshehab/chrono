package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CityLocation
import com.example.model.CurrentWeather
import com.example.model.HourlyForecast
import com.example.model.WidgetCustomization
import com.example.model.WidgetLayoutType
import com.example.ui.theme.FontHelper

@Composable
fun WidgetAuraGlass(
    timeInfo: FormattedTime,
    weather: CurrentWeather,
    hourly: List<HourlyForecast>,
    city: CityLocation,
    config: WidgetCustomization,
    batteryLevel: Int,
    modifier: Modifier = Modifier
) {
    val palette = config.palette
    val fontFamily = FontHelper.getFontFamily(config.fontDesign)
    val shape = RoundedCornerShape(config.cornerRadiusDp.dp)

    val infiniteTransition = rememberInfiniteTransition(label = "aura_pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val tempVal = if (config.useFahrenheit) {
        (weather.temperatureC * 9 / 5 + 32).toInt()
    } else {
        weather.temperatureC.toInt()
    }

    val feelsVal = if (config.useFahrenheit) {
        (weather.feelsLikeC * 9 / 5 + 32).toInt()
    } else {
        weather.feelsLikeC.toInt()
    }

    val highVal = if (config.useFahrenheit) {
        (weather.highTempC * 9 / 5 + 32).toInt()
    } else {
        weather.highTempC.toInt()
    }

    val lowVal = if (config.useFahrenheit) {
        (weather.lowTempC * 9 / 5 + 32).toInt()
    } else {
        weather.lowTempC.toInt()
    }

    val glassBrush = Brush.linearGradient(
        colors = listOf(
            palette.surfaceColor.copy(alpha = config.backgroundOpacity * 0.9f),
            palette.backgroundDark.copy(alpha = config.backgroundOpacity)
        )
    )

    val borderBrush = Brush.linearGradient(
        colors = listOf(
            palette.primaryAccent.copy(alpha = glowAlpha),
            palette.secondaryAccent.copy(alpha = 0.3f),
            Color.White.copy(alpha = 0.15f)
        )
    )

    Box(
        modifier = modifier
            .testTag("widget_aura_glass")
            .clip(shape)
            .background(glassBrush)
            .border(1.5.dp, borderBrush, shape)
            .padding(18.dp)
    ) {
        when (config.layoutType) {
            WidgetLayoutType.DASHBOARD_4X2 -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top header: Location & Battery
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (config.showLocationName) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(palette.primaryAccent.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "📍 ${city.name}",
                                    color = palette.textPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = fontFamily
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        if (config.showBatteryLevel) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.1f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "⚡ $batteryLevel%",
                                    color = palette.secondaryAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = fontFamily
                                )
                            }
                        }
                    }

                    // Main Middle: Time & Weather
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Clock
                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = timeInfo.fullTime,
                                    color = palette.textPrimary,
                                    fontSize = 46.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = fontFamily,
                                    lineHeight = 46.sp
                                )

                                if (config.showSeconds) {
                                    Text(
                                        text = ":${timeInfo.seconds}",
                                        color = palette.primaryAccent,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = fontFamily,
                                        modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                                    )
                                }

                                if (!config.is24HourFormat && timeInfo.amPm.isNotEmpty()) {
                                    Text(
                                        text = timeInfo.amPm,
                                        color = palette.secondaryAccent,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = fontFamily,
                                        modifier = Modifier.padding(bottom = 8.dp, start = 6.dp)
                                    )
                                }
                            }

                            Text(
                                text = timeInfo.fullDate,
                                color = palette.textSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = fontFamily
                            )
                        }

                        // Right: Big Weather
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = weather.conditionEmoji,
                                    fontSize = 32.sp,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Text(
                                    text = "$tempVal°",
                                    color = palette.primaryAccent,
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = fontFamily,
                                    lineHeight = 42.sp
                                )
                            }

                            Text(
                                text = weather.conditionDescription,
                                color = palette.textPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = fontFamily
                            )

                            if (config.showFeelsLike) {
                                Text(
                                    text = "Feels like $feelsVal° • H:$highVal° L:$lowVal°",
                                    color = palette.textSecondary,
                                    fontSize = 11.sp,
                                    fontFamily = fontFamily
                                )
                            }
                        }
                    }

                    // Bottom: Mini stats / forecast
                    if (config.showDetailedStats) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.2f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💧 ${weather.humidityPercent}% Humidity",
                                color = palette.textSecondary,
                                fontSize = 11.sp,
                                fontFamily = fontFamily
                            )
                            Text(
                                text = "💨 ${weather.windSpeedKmh.toInt()} km/h",
                                color = palette.textSecondary,
                                fontSize = 11.sp,
                                fontFamily = fontFamily
                            )
                            Text(
                                text = "☀️ UV ${weather.uvIndex.toInt()}",
                                color = palette.textSecondary,
                                fontSize = 11.sp,
                                fontFamily = fontFamily
                            )
                        }
                    }
                }
            }

            WidgetLayoutType.PILL_COMPACT_4X1 -> {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = timeInfo.fullTime,
                            color = palette.textPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = fontFamily
                        )

                        if (!config.is24HourFormat && timeInfo.amPm.isNotEmpty()) {
                            Text(
                                text = " ${timeInfo.amPm}",
                                color = palette.secondaryAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = fontFamily
                            )
                        }

                        Text(
                            text = " • ${timeInfo.dateMonth}",
                            color = palette.textSecondary,
                            fontSize = 13.sp,
                            fontFamily = fontFamily,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = weather.conditionEmoji,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = "$tempVal°",
                            color = palette.primaryAccent,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = fontFamily
                        )
                    }
                }
            }

            WidgetLayoutType.RADIAL_DIAL_2X2 -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = timeInfo.dayOfWeek.uppercase(),
                        color = palette.secondaryAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = fontFamily
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = timeInfo.fullTime,
                            color = palette.textPrimary,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = fontFamily
                        )
                        if (config.showSeconds) {
                            Text(
                                text = ":${timeInfo.seconds}",
                                color = palette.primaryAccent,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = fontFamily
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(palette.primaryAccent.copy(alpha = 0.15f))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(text = weather.conditionEmoji, fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$tempVal°",
                            color = palette.textPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = fontFamily
                        )
                    }

                    Text(
                        text = "📍 ${city.name}",
                        color = palette.textSecondary,
                        fontSize = 11.sp,
                        fontFamily = fontFamily
                    )
                }
            }

            WidgetLayoutType.VERTICAL_STACK_2X3 -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = timeInfo.dayOfWeek,
                            color = palette.secondaryAccent,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = fontFamily
                        )
                        Text(
                            text = "📍 ${city.name}",
                            color = palette.textSecondary,
                            fontSize = 12.sp,
                            fontFamily = fontFamily
                        )
                    }

                    Column {
                        Text(
                            text = timeInfo.hours,
                            color = palette.textPrimary,
                            fontSize = 54.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = fontFamily,
                            lineHeight = 52.sp
                        )
                        Text(
                            text = timeInfo.minutes,
                            color = palette.primaryAccent,
                            fontSize = 54.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = fontFamily,
                            lineHeight = 52.sp
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = weather.conditionEmoji, fontSize = 26.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "$tempVal°",
                                    color = palette.textPrimary,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = fontFamily
                                )
                                Text(
                                    text = weather.conditionDescription,
                                    color = palette.textSecondary,
                                    fontSize = 11.sp,
                                    fontFamily = fontFamily
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
