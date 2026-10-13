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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CityLocation
import com.example.model.CurrentWeather
import com.example.model.WidgetCustomization
import com.example.model.WidgetLayoutType

@Composable
fun WidgetCyberNeon(
    timeInfo: FormattedTime,
    weather: CurrentWeather,
    city: CityLocation,
    config: WidgetCustomization,
    batteryLevel: Int,
    modifier: Modifier = Modifier
) {
    val neonCyan = config.palette.primaryAccent
    val neonMagenta = config.palette.secondaryAccent
    val darkBg = config.palette.backgroundDark.copy(alpha = config.backgroundOpacity)
    val shape = RoundedCornerShape(12.dp)

    val tempVal = if (config.useFahrenheit) {
        (weather.temperatureC * 9 / 5 + 32).toInt()
    } else {
        weather.temperatureC.toInt()
    }

    Box(
        modifier = modifier
            .testTag("widget_cyber_neon")
            .clip(shape)
            .background(darkBg)
            .border(1.dp, neonCyan.copy(alpha = 0.5f), shape)
            .padding(14.dp)
    ) {
        // Futuristic corner brackets overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val bracketLength = 24.dp.toPx()
            val strokeW = 2.dp.toPx()
            val color = neonCyan

            // Top-left bracket
            drawLine(color, Offset(0f, 0f), Offset(bracketLength, 0f), strokeWidth = strokeW)
            drawLine(color, Offset(0f, 0f), Offset(0f, bracketLength), strokeWidth = strokeW)

            // Top-right bracket
            drawLine(color, Offset(size.width, 0f), Offset(size.width - bracketLength, 0f), strokeWidth = strokeW)
            drawLine(color, Offset(size.width, 0f), Offset(size.width, bracketLength), strokeWidth = strokeW)

            // Bottom-left bracket
            drawLine(color, Offset(0f, size.height), Offset(bracketLength, size.height), strokeWidth = strokeW)
            drawLine(color, Offset(0f, size.height), Offset(0f, size.height - bracketLength), strokeWidth = strokeW)

            // Bottom-right bracket
            drawLine(color, Offset(size.width, size.height), Offset(size.width - bracketLength, size.height), strokeWidth = strokeW)
            drawLine(color, Offset(size.width, size.height), Offset(size.width, size.height - bracketLength), strokeWidth = strokeW)
        }

        when (config.layoutType) {
            WidgetLayoutType.DASHBOARD_4X2 -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header HUD: SYS STATUS & BATTERY
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "◈ SYS.CHRONOS // ",
                                color = neonMagenta,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "LOC: ${city.name.uppercase()}",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(
                            text = "PWR:${batteryLevel}% [ONLINE]",
                            color = neonCyan,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Main readout: Neon digital time and weather
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
                                    fontSize = 44.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = ":${timeInfo.seconds}",
                                    color = neonCyan,
                                    fontSize = 18.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                                )
                            }
                            Text(
                                text = "${timeInfo.dayOfWeek.uppercase()} // ${timeInfo.dateMonth.uppercase()}",
                                color = neonMagenta,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Weather block
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = weather.conditionEmoji, fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$tempVal°",
                                    color = neonCyan,
                                    fontSize = 38.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Text(
                                text = weather.conditionDescription.uppercase(),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Bottom: Live telemetry gauge
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "HUM: ${weather.humidityPercent}% | WIND: ${weather.windSpeedKmh.toInt()}KM/H",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "SEC_TICK: ${timeInfo.seconds}s",
                                color = neonCyan,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { timeInfo.secondsFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = neonCyan,
                            trackColor = neonCyan.copy(alpha = 0.2f)
                        )
                    }
                }
            }

            else -> {
                // Compact HUD
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = timeInfo.fullTime,
                                color = Color.White,
                                fontSize = 26.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = ":${timeInfo.seconds}",
                                color = neonCyan,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
                            )
                        }
                        Text(
                            text = "${timeInfo.dayOfWeek.take(3).uppercase()} ${timeInfo.dateMonth}",
                            color = neonMagenta,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = weather.conditionEmoji, fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$tempVal°",
                            color = neonCyan,
                            fontSize = 24.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}
