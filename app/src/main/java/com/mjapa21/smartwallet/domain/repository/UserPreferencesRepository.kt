package com.mjapa21.smartwallet.domain.repository

import com.mjapa21.smartwallet.domain.model.UserPreferences

interface UserPreferencesRepository {
    suspend fun getUserPreferences(): UserPreferences

    suspend fun saveUserPreferences(userDetails: UserPreferences)
}