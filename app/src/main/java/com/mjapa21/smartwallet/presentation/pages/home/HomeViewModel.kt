package com.mjapa21.smartwallet.presentation.pages.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjapa21.smartwallet.domain.model.CardDetails
import com.mjapa21.smartwallet.domain.model.TransactionDetails
import com.mjapa21.smartwallet.domain.usecases.GetCardDetailsUseCase
import com.mjapa21.smartwallet.domain.usecases.GetTransactionsUseCase
import com.mjapa21.smartwallet.domain.usecases.GetUserUseCase
import com.mjapa21.smartwallet.domain.usecases.SaveTransactionUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

/** One-time occurrences */
sealed interface HomeEvent {
    data class ShowError(val message: String) : HomeEvent
}

class HomeViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val getCardDetailsUseCase: GetCardDetailsUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val saveTransactionUseCase: SaveTransactionUseCase,
) : ViewModel() {

    /***
     * HomeScreen ui state
     */
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()


    /***
     * Add Transation bottom sheet ui state
     */
    private val _addTransactionState = MutableStateFlow(AddTransactionUiState())
    val addTransactionState: StateFlow<AddTransactionUiState> = _addTransactionState.asStateFlow()


    /***
     * channel for sending error events
     */
    private val _events = Channel<HomeEvent>()
    val events: Flow<HomeEvent> = _events.receiveAsFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                val user = getUserUseCase()
                val card = getCardDetailsUseCase()

                getTransactionsUseCase().collect { transactions ->
                    val monthlyIncome = user.monthlyIncome
                    // getUserUseCase() completed before we started collecting transactions,
                    // so the user's monthly income is already available for the first
                    // transaction emission and for every subsequent emission.
                    val monthlyExpenses = transactions
                        .filter { it.amount < 0 }
                        .sumOf { abs(it.amount) }

                    val balance = monthlyIncome - monthlyExpenses

                    val spentPercentage = if (monthlyIncome > 0) {
                        ((monthlyExpenses / monthlyIncome) * 100).toInt()
                    } else {
                        0
                    }

                    _uiState.update { current ->
                        current.copy(
                            userName = user.name,
                            currentDate = formatToday(),
                            cardInfo = card?.toCardInfo(),
                            balanceInfo = BalanceInfo(
                                monthlyIncome = monthlyIncome.formatCurrency(),
                                monthlyExpenses = monthlyExpenses.formatCurrency(),
                                balance = balance.formatBalance(),
                                spentPercentage = spentPercentage
                            ),
                            recentTransactions = transactions
                                .sortedByDescending { it.date }
                                .take(4)
                                .map { it.toTransactionInfo() }
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.send(
                    HomeEvent.ShowError(
                        e.message ?: "Couldn't load your data. Please try again."
                    )
                )
            }
        }
    }

    // ---- Bottom sheet visibility ----

    fun onAddTransactionClick() {
        _uiState.update { it.copy(isAddTransactionSheetVisible = true) }
    }

    fun onDismissAddTransactionSheet() {
        _uiState.update { it.copy(isAddTransactionSheetVisible = false) }
        _addTransactionState.value = AddTransactionUiState() // reset the form for next time
    }

    // ---- Bottom sheet form fields ----

    fun onTransactionTitleChange(value: String) {
        _addTransactionState.update { it.copy(title = value) }
    }

    fun onTransactionAmountChange(value: String) {
        // digits + single decimal point filtering; just like the income field on Login
        val filtered = value.filter { it.isDigit() || it == '.' }
        _addTransactionState.update { it.copy(amount = filtered) }
    }

    fun onTransactionTypeToggle(isIncome: Boolean) {
        _addTransactionState.update { it.copy(isIncome = isIncome) }
    }

    fun onSaveTransactionClick() {
        val form = _addTransactionState.value

        val validationError = validateTransactionForm(form)
        if (validationError != null) {
            viewModelScope.launch { _events.send(HomeEvent.ShowError(validationError)) }
            return
        }

        viewModelScope.launch {
            _addTransactionState.update { it.copy(isBeingSavedLocally = true) }
            try {
                val rawAmount = form.amount.toDouble()
                //income = positive, expense = negative
                val signedAmount = if (form.isIncome) abs(rawAmount) else -abs(rawAmount)

                saveTransactionUseCase(
                    TransactionDetails(
                        name = form.title.trim(),
                        amount = signedAmount,
                        date = System.currentTimeMillis()
                    )
                )

                // loadHomeData()'s getTransactionsUseCase().collect will pick up the
                // new row automatically (Room Flow re-emits on any table change) —
                // no manual refresh is needed here :))
                _uiState.update { it.copy(isAddTransactionSheetVisible = false) }
                _addTransactionState.value = AddTransactionUiState()
            } catch (e: Exception) {
                _addTransactionState.update { it.copy(isBeingSavedLocally = false) }
                _events.send(
                    HomeEvent.ShowError(
                        e.message ?: "Couldn't save the transaction. Please try again."
                    )
                )
            }
        }
    }

    private fun validateTransactionForm(form: AddTransactionUiState): String? {
        val amountValue = form.amount.toDoubleOrNull()
        return when {
            form.title.isBlank() -> "Please enter a title"
            amountValue == null -> "Enter a valid amount"
            amountValue <= 0.0 -> "Amount must be greater than zero"
            else -> null
        }
    }

    // ---- Formatting ----

    private fun formatToday(): String {
        val formatter = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
        return formatter.format(Date())
    }

    private fun Double.formatCurrency(): String {
        return "₾" + "%,.2f".format(Locale.US, this)
    }

    private fun Double.formatSignedAmount(): String {
        val formatted = (abs(this)).formatCurrency()
        return if (this >= 0) "+$formatted" else "-$formatted"
    }

    private fun Double.formatBalance(): String {
        return if (this < 0) formatSignedAmount() else formatCurrency()
    }

    private fun Long.toShortDate(): String {
        // date is epoch millis (Long) — so we can wrap directly in Date() for formatting
        val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
        return formatter.format(Date(this))
    }

    private fun CardDetails.toCardInfo(): CardInfo {
        return CardInfo(
            cardNumber = cardNumber,
            cvv = cvv,
            expiryDate = expiryDate
        )
    }

    private fun TransactionDetails.toTransactionInfo(): TransactionInfo {
        return TransactionInfo(
            title = name,
            amount = amount.formatSignedAmount(),
            date = date.toShortDate()
        )
    }
}