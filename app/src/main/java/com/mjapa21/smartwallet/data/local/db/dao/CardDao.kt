package com.mjapa21.smartwallet.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mjapa21.smartwallet.data.local.db.model.CardDbo


@Dao
interface CardDao {
    @Insert
    suspend fun insertCard(card: CardDbo)

    @Query("SELECT * FROM CARDS LIMIT 1")
    suspend fun getCard(): CardDbo?
}