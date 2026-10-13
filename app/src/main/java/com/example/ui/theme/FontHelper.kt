package com.example.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.model.FontDesign

object FontHelper {
    fun getFontFamily(design: FontDesign): FontFamily {
        return when (design) {
            FontDesign.MODERN_SANS -> FontFamily.SansSerif
            FontDesign.LUXURY_SERIF -> FontFamily.Serif
            FontDesign.CYBER_MONO -> FontFamily.Monospace
            FontDesign.PLAYFUL_ROUND -> FontFamily.Cursive
        }
    }
}
