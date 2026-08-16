package com.mjapa21.smartwallet.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mjapa21.smartwallet.data.local.db.dao.CardDao
import com.mjapa21.smartwallet.data.local.db.dao.TransactionDao
import com.mjapa21.smartwallet.data.local.db.model.CardDbo
import com.mjapa21.smartwallet.data.local.db.model.TransactionDbo

@Database(entities = [CardDbo::class, TransactionDbo::class], version = 1)
abstract class SmartWalletDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao

    abstract fun transactionDao(): TransactionDao
}
