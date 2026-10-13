package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.ui.platform.LocalContext
import com.example.util.LocationProviderHelper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FontDesign
import com.example.model.ThemePalette
import com.example.model.WidgetCustomization
import com.example.model.WidgetLayoutType
import com.example.model.WidgetStyle
import com.example.ui.components.WidgetCanvasPreview
import com.example.viewmodel.WallpaperPreset
import com.example.viewmodel.WidgetStudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetStudioScreen(viewModel: WidgetStudioViewModel) {
    val customization by viewModel.customization.collectAsState()
    val weather by viewModel.currentWeather.collectAsState()
    val hourly by viewModel.hourlyForecast.collectAsState()
    val daily by viewModel.dailyForecast.collectAsState()
    val city by viewModel.selectedCity.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val isLoadingWeather by viewModel.isLoadingWeather.collectAsState()
    val liveTime by viewModel.liveTimeMillis.collectAsState()
    val batteryLevel by viewModel.batteryLevel.collectAsState()
    val isStandbyMode by viewModel.isStandbyMode.collectAsState()
    val selectedWallpaper by viewModel.selectedWallpaper.collectAsState()
    val pinMessage by viewModel.pinWidgetMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableIntStateOf(0) }
    var showCitySearch by remember { mutableStateOf(false) }
    var showPhoneSimulator by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.detectAndFetchLocationWeather(context)
        }
    }

    val onDetectLocationAction: () -> Unit = {
        if (LocationProviderHelper.hasLocationPermission(context)) {
            viewModel.detectAndFetchLocationWeather(context)
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(pinMessage) {
        pinMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearPinMessage()
        }
    }

    if (isStandbyMode) {
        StandbyModeScreen(
            timeMillis = liveTime,
            weather = weather,
            city = city,
            config = customization,
            batteryLevel = batteryLevel,
            onExit = { viewModel.setStandbyMode(false) }
        )
        return
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("widget_studio_screen"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF38BDF8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "⏱️", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Chronos Studio",
                            color = Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    // Live Ticker pulse badge
                    val transition = rememberInfiniteTransition(label = "pulse")
                    val alpha by transition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulseAlpha"
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = alpha), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981).copy(alpha = alpha))
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "LIVE",
                            color = Color(0xFF10B981),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.fetchWeatherData() },
                        modifier = Modifier.testTag("refresh_weather_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Weather",
                            tint = if (isLoadingWeather) Color(0xFF38BDF8) else Color.White
                        )
                    }
                    IconButton(
                        onClick = { viewModel.setStandbyMode(true) },
                        modifier = Modifier.testTag("open_standby_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = "Desk Standby Mode",
                            tint = Color(0xFFFBBF24)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF0B0F19)
                )
            )
        },
        containerColor = Color(0xFF0B0F19)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Pinned Live Widget Display (Always visible at top!)
            HeroPreviewStage(
                timeMillis = liveTime,
                weather = weather,
                hourly = hourly,
                city = city,
                config = customization,
                batteryLevel = batteryLevel,
                selectedWallpaper = selectedWallpaper,
                onSelectWallpaper = { viewModel.setWallpaper(it) },
                onOpenSimulator = { showPhoneSimulator = true },
                onPinWidget = {
                    val isDashboard = customization.layoutType == WidgetLayoutType.DASHBOARD_4X2
                    viewModel.requestPinWidget(isDashboard)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Navigation Tabs for Customizer (Pinned)
            val tabs = listOf(
                "Styles",
                "Themes",
                "Typography",
                "Layout",
                "Weather"
            )

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF111827),
                contentColor = Color(0xFF38BDF8),
                edgePadding = 16.dp,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == index) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                            )
                        },
                        modifier = Modifier.testTag("tab_${title.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Scrollable Tab Options Container (Only options scroll down; display stays visible!)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                when (selectedTab) {
                    0 -> StylesTabContent(
                        currentStyle = customization.style,
                        onSelectStyle = { viewModel.setStyle(it) }
                    )
                    1 -> ThemesTabContent(
                        config = customization,
                        onSelectPalette = { viewModel.setPalette(it) },
                        onUpdateOpacity = { viewModel.updateCustomization { c -> c.copy(backgroundOpacity = it) } },
                        onUpdateRadius = { viewModel.updateCustomization { c -> c.copy(cornerRadiusDp = it) } }
                    )
                    2 -> TypographyTabContent(
                        currentFont = customization.fontDesign,
                        onSelectFont = { viewModel.setFont(it) }
                    )
                    3 -> LayoutTabContent(
                        config = customization,
                        onSelectLayout = { viewModel.setLayout(it) },
                        onToggle = { key, value ->
                            viewModel.updateCustomization { c ->
                                when (key) {
                                    "24h" -> c.copy(is24HourFormat = value)
                                    "f" -> c.copy(useFahrenheit = value)
                                    "sec" -> c.copy(showSeconds = value)
                                    "bat" -> c.copy(showBatteryLevel = value)
                                    "loc" -> c.copy(showLocationName = value)
                                    "feels" -> c.copy(showFeelsLike = value)
                                    "stats" -> c.copy(showDetailedStats = value)
                                    else -> c
                                }
                            }
                        }
                    )
                    4 -> WeatherTabContent(
                        weather = weather,
                        hourly = hourly,
                        daily = daily,
                        city = city,
                        useFahrenheit = customization.useFahrenheit,
                        isOwmConfigured = viewModel.isOwmConfigured(),
                        onDetectLocation = onDetectLocationAction,
                        onChangeCity = { showCitySearch = true }
                    )
                }
            }
        }
    }

    if (showCitySearch) {
        CitySearchDialog(
            currentCity = city,
            searchResults = searchResults,
            isSearching = isSearching,
            onSearch = { viewModel.searchCities(it) },
            onSelectCity = { viewModel.selectCity(it) },
            onDetectLocation = onDetectLocationAction,
            onDismiss = {
                showCitySearch = false
                viewModel.clearCitySearch()
            }
        )
    }

    if (showPhoneSimulator) {
        HomeScreenSimulatorDialog(
            timeMillis = liveTime,
            weather = weather,
            hourly = hourly,
            city = city,
            config = customization,
            batteryLevel = batteryLevel,
            selectedWallpaper = selectedWallpaper,
            onSelectWallpaper = { viewModel.setWallpaper(it) },
            onPinWidget = {
                val isDashboard = customization.layoutType == WidgetLayoutType.DASHBOARD_4X2
                viewModel.requestPinWidget(isDashboard)
            },
            onDismiss = { showPhoneSimulator = false }
        )
    }
}

