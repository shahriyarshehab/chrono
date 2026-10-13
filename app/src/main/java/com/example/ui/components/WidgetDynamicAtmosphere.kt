package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CityLocation
import com.example.model.CurrentWeather
import com.example.model.WeatherCategory
import com.example.model.WidgetCustomization
import com.example.model.WidgetLayoutType

@Composable
fun WidgetDynamicAtmosphere(
    timeInfo: FormattedTime,
    weather: CurrentWeather,
    city: CityLocation,
    config: WidgetCustomization,
    batteryLevel: Int,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(config.cornerRadiusDp.dp)

    // Compute dynamic sky gradient based on live weather & day/night
    val skyColors = when (weather.conditionCategory) {
        WeatherCategory.SUNNY -> {
            if (weather.isDay) {
                listOf(Color(0xFF0284C7), Color(0xFF0369A1), Color(0xFFF59E0B))
            } else {
                listOf(Color(0xFF0B132B), Color(0xFF1C2541), Color(0xFF3A506B))
            }
        }
        WeatherCategory.PARTLY_CLOUDY, WeatherCategory.CLOUDY -> {
            if (weather.isDay) {
                listOf(Color(0xFF334155), Color(0xFF475569), Color(0xFF64748B))
            } else {
                listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF334155))
            }
        }
        WeatherCategory.RAIN, WeatherCategory.DRIZZLE -> {
            listOf(Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF38BDF8).copy(alpha = 0.5f))
        }
        WeatherCategory.THUNDERSTORM -> {
            listOf(Color(0xFF2E1065), Color(0xFF3B0764), Color(0xFF581C87))
        }
        WeatherCategory.SNOW -> {
            listOf(Color(0xFF1E293B), Color(0xFF475569), Color(0xFFBAE6FD))
        }
        WeatherCategory.FOG -> {
            listOf(Color(0xFF374151), Color(0xFF4B5563), Color(0xFF6B7280))
        }
    }

    val tempVal = if (config.useFahrenheit) {
        (weather.temperatureC * 9 / 5 + 32).toInt()
    } else {
        weather.temperatureC.toInt()
    }

    Box(
        modifier = modifier
            .testTag("widget_dynamic_atmosphere")
            .clip(shape)
            .background(Brush.linearGradient(skyColors))
            .border(1.2.dp, Color.White.copy(alpha = 0.25f), shape)
            .padding(18.dp)
    ) {
        // Ambient canvas accents (subtle stars or light mist)
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (!weather.isDay && weather.conditionCategory == WeatherCategory.SUNNY) {
                // Draw night star dots
                val starPositions = listOf(
                    Offset(size.width * 0.15f, size.height * 0.2f),
                    Offset(size.width * 0.45f, size.height * 0.15f),
                    Offset(size.width * 0.75f, size.height * 0.35f),
                    Offset(size.width * 0.85f, size.height * 0.18f),
                    Offset(size.width * 0.32f, size.height * 0.65f)
                )
                for (pos in starPositions) {
                    drawCircle(Color.White.copy(alpha = 0.65f), radius = 2.dp.toPx(), center = pos)
                }
            }
        }

        when (config.layoutType) {
            WidgetLayoutType.DASHBOARD_4X2 -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LIVE SKY • ${city.name.uppercase()}",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "⚡ $batteryLevel%",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = timeInfo.fullTime,
                                    color = Color.White,
                                    fontSize = 46.sp,
                                    fontWeight = FontWeight.Black,
                                    lineHeight = 44.sp
                                )
                                if (config.showSeconds) {
                                    Text(
                                        text = ":${timeInfo.seconds}",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                                    )
                                }
                            }
                            Text(
                                text = timeInfo.fullDate,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = weather.conditionEmoji, fontSize = 34.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$tempVal°",
                                    color = Color.White,
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Text(
                                text = weather.conditionDescription,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.25f))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "💧 Humidity: ${weather.humidityPercent}%",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp
                        )
                        Text(
                            text = "💨 Wind: ${weather.windSpeedKmh.toInt()} km/h",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp
                        )
                        Text(
                            text = "☀️ UV: ${weather.uvIndex.toInt()}",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            else -> {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${timeInfo.fullTime}  ${timeInfo.dayOfWeek.take(3)}",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${weather.conditionEmoji} $tempVal°",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
