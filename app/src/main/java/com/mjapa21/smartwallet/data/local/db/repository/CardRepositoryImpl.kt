package com.mjapa21.smartwallet.data.local.db.repository

import com.mjapa21.smartwallet.data.local.db.dao.CardDao
import com.mjapa21.smartwallet.data.local.db.mapper.toDbo
import com.mjapa21.smartwallet.data.local.db.mapper.toDomain
import com.mjapa21.smartwallet.domain.model.CardDetails
import com.mjapa21.smartwallet.domain.repository.CardRepository

class CardRepositoryImpl(
    private val cardDao: CardDao
) : CardRepository {

    override suspend fun saveCard(card: CardDetails) {
        cardDao.insertCard(card.toDbo())
    }

    override suspend fun getCard(): CardDetails? {
        return cardDao.getCard()?.toDomain()
    }
}