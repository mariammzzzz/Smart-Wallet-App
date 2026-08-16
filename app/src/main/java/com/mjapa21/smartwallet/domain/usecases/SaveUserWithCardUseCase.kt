package com.mjapa21.smartwallet.domain.usecases

import com.mjapa21.smartwallet.domain.model.CardDetails
import com.mjapa21.smartwallet.domain.model.UserPreferences
import com.mjapa21.smartwallet.domain.repository.CardRepository
import com.mjapa21.smartwallet.domain.repository.UserPreferencesRepository

class SaveUserWithCardUseCase(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val cardRepository: CardRepository
) {
    suspend operator fun invoke(userDetails: UserPreferences, card: CardDetails) {
        cardRepository.saveCard(card)
        userPreferencesRepository.saveUserPreferences(userDetails.copy(isOnboardingComplete = true))
    }
}