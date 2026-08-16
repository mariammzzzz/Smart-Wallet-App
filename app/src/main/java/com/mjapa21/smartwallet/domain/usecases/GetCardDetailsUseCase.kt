package com.mjapa21.smartwallet.domain.usecases

import com.mjapa21.smartwallet.domain.model.CardDetails
import com.mjapa21.smartwallet.domain.repository.CardRepository

class GetCardDetailsUseCase(private val cardRepository: CardRepository) {
    suspend operator fun invoke(): CardDetails? {
        return cardRepository.getCard()
    }
}