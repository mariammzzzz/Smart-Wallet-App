package com.mjapa21.smartwallet.presentation.pages.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mjapa21.smartwallet.R
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun HomeScreen(
    onSeeAllTransactionsClick: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val addTransactionState by viewModel.addTransactionState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.ShowError ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    HomeContent(
        uiState = uiState,
        onAddTransactionClick = viewModel::onAddTransactionClick,
        onSeeAllTransactionsClick = onSeeAllTransactionsClick
    )

    if (uiState.isAddTransactionSheetVisible) {
        AddTransactionBottomSheet(
            state = addTransactionState,
            onTitleChange = viewModel::onTransactionTitleChange,
            onAmountChange = viewModel::onTransactionAmountChange,
            onTypeToggle = viewModel::onTransactionTypeToggle,
            onSaveClick = viewModel::onSaveTransactionClick,
            onDismiss = viewModel::onDismissAddTransactionSheet
        )
    }
}


@Composable
fun HomeContent(
    uiState: HomeUiState,
    onAddTransactionClick: () -> Unit = {},
    onSeeAllTransactionsClick: () -> Unit = {}
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTransactionClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_outline_add_24),
                    contentDescription = "Add transaction"
                )
            }
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            WelcomeHeader(userName = uiState.userName, currentDate = uiState.currentDate)
            CreditCard(cardInfo = uiState.cardInfo)
            BalanceSummary(
                balanceInfo = uiState.balanceInfo,
                informativeMessage = uiState.balanceInformativeMessage
            )
            RecentTransactionsSection(
                transactions = uiState.recentTransactions,
                onSeeAllClick = onSeeAllTransactionsClick
            )
        }
    }
}

@Composable
private fun WelcomeHeader(userName: String, currentDate: String) {
    Column {
        Text(
            text = "Welcome, $userName",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = currentDate,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CreditCard(cardInfo: CardInfo?) {
    if (cardInfo == null) return
    val maskedNumber = formatCardNumber(cardInfo.cardNumber)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.7f)
            .clip(RoundedCornerShape(20.dp))
            .background( //background for a card like design with a gradient
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            )
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "SmartWallet",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = maskedNumber,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "EXPIRES",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = cardInfo.expiryDate.formatToDDMM(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "CVV",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = cardInfo.cvv,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Groups the card number into 4-digit blocks
 * Example: "1111222233334444" -> "1111 2222 3333 4444"
 */
private fun formatCardNumber(cardNumber: String): String {
    return cardNumber.chunked(4).joinToString(" ")
}

@Composable
private fun BalanceSummary(
    balanceInfo: BalanceInfo?,
    informativeMessage: BalanceInformativeMessage?
) {
    if (balanceInfo == null) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Current Balance",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = balanceInfo.balance,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BalanceStat(label = "Income", value = balanceInfo.monthlyIncome)
                BalanceStat(label = "Expenses", value = balanceInfo.monthlyExpenses)
                BalanceStat(label = "Spent", value = "${balanceInfo.spentPercentage}%")
            }

            if (informativeMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                BalanceMessageRow(informativeMessage)
            }
        }
    }
}

@Composable
private fun BalanceMessageRow(informativeMessage: BalanceInformativeMessage) {
    val (iconRes, tint) = when (informativeMessage) {
        is BalanceInformativeMessage.Negative ->
            R.drawable.ic_exclamation to MaterialTheme.colorScheme.error
        is BalanceInformativeMessage.Warning ->
            R.drawable.ic_warning to MaterialTheme.colorScheme.tertiary
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null, // decorative, the text next to it carries the meaning
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = informativeMessage.message,
            style = MaterialTheme.typography.bodySmall,
            color = tint
        )
    }
}

@Composable
private fun BalanceStat(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun RecentTransactionsSection(
    transactions: List<TransactionInfo>?,
    onSeeAllClick: () -> Unit
) {
    if (transactions.isNullOrEmpty()) return //even if we fetched transactions already but its empty we dont draw the section
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = onSeeAllClick) {
                Text(text = "See all")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        //this is not a lazy columns because it shows just a few transactions
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            transactions.forEach { transaction ->
                TransactionRow(transaction = transaction)
            }
        }
    }
}

private fun String.formatToDDMM(): String {
    return this.chunked(2).joinToString(separator = "/")
}

@Composable
private fun TransactionRow(transaction: TransactionInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = transaction.title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = transaction.date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = transaction.amount,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = if (transaction.amount.startsWith("-")) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            }
        )
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun HomeContentPreview() {
    HomeContent(
        uiState = HomeUiState.mock().copy(userName = "Mariam", currentDate = "Thursday, 4 August")
    )
}