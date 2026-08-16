package com.mjapa21.smartwallet.data.local.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "cards")
data class CardDbo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cardNumber: String,
    val cvv: String,
    val expiryDate: String,
)