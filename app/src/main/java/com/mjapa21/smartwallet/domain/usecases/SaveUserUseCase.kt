package com.mjapa21.smartwallet.domain.usecases

import com.mjapa21.smartwallet.domain.model.UserPreferences
import com.mjapa21.smartwallet.domain.repository.UserPreferencesRepository

class SaveUserUseCase(private val userPreferencesRepository: UserPreferencesRepository) {
    suspend operator fun invoke(userDetails: UserPreferences) {
        userPreferencesRepository.saveUserPreferences(userDetails)
    }
}