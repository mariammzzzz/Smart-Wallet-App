package com.mjapa21.smartwallet.domain.model

data class UserPreferences( //todo make similar to this data class in the data/model layer too and map in the repository
    val name: String,
    val monthlyIncome: Double,
    val isOnboardingComplete: Boolean
) {
    companion object {
        val EMPTY = UserPreferences(
            name = "",
            monthlyIncome = 0.0,
            isOnboardingComplete = false
        )
    }
}
