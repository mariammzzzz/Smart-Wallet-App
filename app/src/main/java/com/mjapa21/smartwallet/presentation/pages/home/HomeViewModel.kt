package com.mjapa21.smartwallet.presentation.pages.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjapa21.smartwallet.domain.usecases.GetUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel(
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState.mock()) //todo replace this later
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            //todo replace with real use cases and data fetching from db
            val user = getUserUseCase()

            _uiState.update { current ->
                current.copy(
                    userName = user.name,
                    currentDate = formatToday(),
                    balanceInfo = current.balanceInfo.copy(
                        monthlyIncome = formatCurrency(user.monthlyIncome)
                    )
                )
            }
        }
    }

    private fun formatToday(): String {
        val formatter = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
        return formatter.format(Date())
    }

    private fun formatCurrency(amount: Double): String {
        return "₾" + "%,.2f".format(Locale.US, amount)
    }
}