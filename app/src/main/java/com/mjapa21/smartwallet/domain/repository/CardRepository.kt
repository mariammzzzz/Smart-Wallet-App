package com.mjapa21.smartwallet.domain.repository

import com.mjapa21.smartwallet.domain.model.CardDetails

interface CardRepository {
    suspend fun saveCard(card: CardDetails)

    suspend fun getCard(): CardDetails?
}