@Composable
private fun HeroPreviewStage(
    timeMillis: Long,
    weather: com.example.model.CurrentWeather,
    hourly: List<com.example.model.HourlyForecast>,
    city: com.example.model.CityLocation,
    config: WidgetCustomization,
    batteryLevel: Int,
    selectedWallpaper: WallpaperPreset,
    onSelectWallpaper: (WallpaperPreset) -> Unit,
    onOpenSimulator: () -> Unit,
    onPinWidget: () -> Unit
) {
    val wallpaperBrush = when (selectedWallpaper) {
        WallpaperPreset.COSMIC_AURORA -> Brush.verticalGradient(
            listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF0F172A))
        )
        WallpaperPreset.ALPINE_SUNSET -> Brush.verticalGradient(
            listOf(Color(0xFF31102A), Color(0xFF6B1D4B), Color(0xFF1A1325))
        )
        WallpaperPreset.CYBER_NIGHT -> Brush.verticalGradient(
            listOf(Color(0xFF09061A), Color(0xFF1B0B33), Color(0xFF09061A))
        )
        WallpaperPreset.MINIMAL_OBSIDIAN -> Brush.verticalGradient(
            listOf(Color(0xFF131316), Color(0xFF1A1A1E), Color(0xFF131316))
        )
        WallpaperPreset.PASTEL_DAWN -> Brush.verticalGradient(
            listOf(Color(0xFF4C1D95), Color(0xFF831843), Color(0xFF1E1B4B))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
    ) {
        // Live Canvas Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(wallpaperBrush)
                .border(1.5.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(22.dp))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            WidgetCanvasPreview(
                timeMillis = timeMillis,
                weather = weather,
                hourly = hourly,
                city = city,
                config = config,
                batteryLevel = batteryLevel
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Actions Row: Wallpaper selector & Pin Widget
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onOpenSimulator,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF38BDF8)
                ),
                modifier = Modifier.testTag("preview_simulator_button")
            ) {
                Icon(
                    imageVector = Icons.Default.PhoneAndroid,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Device Simulator", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onPinWidget,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF38BDF8)
                ),
                modifier = Modifier.testTag("pin_widget_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Dashboard,
                    contentDescription = null,
                    tint = Color(0xFF0F172A),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Pin to Home Screen",
                    color = Color(0xFF0F172A),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun StylesTabContent(
    currentStyle: WidgetStyle,
    onSelectStyle: (WidgetStyle) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "CHOOSE WIDGET ARCHETYPE",
            color = Color(0xFF64748B),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        WidgetStyle.values().forEach { style ->
            val isSelected = style == currentStyle
            val iconEmoji = when (style) {
                WidgetStyle.AURA_GLASS -> "✨"
                WidgetStyle.CYBER_NEON -> "⚡"
                WidgetStyle.NEO_BRUTALIST -> "🟡"
                WidgetStyle.EDITORIAL_CHRONO -> "🕰️"
                WidgetStyle.RETRO_FLIP -> "📟"
                WidgetStyle.DYNAMIC_ATMOSPHERE -> "🌤️"
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onSelectStyle(style) }
                    .border(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) Color(0xFF38BDF8) else Color(0xFF1E293B),
                        RoundedCornerShape(16.dp)
                    )
                    .testTag("style_${style.name.lowercase()}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF1E293B) else Color(0xFF131B2E)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = iconEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = style.displayName,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF38BDF8).copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                        Text(
                            text = style.tagline,
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color(0xFF38BDF8)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemesTabContent(
    config: WidgetCustomization,
    onSelectPalette: (ThemePalette) -> Unit,
    onUpdateOpacity: (Float) -> Unit,
    onUpdateRadius: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "COLOR PALETTE",
            color = Color(0xFF64748B),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ThemePalette.values().take(3).forEach { pal ->
                val isSel = pal == config.palette
                PaletteCard(palette = pal, isSelected = isSel, onClick = { onSelectPalette(pal) }, modifier = Modifier.weight(1f))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ThemePalette.values().drop(3).take(3).forEach { pal ->
                val isSel = pal == config.palette
                PaletteCard(palette = pal, isSelected = isSel, onClick = { onSelectPalette(pal) }, modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Surface Sliders
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Glass Opacity Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Glass Opacity", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "${(config.backgroundOpacity * 100).toInt()}%", color = Color(0xFF38BDF8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = config.backgroundOpacity,
                    onValueChange = onUpdateOpacity,
                    valueRange = 0.35f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF38BDF8),
                        activeTrackColor = Color(0xFF38BDF8)
                    ),
                    modifier = Modifier.testTag("slider_opacity")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Corner Radius Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Corner Radius", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "${config.cornerRadiusDp} dp", color = Color(0xFF38BDF8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = config.cornerRadiusDp.toFloat(),
                    onValueChange = { onUpdateRadius(it.toInt()) },
                    valueRange = 12f..36f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF38BDF8),
                        activeTrackColor = Color(0xFF38BDF8)
                    ),
                    modifier = Modifier.testTag("slider_radius")
                )
            }
        }
    }
}

