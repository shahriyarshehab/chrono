package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.CityLocation
import com.example.model.CurrentWeather
import com.example.model.HourlyForecast
import com.example.model.WidgetCustomization
import com.example.ui.components.WidgetCanvasPreview
import com.example.viewmodel.WallpaperPreset

@Composable
fun HomeScreenSimulatorDialog(
    timeMillis: Long,
    weather: CurrentWeather,
    hourly: List<HourlyForecast>,
    city: CityLocation,
    config: WidgetCustomization,
    batteryLevel: Int,
    selectedWallpaper: WallpaperPreset,
    onSelectWallpaper: (WallpaperPreset) -> Unit,
    onPinWidget: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_screen_simulator_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0F172A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Home Screen Simulator",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Preview widget placed on your device launcher",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Wallpaper selector chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WallpaperPreset.values().take(4).forEach { wp ->
                        val isSel = wp == selectedWallpaper
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) Color(0xFF38BDF8) else Color(0xFF1E293B))
                                .clickable { onSelectWallpaper(wp) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = wp.title.split(" ").first(),
                                color = if (isSel) Color.Black else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Simulated Phone Screen
                val wallpaperBrush = when (selectedWallpaper) {
                    WallpaperPreset.COSMIC_AURORA -> Brush.verticalGradient(
                        listOf(Color(0xFF0B132B), Color(0xFF1C2541), Color(0xFF3A506B), Color(0xFF1C2541))
                    )
                    WallpaperPreset.ALPINE_SUNSET -> Brush.verticalGradient(
                        listOf(Color(0xFF2B0938), Color(0xFF6B114D), Color(0xFFD44A48), Color(0xFFF29C38))
                    )
                    WallpaperPreset.CYBER_NIGHT -> Brush.verticalGradient(
                        listOf(Color(0xFF0D0221), Color(0xFF0F084B), Color(0xFF261447))
                    )
                    WallpaperPreset.MINIMAL_OBSIDIAN -> Brush.verticalGradient(
                        listOf(Color(0xFF121214), Color(0xFF1C1C20), Color(0xFF141416))
                    )
                    WallpaperPreset.PASTEL_DAWN -> Brush.verticalGradient(
                        listOf(Color(0xFFE0C3FC), Color(0xFF8EC5FC))
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(wallpaperBrush)
                        .border(2.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(28.dp))
                        .padding(14.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Phone status bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "9:41",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "5G • 100%",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // The Widget Preview
                        WidgetCanvasPreview(
                            timeMillis = timeMillis,
                            weather = weather,
                            hourly = hourly,
                            city = city,
                            config = config,
                            batteryLevel = batteryLevel,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Simulated app dock icons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            listOf("📞", "💬", "🌐", "📷").forEach { icon ->
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = icon, fontSize = 18.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pin action
                Button(
                    onClick = {
                        onPinWidget()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("pin_widget_from_simulator"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
                ) {
                    Text(
                        text = "Add This Widget to Home Screen",
                        color = Color(0xFF0F172A),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
