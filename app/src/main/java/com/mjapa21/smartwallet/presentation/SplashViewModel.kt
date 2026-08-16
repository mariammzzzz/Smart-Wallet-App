package com.mjapa21.smartwallet.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjapa21.smartwallet.domain.usecases.GetUserUseCase
import com.mjapa21.smartwallet.presentation.navigation.Destinations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class SplashViewModel(private val getUserUseCase: GetUserUseCase) : ViewModel() {
    private val _startDestination = MutableStateFlow<Destinations?>(value = null)
    val startDestination: StateFlow<Destinations?> = _startDestination.asStateFlow()


    init {
        viewModelScope.launch {
            try {
                val userPreferences = getUserUseCase()
                if (userPreferences.isOnboardingComplete) {
                    _startDestination.value = Destinations.Home
                } else {
                    _startDestination.value = Destinations.Login
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _startDestination.value = Destinations.Login
            }
        }
    }
}