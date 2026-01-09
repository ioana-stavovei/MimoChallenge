package com.example.mimochallenge.util

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

object ColorUtils {
    fun fromHex(hex: String): Color {
        return try {
            // Handles both #FFFFFF and #FFFFFFFF formats
            Color(hex.toColorInt())
        } catch (e: Exception) {
            // Default fallback if API sends invalid hex
            Color.Gray
        }
    }
}