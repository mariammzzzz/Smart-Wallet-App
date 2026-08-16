package com.mjapa21.smartwallet.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mjapa21.smartwallet.domain.model.UserPreferences
import com.mjapa21.smartwallet.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserPreferencesRepositoryImpl(private val dataStore: DataStore<Preferences>) :
    UserPreferencesRepository {
    override suspend fun getUserPreferences(): UserPreferences {
        return dataStore.data.map { preferences ->
            val name = preferences[USER_NAME_KEY] ?: ""
            val monthlyIncome = preferences[USER_MONTHLY_INCOME_KEY] ?: 0.0
            val isOnboardingComplete = preferences[USER_ONBOARDING_COMPLETE_KEY] ?: false
            UserPreferences(name, monthlyIncome, isOnboardingComplete)
        }.first()
    }

    override suspend fun saveUserPreferences(userDetails: UserPreferences) {
        dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = userDetails.name
            preferences[USER_MONTHLY_INCOME_KEY] = userDetails.monthlyIncome
            preferences[USER_ONBOARDING_COMPLETE_KEY] = userDetails.isOnboardingComplete
        }
    }


    companion object {
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val USER_MONTHLY_INCOME_KEY = doublePreferencesKey("user_monthly_income")
        val USER_ONBOARDING_COMPLETE_KEY = booleanPreferencesKey("user_onboarding_complete")
    }
}