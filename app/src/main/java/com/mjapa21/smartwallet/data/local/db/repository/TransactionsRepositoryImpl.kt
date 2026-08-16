package com.mjapa21.smartwallet.data.local.db.repository

import com.mjapa21.smartwallet.data.local.db.dao.TransactionDao
import com.mjapa21.smartwallet.data.local.db.mapper.toDbo
import com.mjapa21.smartwallet.data.local.db.mapper.toDomain
import com.mjapa21.smartwallet.domain.model.TransactionDetails
import com.mjapa21.smartwallet.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionsRepositoryImpl(
    private val transactionDao: TransactionDao
) : TransactionsRepository {

    override suspend fun saveTransaction(transaction: TransactionDetails) {
        transactionDao.insertTransaction(transaction.toDbo())
    }

    override fun getTransactions(): Flow<List<TransactionDetails>> {
        return transactionDao.getAllTransactionsFlow()
            .map { dboList -> dboList.map { it.toDomain() } }
    }

    override fun getRecentTransactions(limit: Int): Flow<List<TransactionDetails>> {
        return transactionDao.getFirstTransactionsFlow(limit)
            .map { dboList -> dboList.map { it.toDomain() } }
    }
}