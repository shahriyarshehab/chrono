package com.example.model

import androidx.compose.ui.graphics.Color

enum class WidgetStyle(val displayName: String, val tagline: String) {
    AURA_GLASS("Aura Glass", "Translucent frosted glass with glowing celestial accents"),
    CYBER_NEON("Cyber Neon", "Futuristic HUD with glowing telemetry & cyber typography"),
    NEO_BRUTALIST("Neo Brutalist", "High-contrast bold pop aesthetic with chunky borders"),
    EDITORIAL_CHRONO("Editorial Chrono", "Luxury horology typography & minimalist Swiss layout"),
    RETRO_FLIP("Retro Flip", "Mechanical split-flap timepiece with matrix weather forecast"),
    DYNAMIC_ATMOSPHERE("Dynamic Sky", "Adaptive atmosphere reacting in real-time to live weather")
}

enum class ThemePalette(
    val displayName: String,
    val backgroundDark: Color,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val surfaceColor: Color
) {
    COSMIC_NIGHT(
        displayName = "Cosmic Night",
        backgroundDark = Color(0xFF0F172A),
        primaryAccent = Color(0xFF38BDF8),
        secondaryAccent = Color(0xFFFBBF24),
        textPrimary = Color(0xFFF8FAFC),
        textSecondary = Color(0xFF94A3B8),
        surfaceColor = Color(0x331E293B)
    ),
    SOLAR_AMBER(
        displayName = "Solar Amber",
        backgroundDark = Color(0xFF14120E),
        primaryAccent = Color(0xFFF59E0B),
        secondaryAccent = Color(0xFFFB923C),
        textPrimary = Color(0xFFFFFBEB),
        textSecondary = Color(0xFFD97706),
        surfaceColor = Color(0x33292524)
    ),
    AURORA_MINT(
        displayName = "Aurora Mint",
        backgroundDark = Color(0xFF051B17),
        primaryAccent = Color(0xFF34D399),
        secondaryAccent = Color(0xFFA7F3D0),
        textPrimary = Color(0xFFECFDF5),
        textSecondary = Color(0xFF6EE7B7),
        surfaceColor = Color(0x33064E3B)
    ),
    CYBER_MAGENTA(
        displayName = "Cyber Magenta",
        backgroundDark = Color(0xFF160A24),
        primaryAccent = Color(0xFFF43F5E),
        secondaryAccent = Color(0xFFA855F7),
        textPrimary = Color(0xFFFAF5FF),
        textSecondary = Color(0xFFC084FC),
        surfaceColor = Color(0x333B0764)
    ),
    SLATE_MINIMAL(
        displayName = "Slate Minimal",
        backgroundDark = Color(0xFF1E293B),
        primaryAccent = Color(0xFFE2E8F0),
        secondaryAccent = Color(0xFF94A3B8),
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xFFCBD5E1),
        surfaceColor = Color(0x33334155)
    ),
    ROSE_GOLD(
        displayName = "Rose Gold",
        backgroundDark = Color(0xFF1C1318),
        primaryAccent = Color(0xFFFB7185),
        secondaryAccent = Color(0xFFFDE68A),
        textPrimary = Color(0xFFFFF1F2),
        textSecondary = Color(0xFFFDA4AF),
        surfaceColor = Color(0x334C0519)
    )
}

enum class FontDesign(val displayName: String) {
    MODERN_SANS("Modern Sans"),
    LUXURY_SERIF("Luxury Serif"),
    CYBER_MONO("Cyber Monospace"),
    PLAYFUL_ROUND("Playful Rounded")
}

enum class WidgetLayoutType(val displayName: String, val dimensions: String) {
    DASHBOARD_4X2("Wide Dashboard", "4 × 2 Cells"),
    PILL_COMPACT_4X1("Compact Pill", "3 × 1 Cells"),
    RADIAL_DIAL_2X2("Radial Chrono", "2 × 2 Cells"),
    VERTICAL_STACK_2X3("Vertical Stack", "2 × 3 Cells")
}

/**
 * Customization state configuration
 */
data class WidgetCustomization(
    val style: WidgetStyle = WidgetStyle.AURA_GLASS,
    val palette: ThemePalette = ThemePalette.COSMIC_NIGHT,
    val fontDesign: FontDesign = FontDesign.MODERN_SANS,
    val layoutType: WidgetLayoutType = WidgetLayoutType.DASHBOARD_4X2,
    val is24HourFormat: Boolean = false,
    val useFahrenheit: Boolean = true,
    val showSeconds: Boolean = true,
    val showBatteryLevel: Boolean = true,
    val showLocationName: Boolean = true,
    val showFeelsLike: Boolean = true,
    val showDetailedStats: Boolean = true,
    val showMiniForecast: Boolean = true,
    val backgroundOpacity: Float = 0.85f,
    val cornerRadiusDp: Int = 24
)
