package com.mjapa21.smartwallet.domain.model

data class UserPreferences(
    val name: String,
    val monthlyIncome: Double,
    val isOnboardingComplete: Boolean
)
