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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mjapa21.smartwallet.R
import com.mjapa21.smartwallet.presentation.pages.shared.ui.avatarColorFor
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
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransactionClick,
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
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
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
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
            text = "Welcome back,",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = userName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = currentDate,
            style = MaterialTheme.typography.bodySmall,
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
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            )
    ) {
        // decorative circles on the card design
        Box(
            modifier = Modifier
                .size(180.dp)
                .offset(x = 120.dp, y = (-90).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
        )
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-40).dp, y = 40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SmartWallet",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = maskedNumber,
                style = MaterialTheme.typography.titleLarge.copy(letterSpacing = 2.sp),
                color = Color.White,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text(
                text = "Current Balance",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = balanceInfo.balance,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatChip(
                    modifier = Modifier.weight(1f),
                    label = "Income",
                    value = balanceInfo.monthlyIncome,
                    isPositive = true
                )
                StatChip(
                    modifier = Modifier.weight(1f),
                    label = "Expenses",
                    value = balanceInfo.monthlyExpenses,
                    isPositive = false
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            val spentFraction = (balanceInfo.spentPercentage.coerceIn(0, 100)) / 100f
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Spent",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${balanceInfo.spentPercentage}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { spentFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (spentFraction > 0.85f) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    trackColor = MaterialTheme.colorScheme.surface,
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )
            }

            if (informativeMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                BalanceMessageRow(informativeMessage)
            }
        }
    }
}

@Composable
private fun StatChip(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    isPositive: Boolean
) {
    val tint =
        if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isPositive) "↑" else "↓",
                color = tint,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(tint.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
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

        Spacer(modifier = Modifier.height(4.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                transactions.forEachIndexed { index, transaction ->
                    TransactionRow(transaction = transaction)
                    if (index != transactions.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(
                                alpha = 0.4f
                            )
                        )
                    }
                }
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
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(avatarColorFor(transaction.title)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = transaction.title.take(1).uppercase(),
                color = Color(0xFF3E3E3E),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
            fontWeight = FontWeight.SemiBold,
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