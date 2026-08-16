package com.mjapa21.smartwallet.domain.repository

import com.mjapa21.smartwallet.domain.model.TransactionDetails
import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {
    suspend fun saveTransaction(transaction: TransactionDetails)

    fun getTransactions(): Flow<List<TransactionDetails>>

    /** For the Home screen's "recent transactions" section. */
    fun getRecentTransactions(limit: Int): Flow<List<TransactionDetails>>
}