@Composable
private fun PaletteCard(
    palette: ThemePalette,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF131B2E))
            .border(
                if (isSelected) 2.dp else 1.dp,
                if (isSelected) Color(0xFF38BDF8) else Color(0xFF1E293B),
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(10.dp)
            .testTag("palette_${palette.name.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(palette.primaryAccent))
                Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(palette.secondaryAccent))
                Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(palette.backgroundDark))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = palette.displayName,
                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun TypographyTabContent(
    currentFont: FontDesign,
    onSelectFont: (FontDesign) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "CLOCK TYPOGRAPHY DESIGN",
            color = Color(0xFF64748B),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        FontDesign.values().forEach { font ->
            val isSel = font == currentFont
            val sampleText = when (font) {
                FontDesign.MODERN_SANS -> "10:30 • Clean Neo-Grotesque"
                FontDesign.LUXURY_SERIF -> "10:30 • Luxury Swiss Chrono"
                FontDesign.CYBER_MONO -> "10:30 • Tech HUD Monospace"
                FontDesign.PLAYFUL_ROUND -> "10:30 • Soft Organic Curves"
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onSelectFont(font) }
                    .border(
                        if (isSel) 2.dp else 1.dp,
                        if (isSel) Color(0xFF38BDF8) else Color(0xFF1E293B),
                        RoundedCornerShape(14.dp)
                    )
                    .testTag("font_${font.name.lowercase()}"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = font.displayName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = sampleText, color = Color(0xFF94A3B8), fontSize = 12.sp)
                    }
                    if (isSel) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Active", tint = Color(0xFF38BDF8))
                    }
                }
            }
        }
    }
}

