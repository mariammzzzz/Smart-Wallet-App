package com.mjapa21.smartwallet.presentation.pages.home

data class HomeUiState(
    val userName: String,
    val currentDate: String,
    val cardInfo: CardInfo,
    val balanceInfo: BalanceInfo,
    val recentTransactions: List<TransactionInfo>,
) {
    companion object {
        //todo remove later, this is just for now
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


data class CardInfo(
    val cardNumber: String,
    val cvv: String,
    val expiryDate: String,
)

data class BalanceInfo(
    val monthlyIncome: String,
    val monthlyExpenses: String,
    val balance: String,
    val spentPercentage: Int,
)

data class TransactionInfo(
    val id: String,
    val title: String,
    val amount: String,
    val date: String,
)