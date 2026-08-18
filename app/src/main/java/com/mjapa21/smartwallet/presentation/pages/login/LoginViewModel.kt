package com.mjapa21.smartwallet.presentation.pages.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjapa21.smartwallet.domain.model.CardDetails
import com.mjapa21.smartwallet.domain.model.UserPreferences
import com.mjapa21.smartwallet.domain.usecases.SaveUserWithCardUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

/**
 * everything the LoginScreen needs to render itself, in one immutable snapshot
 */
data class LoginUiState(
    val name: String = "",
    val cardNumber: String = "",
    val cvv: String = "",
    val expiryDate: String = "",
    val monthlyIncome: String = "",
    val isLoading: Boolean = false
)


/**
 * One-time occurrences the screen should react to exactly once — a Toast, a
 * navigation trigger. An event describes "this
 * happened" and should fire once, then be gone (unlike state).
 */
sealed interface LoginEvent {
    data class ShowError(val message: String) : LoginEvent
    data object NavigateToHome : LoginEvent
}


class LoginViewModel(private val saveUserWithCardUseCase: SaveUserWithCardUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = Channel<LoginEvent>()
    val events: Flow<LoginEvent> = _events.receiveAsFlow()

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun onCardNumberChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }.take(CARD_NUMBER_LENGTH)
        _uiState.update { it.copy(cardNumber = digitsOnly) }
    }

    fun onCvvChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }.take(CVV_LENGTH)
        _uiState.update { it.copy(cvv = digitsOnly) }
    }

    fun onExpiryDateChange(value: String) {
        // raw digits only — the "/" is added at display time by ExpiryDateVisualTransformation
        val digitsOnly = value.filter { it.isDigit() }.take(EXPIRY_DATE_LENGTH)
        _uiState.update { it.copy(expiryDate = digitsOnly) }
    }

    fun onMonthlyIncomeChange(value: String) {
        val filtered = value.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(monthlyIncome = filtered) }
    }

    fun onSubmit() {
        val state = _uiState.value

        val validationError = validate(state)
        if (validationError != null) {
            viewModelScope.launch { _events.send(LoginEvent.ShowError(validationError)) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val income = state.monthlyIncome.toDoubleOrNull() ?: 0.0

                saveUserWithCardUseCase(
                    userDetails = UserPreferences(
                        name = state.name,
                        monthlyIncome = income,
                        isOnboardingComplete = true // forced again inside the use case regardless
                    ),
                    card = CardDetails(
                        cardNumber = state.cardNumber,
                        cvv = state.cvv,
                        expiryDate = state.expiryDate
                    )
                )
                _uiState.update { it.copy(isLoading = false) }
                _events.send(LoginEvent.NavigateToHome)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _events.send(
                    LoginEvent.ShowError(
                        e.message ?: "Something went wrong. Please try again."
                    )
                )
            }
        }
    }

    private fun validate(state: LoginUiState): String? {
        return when {
            state.name.isBlank() -> "Please enter your name"
            state.cardNumber.length < CARD_NUMBER_LENGTH -> "Card number must contain $CARD_NUMBER_LENGTH digits"
            state.cvv.length < CVV_LENGTH -> "Enter a valid CVV"
            state.monthlyIncome.isBlank() || state.monthlyIncome.toDoubleOrNull() == null -> "Please enter your monthly income"
            else -> validateExpiryDate(state.expiryDate)
        }
    }

    private fun validateExpiryDate(expiryDate: String): String? {
        if (expiryDate.length != EXPIRY_DATE_LENGTH) return "Enter expiry date as MM/YY"
        val month = expiryDate.substring(0, 2).toIntOrNull()

        if (month == null || month !in 1..12) {
            return "Enter a valid month in expiry date"
        }
        return null
    }


    companion object {
        const val CARD_NUMBER_LENGTH = 16
        const val CVV_LENGTH = 3
        const val EXPIRY_DATE_LENGTH = 4
    }
}