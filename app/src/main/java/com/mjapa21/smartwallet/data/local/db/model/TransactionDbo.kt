package com.mjapa21.smartwallet.data.local.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "transactions")
data class TransactionDbo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val amount: Double,
    val date: Long
)