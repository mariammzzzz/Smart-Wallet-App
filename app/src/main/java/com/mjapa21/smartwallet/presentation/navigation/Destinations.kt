package com.mjapa21.smartwallet.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destinations : NavKey {
    @Serializable
    data object Login : Destinations

    @Serializable
    data object Home : Destinations


}