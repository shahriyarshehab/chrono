package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.WidgetCustomization
import com.example.ui.components.TimeFormatHelper
import com.example.ui.theme.FontHelper

@Composable
fun StandbyModeScreen(
    timeMillis: Long,
    weather: CurrentWeather,
    city: CityLocation,
    config: WidgetCustomization,
    batteryLevel: Int,
    onExit: () -> Unit
) {
    BackHandler { onExit() }

    var brightness by remember { mutableFloatStateOf(0.9f) }
    val timeInfo = TimeFormatHelper.format(timeMillis, config.is24HourFormat)
    val fontFamily = FontHelper.getFontFamily(config.fontDesign)

    val tempVal = if (config.useFahrenheit) {
        (weather.temperatureC * 9 / 5 + 32).toInt()
    } else {
        weather.temperatureC.toInt()
    }

    val transition = rememberInfiniteTransition(label = "standby_glow")
    val pulse by transition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("standby_mode_screen")
            .background(Color.Black)
    ) {
        // Ambient background glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width * 0.5f, size.height * 0.45f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        config.palette.primaryAccent.copy(alpha = 0.25f * brightness * pulse),
                        config.palette.secondaryAccent.copy(alpha = 0.1f * brightness),
                        Color.Transparent
                    ),
                    center = center,
                    radius = size.width * 0.7f
                ),
                radius = size.width * 0.7f,
                center = center
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.6f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "📍 ${city.name}",
                        color = Color.White.copy(alpha = 0.8f * brightness),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "⚡ $batteryLevel%",
                        color = config.palette.primaryAccent.copy(alpha = brightness),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onExit,
                    modifier = Modifier
                        .testTag("exit_standby_button")
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit Standby",
                        tint = Color.White
                    )
                }
            }

            // Giant Center Clock
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = timeInfo.fullTime,
                        color = Color.White.copy(alpha = brightness),
                        fontSize = 82.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = fontFamily,
                        letterSpacing = (-2).sp,
                        lineHeight = 80.sp
                    )
                    if (config.showSeconds) {
                        Text(
                            text = ":${timeInfo.seconds}",
                            color = config.palette.primaryAccent.copy(alpha = brightness),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = fontFamily,
                            modifier = Modifier.padding(bottom = 12.dp, start = 6.dp)
                        )
                    }
                }

                Text(
                    text = timeInfo.fullDate,
                    color = Color.White.copy(alpha = 0.7f * brightness),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = fontFamily
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Weather Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.7f * brightness))
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(text = weather.conditionEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "$tempVal°",
                        color = Color.White.copy(alpha = brightness),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${weather.conditionDescription}",
                        color = Color.White.copy(alpha = 0.75f * brightness),
                        fontSize = 16.sp,
                        fontFamily = fontFamily
                    )
                }
            }

            // Bottom Dimming Controller
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.7f))
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DarkMode,
                        contentDescription = "Dim",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Nightstand Dimmer: ${(brightness * 100).toInt()}%",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Default.LightMode,
                        contentDescription = "Bright",
                        tint = config.palette.primaryAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Slider(
                    value = brightness,
                    onValueChange = { brightness = it },
                    valueRange = 0.15f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = config.palette.primaryAccent,
                        activeTrackColor = config.palette.primaryAccent,
                        inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("standby_dimmer_slider")
                )
            }
        }
    }
}
