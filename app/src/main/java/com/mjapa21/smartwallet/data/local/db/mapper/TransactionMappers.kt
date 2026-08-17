package com.mjapa21.smartwallet.data.local.db.mapper

import com.mjapa21.smartwallet.data.local.db.model.TransactionDbo
import com.mjapa21.smartwallet.domain.model.TransactionDetails


fun TransactionDbo.toDomain(): TransactionDetails {
    return TransactionDetails(
        name = name,
        amount = amount,
        date = date,
        id = id
    )
}

fun TransactionDetails.toDbo(): TransactionDbo {
    return TransactionDbo(
        name = name,
        amount = amount,
        date = date
    )
}