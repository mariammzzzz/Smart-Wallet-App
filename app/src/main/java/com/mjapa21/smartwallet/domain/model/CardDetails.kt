package com.mjapa21.smartwallet.domain.model

data class CardDetails(
    val cardNumber: String,
    val cvv: String,
    val expiryDate: String
)