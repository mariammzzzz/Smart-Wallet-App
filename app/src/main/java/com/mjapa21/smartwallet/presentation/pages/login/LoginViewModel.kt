package com.mjapa21.smartwallet.presentation.pages.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjapa21.smartwallet.domain.model.UserPreferences
import com.mjapa21.smartwallet.domain.usecases.SaveUserUseCase
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

//TODO: need to replace with real use case once the domain layer is wired up
class LoginViewModel(private val saveUserUseCase: SaveUserUseCase) : ViewModel() {

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
                // TODO: call loginUserUseCase(
                //     name = state.name,
                //     cardNumber = state.cardNumber,
                //     cvv = state.cvv,
                //     expiryDate = state.expiryDate,
                //     monthlyIncome = state.monthlyIncome
                // )
                // This is where card -> Room and name/income -> DataStore will happen
                saveUserUseCase(
                    userDetails = UserPreferences(
                        name = state.name,
                        monthlyIncome = state.monthlyIncome.toDouble(),
                        isOnboardingComplete = true
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
            state.expiryDate.length != EXPIRY_DATE_LENGTH -> "Enter expiry date as MM/YY"
            state.monthlyIncome.isBlank() || state.monthlyIncome.toDoubleOrNull() == null -> "Please enter your monthly income"
            else -> null
        }
    }


    companion object {
        const val CARD_NUMBER_LENGTH = 16
        const val CVV_LENGTH = 3
        const val EXPIRY_DATE_LENGTH = 4
    }
}