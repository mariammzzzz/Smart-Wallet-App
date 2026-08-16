package com.mjapa21.smartwallet.domain.usecases

import com.mjapa21.smartwallet.domain.model.TransactionDetails
import com.mjapa21.smartwallet.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow

class GetTransactionsUseCase(private val transactionsRepository: TransactionsRepository) {
    operator fun invoke(): Flow<List<TransactionDetails>> {
        return transactionsRepository.getTransactions()
    }
}