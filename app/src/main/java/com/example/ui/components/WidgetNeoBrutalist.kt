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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CityLocation
import com.example.model.CurrentWeather
import com.example.model.WidgetCustomization
import com.example.model.WidgetLayoutType

@Composable
fun WidgetNeoBrutalist(
    timeInfo: FormattedTime,
    weather: CurrentWeather,
    city: CityLocation,
    config: WidgetCustomization,
    modifier: Modifier = Modifier
) {
    val yellowPop = Color(0xFFFFDE59)
    val pinkPop = Color(0xFFFF5757)
    val cardBg = Color(0xFFFFFDF0)
    val shape = RoundedCornerShape(16.dp)

    val tempVal = if (config.useFahrenheit) {
        (weather.temperatureC * 9 / 5 + 32).toInt()
    } else {
        weather.temperatureC.toInt()
    }

    Box(modifier = modifier.testTag("widget_neo_brutalist")) {
        // Hard black drop shadow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = 5.dp, y = 5.dp)
                .clip(shape)
                .background(Color.Black)
        )

        // Main card with thick black border
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(cardBg)
                .border(2.5.dp, Color.Black, shape)
                .padding(16.dp)
        ) {
            when (config.layoutType) {
                WidgetLayoutType.DASHBOARD_4X2 -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(yellowPop)
                                    .border(1.5.dp, Color.Black, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "★ ${city.name.uppercase()}",
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .border(1.5.dp, Color.Black, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = timeInfo.fullDate.uppercase(),
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Middle: Clock and Temperature sticker
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = timeInfo.fullTime,
                                        color = Color.Black,
                                        fontSize = 46.sp,
                                        fontWeight = FontWeight.Black,
                                        lineHeight = 44.sp
                                    )
                                    if (config.showSeconds) {
                                        Text(
                                            text = ":${timeInfo.seconds}",
                                            color = pinkPop,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                                        )
                                    }
                                }
                            }

                            // Weather sticker
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(pinkPop)
                                    .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = weather.conditionEmoji, fontSize = 28.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "$tempVal°",
                                        color = Color.White,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }

                        // Bottom status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "STATUS: ${weather.conditionDescription.uppercase()}",
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "HUM: ${weather.humidityPercent}% • WIND: ${weather.windSpeedKmh.toInt()}KM/H",
                                color = Color.DarkGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
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
                            Text(
                                text = timeInfo.fullTime,
                                color = Color.Black,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = " • ${timeInfo.dayOfWeek.take(3).uppercase()}",
                                color = Color.DarkGray,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(yellowPop)
                                .border(1.5.dp, Color.Black, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${weather.conditionEmoji} $tempVal°",
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
