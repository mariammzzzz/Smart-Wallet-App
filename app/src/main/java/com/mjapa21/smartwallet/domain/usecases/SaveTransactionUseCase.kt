package com.mjapa21.smartwallet.domain.usecases

import com.mjapa21.smartwallet.domain.model.TransactionDetails
import com.mjapa21.smartwallet.domain.repository.TransactionsRepository

class SaveTransactionUseCase(private val transactionsRepository: TransactionsRepository) {
    suspend operator fun invoke(transaction: TransactionDetails) {
        transactionsRepository.saveTransaction(transaction)
    }

}