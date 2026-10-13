package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
fun WidgetRetroFlip(
    timeInfo: FormattedTime,
    weather: CurrentWeather,
    city: CityLocation,
    config: WidgetCustomization,
    modifier: Modifier = Modifier
) {
    val flapBg = Color(0xFF1E1E24)
    val amberLcd = Color(0xFFFFB300)
    val outerBg = Color(0xFF121216).copy(alpha = config.backgroundOpacity)
    val shape = RoundedCornerShape(16.dp)

    val tempVal = if (config.useFahrenheit) {
        (weather.temperatureC * 9 / 5 + 32).toInt()
    } else {
        weather.temperatureC.toInt()
    }

    Box(
        modifier = modifier
            .testTag("widget_retro_flip")
            .clip(shape)
            .background(outerBg)
            .border(1.5.dp, Color(0xFF33333E), shape)
            .padding(14.dp)
    ) {
        when (config.layoutType) {
            WidgetLayoutType.DASHBOARD_4X2 -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header label
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SOLARI FLIP • ${city.name.uppercase()}",
                            color = Color(0xFF888899),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = timeInfo.fullDate.uppercase(),
                            color = amberLcd,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Split-flap time cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Hour card
                        FlipCard(text = timeInfo.hours)

                        // Colon
                        Text(
                            text = ":",
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )

                        // Minute card
                        FlipCard(text = timeInfo.minutes)

                        if (config.showSeconds) {
                            Spacer(modifier = Modifier.width(10.dp))
                            // Smaller seconds card
                            SmallFlipCard(text = timeInfo.seconds)
                        }
                    }

                    // Amber dot-matrix weather forecast bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF08080A))
                            .border(1.dp, Color(0xFF222228), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = weather.conditionEmoji, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${weather.conditionDescription.uppercase()} $tempVal°",
                                color = amberLcd,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "HUM:${weather.humidityPercent}%  WND:${weather.windSpeedKmh.toInt()}K",
                            color = Color(0xFFB08800),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FlipCard(text = timeInfo.hours, isSmall = true)
                        Text(text = ":", color = Color.White, fontSize = 20.sp, modifier = Modifier.padding(horizontal = 4.dp))
                        FlipCard(text = timeInfo.minutes, isSmall = true)
                    }

                    Text(
                        text = "${weather.conditionEmoji} $tempVal°",
                        color = amberLcd,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun FlipCard(text: String, isSmall: Boolean = false) {
    val shape = RoundedCornerShape(8.dp)
    val width = if (isSmall) 42.dp else 68.dp
    val height = if (isSmall) 34.dp else 56.dp
    val fontSize = if (isSmall) 22.sp else 38.sp

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF2C2C36), Color(0xFF16161B))
                )
            )
            .border(1.dp, Color(0xFF444455), shape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif
        )
        // Split flap center divider seam
        HorizontalDivider(
            color = Color(0xFF0B0B0E),
            thickness = 1.5.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SmallFlipCard(text: String) {
    Box(
        modifier = Modifier
            .width(36.dp)
            .height(38.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF1E1E24))
            .border(1.dp, Color(0xFF3A3A46), RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFFFFB300),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        HorizontalDivider(
            color = Color(0xFF0B0B0E),
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
