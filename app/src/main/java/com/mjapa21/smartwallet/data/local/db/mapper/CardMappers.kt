package com.mjapa21.smartwallet.data.local.db.mapper

import com.mjapa21.smartwallet.data.local.db.model.CardDbo
import com.mjapa21.smartwallet.domain.model.CardDetails

fun CardDbo.toDomain(): CardDetails {
    return CardDetails(
        cardNumber = cardNumber,
        cvv = cvv,
        expiryDate = expiryDate
    )
}

fun CardDetails.toDbo(): CardDbo {
    return CardDbo(
        cardNumber = cardNumber,
        cvv = cvv,
        expiryDate = expiryDate
    )
}