@Composable
private fun LayoutTabContent(
    config: WidgetCustomization,
    onSelectLayout: (WidgetLayoutType) -> Unit,
    onToggle: (String, Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "WIDGET LAYOUT DIMENSIONS",
            color = Color(0xFF64748B),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WidgetLayoutType.values().forEach { layout ->
                val isSel = layout == config.layoutType
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSel) Color(0xFF0284C7) else Color(0xFF131B2E))
                        .clickable { onSelectLayout(layout) }
                        .padding(vertical = 10.dp, horizontal = 4.dp)
                        .testTag("layout_${layout.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = layout.displayName.split(" ").first(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = layout.dimensions,
                            color = if (isSel) Color(0xFFE0F2FE) else Color(0xFF94A3B8),
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "FEATURE TOGGLES",
            color = Color(0xFF64748B),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ToggleRow(label = "24-Hour Time Format", value = config.is24HourFormat, onToggle = { onToggle("24h", it) }, tag = "toggle_24h")
                ToggleRow(label = "Fahrenheit Units (°F / °C)", value = config.useFahrenheit, onToggle = { onToggle("f", it) }, tag = "toggle_fahrenheit")
                ToggleRow(label = "Show Seconds Live Pulse", value = config.showSeconds, onToggle = { onToggle("sec", it) }, tag = "toggle_seconds")
                ToggleRow(label = "Show Battery Telemetry", value = config.showBatteryLevel, onToggle = { onToggle("bat", it) }, tag = "toggle_battery")
                ToggleRow(label = "Show Location Badge", value = config.showLocationName, onToggle = { onToggle("loc", it) }, tag = "toggle_location")
                ToggleRow(label = "Show Feels-Like & H/L", value = config.showFeelsLike, onToggle = { onToggle("feels", it) }, tag = "toggle_feels_like")
                ToggleRow(label = "Show Humidity & Wind Stats", value = config.showDetailedStats, onToggle = { onToggle("stats", it) }, tag = "toggle_stats")
            }
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    value: Boolean,
    onToggle: (Boolean) -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Switch(
            checked = value,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF38BDF8)
            ),
            modifier = Modifier.testTag(tag)
        )
    }
}

@Composable
private fun WeatherTabContent(
    weather: com.example.model.CurrentWeather,
    hourly: List<com.example.model.HourlyForecast>,
    daily: List<com.example.model.DailyForecast>,
    city: com.example.model.CityLocation,
    useFahrenheit: Boolean,
    isOwmConfigured: Boolean,
    onDetectLocation: () -> Unit,
    onChangeCity: () -> Unit
) {
    val tempVal = if (useFahrenheit) (weather.temperatureC * 9 / 5 + 32).toInt() else weather.temperatureC.toInt()
    val feelsVal = if (useFahrenheit) (weather.feelsLikeC * 9 / 5 + 32).toInt() else weather.feelsLikeC.toInt()

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // OpenWeatherMap API Provider Status Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (isOwmConfigured) Color(0xFF064E3B) else Color(0xFF1E293B))
                .border(
                    1.dp,
                    if (isOwmConfigured) Color(0xFF10B981) else Color(0xFF334155),
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isOwmConfigured) Color(0xFF34D399) else Color(0xFF38BDF8))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isOwmConfigured) "⚡ OpenWeatherMap API (Live Connected)" else "🌦️ OpenWeatherMap API (Ready in Secrets)",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = if (isOwmConfigured) "Active" else "Auto Fallback",
                color = if (isOwmConfigured) Color(0xFF34D399) else Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // City Header Card with GPS & Search Actions
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "CURRENT LOCATION", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(text = city.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = city.country, color = Color(0xFF94A3B8), fontSize = 12.sp)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onDetectLocation,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("detect_gps_location_button")
                        ) {
                            Icon(imageVector = Icons.Default.MyLocation, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "GPS", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onChangeCity,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("change_city_button")
                        ) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Search", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Live Conditions Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WeatherStatBox("Temperature", "$tempVal°", weather.conditionEmoji, Modifier.weight(1f))
            WeatherStatBox("Feels Like", "$feelsVal°", "🌡️", Modifier.weight(1f))
            WeatherStatBox("Humidity", "${weather.humidityPercent}%", "💧", Modifier.weight(1f))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WeatherStatBox("Wind Speed", "${weather.windSpeedKmh.toInt()} km/h", "💨", Modifier.weight(1f))
            WeatherStatBox("Rain Precip", "${weather.precipitationMm} mm", "🌧️", Modifier.weight(1f))
            WeatherStatBox("UV Index", "${weather.uvIndex.toInt()}", "☀️", Modifier.weight(1f))
        }

        // Hourly forecast
        if (hourly.isNotEmpty()) {
            Text(
                text = "12-HOUR HOURLY OUTLOOK",
                color = Color(0xFF64748B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(hourly) { item ->
                    val hTemp = if (useFahrenheit) (item.tempC * 9 / 5 + 32).toInt() else item.tempC.toInt()
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF131B2E))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = item.timeLabel, color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = item.emoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "$hTemp°", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 5-day daily forecast
        if (daily.isNotEmpty()) {
            Text(
                text = "5-DAY FORECAST",
                color = Color(0xFF64748B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    daily.forEach { item ->
                        val maxT = if (useFahrenheit) (item.maxTempC * 9 / 5 + 32).toInt() else item.maxTempC.toInt()
                        val minT = if (useFahrenheit) (item.minTempC * 9 / 5 + 32).toInt() else item.minTempC.toInt()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = item.dayLabel, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.width(70.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = item.emoji, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = item.conditionDesc, color = Color(0xFF94A3B8), fontSize = 12.sp)
                            }
                            Text(text = "$maxT° / $minT°", color = Color(0xFF38BDF8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherStatBox(
    title: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF131B2E))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = title, color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                Text(text = icon, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}
