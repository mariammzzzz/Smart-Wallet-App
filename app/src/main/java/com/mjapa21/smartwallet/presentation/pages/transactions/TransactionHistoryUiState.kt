package com.mjapa21.smartwallet.presentation.pages.transactions

data class TransactionHistoryUiState(
    val isLoading: Boolean = true,
    val transactions: List<TransactionUiModel> = emptyList()
) {
    companion object {
        fun mock() = TransactionHistoryUiState(
            isLoading = false,
            transactions = listOf(
                TransactionUiModel(1, "Grocery Store", "12 Aug 2025", "-$54.20", true),
                TransactionUiModel(2, "Salary", "10 Aug 2025", "+$2,400.00", false),
                TransactionUiModel(3, "Netflix", "08 Aug 2025", "-$15.99", true)
            )
        )
    }
}

data class TransactionUiModel(
    val id: Int?,
    val title: String,
    val date: String,
    val amount: String,
    val isExpense: Boolean
)