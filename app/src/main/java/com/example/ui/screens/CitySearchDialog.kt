package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.CityLocation
import com.example.repository.WeatherRepository

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CitySearchDialog(
    currentCity: CityLocation,
    searchResults: List<CityLocation>,
    isSearching: Boolean,
    onSearch: (String) -> Unit,
    onSelectCity: (CityLocation) -> Unit,
    onDetectLocation: () -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("city_search_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1E293B)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Select Location",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Currently: ${currentCity.name}, ${currentCity.country}",
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
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // GPS Location Detection Button
                Button(
                    onClick = {
                        onDetectLocation()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("use_gps_location_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Detect My Current Location (GPS)",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        onSearch(it)
                    },
                    placeholder = { Text("Search city (e.g. Tokyo, London...)", color = Color(0xFF64748B), fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF38BDF8)
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("city_search_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Popular Cities Quick Chips
                Text(
                    text = "POPULAR CITIES",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    WeatherRepository.DEFAULT_CITIES.forEach { city ->
                        val isSelected = city.name == currentCity.name
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) Color(0xFF0284C7) else Color(0xFF334155))
                                .clickable {
                                    onSelectCity(city)
                                    onDismiss()
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = city.name,
                                color = if (isSelected) Color.White else Color(0xFFE2E8F0),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Results
                if (isSearching) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF38BDF8), modifier = Modifier.size(28.dp))
                    }
                } else if (searchResults.isNotEmpty()) {
                    Text(
                        text = "SEARCH RESULTS",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        items(searchResults) { city ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        onSelectCity(city)
                                        onDismiss()
                                    }
                                    .padding(vertical = 10.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = city.name,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = city.country,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
