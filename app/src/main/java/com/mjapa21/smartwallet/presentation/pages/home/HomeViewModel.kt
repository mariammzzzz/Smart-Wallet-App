package com.mjapa21.smartwallet.presentation.pages.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjapa21.smartwallet.domain.model.CardDetails
import com.mjapa21.smartwallet.domain.usecases.GetCardDetailsUseCase
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
    private val getUserUseCase: GetUserUseCase,
    private val getCardDetailsUseCase: GetCardDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState()) //todo replace this later
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            //todo replace balanceInfo!!! /recentTransactions with real data from Room
            val user = getUserUseCase()
            val card = getCardDetailsUseCase()

            _uiState.update { current ->
                current.copy(
                    userName = user.name,
                    currentDate = formatToday(),
                    balanceInfo = BalanceInfo(
                        monthlyIncome = formatCurrency(user.monthlyIncome)
                    ),
                    cardInfo = card?.toCardInfo() ?: current.cardInfo
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

    private fun CardDetails.toCardInfo(): CardInfo {
        return CardInfo(
            cardNumber = cardNumber,
            cvv = cvv,
            expiryDate = expiryDate
        )
    }
}