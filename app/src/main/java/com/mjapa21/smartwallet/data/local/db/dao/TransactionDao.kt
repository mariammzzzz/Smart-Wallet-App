package com.mjapa21.smartwallet.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mjapa21.smartwallet.data.local.db.model.TransactionDbo
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert
    suspend fun insertTransaction(transaction: TransactionDbo) //onConflict it will ABORT by default

    @Query("SELECT * FROM transactions")
    fun getAllTransactionsFlow(): Flow<List<TransactionDbo>>

    @Query(
        """
    SELECT * 
    FROM transactions
    ORDER BY date DESC
    LIMIT :n
"""
    )
    fun getFirstTransactionsFlow(n: Int): Flow<List<TransactionDbo>>
}