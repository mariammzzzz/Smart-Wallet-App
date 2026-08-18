package com.mjapa21.smartwallet.presentation.pages.shared.ui

import androidx.compose.ui.graphics.Color
import kotlin.math.abs

val avatarPalette = listOf(
    Color(0xFFFFE0B2), Color(0xFFC8E6C9), Color(0xFFBBDEFB),
    Color(0xFFF8BBD0), Color(0xFFD1C4E9), Color(0xFFB2EBF2)
)

fun avatarColorFor(seed: String): Color {
    val index = abs(seed.hashCode()) % avatarPalette.size
    return avatarPalette[index]
}