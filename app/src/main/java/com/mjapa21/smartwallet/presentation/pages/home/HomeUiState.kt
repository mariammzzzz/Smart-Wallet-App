package com.mjapa21.smartwallet.presentation.pages.home

data class HomeUiState(
    val userName: String = "",
    val currentDate: String = "",
    val cardInfo: CardInfo? = null,
    val balanceInfo: BalanceInfo? = null,
    val balanceInformativeMessage: BalanceInformativeMessage? = null,
    val recentTransactions: List<TransactionInfo>? = null,
    val isAddTransactionSheetVisible: Boolean = false,
) {
    companion object {
        //Mock ui state object for the HomeScreenPreview
        fun mock() = HomeUiState(
            userName = "",
            currentDate = "",
            cardInfo = CardInfo(
                cardNumber = "4111222233334444",
                cvv = "123",
                expiryDate = "09/28"
            ),
            balanceInfo = BalanceInfo(
                monthlyIncome = "₾0.00",
                monthlyExpenses = "₾0.00",
                balance = "₾0.00",
                spentPercentage = 0
            ),
            balanceInformativeMessage = BalanceInformativeMessage.Warning(
                "You've already spent 80% of your income this month."
            ),
            recentTransactions = listOf(
                TransactionInfo(
                    id = "1",
                    title = "Grocery Store",
                    amount = "-₾54.20",
                    date = "Aug 14"
                ),
                TransactionInfo(id = "2", title = "Salary", amount = "+₾2,400.00", date = "Aug 12"),
                TransactionInfo(id = "3", title = "Netflix", amount = "-₾15.99", date = "Aug 10"),
                TransactionInfo(id = "4", title = "Coffee Shop", amount = "-₾6.50", date = "Aug 9")
            )
        )
    }
}

//this is for the bottom sheet ui state
data class AddTransactionUiState(
    val title: String = "",
    val amount: String = "",
    val isIncome: Boolean = false, // false = expense, true = income
    val isBeingSavedLocally: Boolean = false
)

data class CardInfo(
    val cardNumber: String,
    val cvv: String,
    val expiryDate: String,
)

data class BalanceInfo(
    val monthlyIncome: String,
    val monthlyExpenses: String = "",
    val balance: String = "",
    val spentPercentage: Int = 0,
)

data class TransactionInfo(
    val title: String,
    val amount: String,
    val date: String,
    val id: String? = null,
)

sealed interface BalanceInformativeMessage {
    val message: String

    data class Negative(override val message: String) : BalanceInformativeMessage
    data class Warning(override val message: String) : BalanceInformativeMessage
}

