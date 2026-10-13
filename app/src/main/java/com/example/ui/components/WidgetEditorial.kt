package com.example.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CityLocation
import com.example.model.CurrentWeather
import com.example.model.WidgetCustomization
import com.example.model.WidgetLayoutType

@Composable
fun WidgetEditorial(
    timeInfo: FormattedTime,
    weather: CurrentWeather,
    city: CityLocation,
    config: WidgetCustomization,
    modifier: Modifier = Modifier
) {
    val bg = config.palette.backgroundDark.copy(alpha = config.backgroundOpacity)
    val accent = config.palette.primaryAccent
    val gold = config.palette.secondaryAccent
    val shape = RoundedCornerShape(20.dp)

    val tempVal = if (config.useFahrenheit) {
        (weather.temperatureC * 9 / 5 + 32).toInt()
    } else {
        weather.temperatureC.toInt()
    }

    Box(
        modifier = modifier
            .testTag("widget_editorial")
            .clip(shape)
            .background(bg)
            .border(1.dp, gold.copy(alpha = 0.35f), shape)
            .padding(18.dp)
    ) {
        when (config.layoutType) {
            WidgetLayoutType.DASHBOARD_4X2 -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header: Subtitle & City
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CHRONOMETER — EDITION N° ${timeInfo.seconds}",
                            color = gold,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Serif,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = city.name.uppercase(),
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Serif,
                            letterSpacing = 1.sp
                        )
                    }

                    // Main Row: Large Serif Hours/Minutes and Classical Weather
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = timeInfo.hours,
                                color = Color.White,
                                fontSize = 52.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 50.sp
                            )
                            Text(
                                text = " : ",
                                color = gold,
                                fontSize = 38.sp,
                                fontFamily = FontFamily.Serif
                            )
                            Text(
                                text = timeInfo.minutes,
                                color = accent,
                                fontSize = 52.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Light,
                                lineHeight = 50.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = weather.conditionEmoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$tempVal°",
                                    color = Color.White,
                                    fontSize = 38.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = weather.conditionDescription,
                                color = gold,
                                fontSize = 12.sp,
                                fontStyle = FontStyle.Italic,
                                fontFamily = FontFamily.Serif
                            )
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f), thickness = 0.8.dp)

                    // Footer: Date and Atmosphere
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = timeInfo.fullDate,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "Barometer: ${weather.humidityPercent}% RH • Wind: ${weather.windSpeedKmh.toInt()} km/h",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Serif
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
                        text = "${timeInfo.fullTime} • ${timeInfo.dayOfWeek}",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "${weather.conditionEmoji} $tempVal°",
                        color = gold,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
