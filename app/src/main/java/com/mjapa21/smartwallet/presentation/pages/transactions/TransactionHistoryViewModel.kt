package com.mjapa21.smartwallet.presentation.pages.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjapa21.smartwallet.domain.model.TransactionDetails
import com.mjapa21.smartwallet.domain.usecases.GetTransactionsUseCase
import com.mjapa21.smartwallet.presentation.pages.shared.format.formatSignedAmount
import com.mjapa21.smartwallet.presentation.pages.shared.format.toShortDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionHistoryViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionHistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeTransactions()
    }

    private fun observeTransactions() {
        viewModelScope.launch {
            getTransactionsUseCase()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { _uiState.update { it.copy(isLoading = false) } }
                .collect { transactions ->
                    val sorted = transactions.sortedByDescending { it.date }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            transactions = sorted.map(::toUiModel)
                        )
                    }
                }
        }
    }

    private fun toUiModel(details: TransactionDetails): TransactionUiModel {
        return TransactionUiModel(
            id = details.id,
            title = details.name,
            date = details.date.toShortDate(),
            amount = details.amount.formatSignedAmount(),
            isExpense = details.amount < 0
        )
    }
}