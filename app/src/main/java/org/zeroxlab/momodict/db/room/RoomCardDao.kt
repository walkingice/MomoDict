package org.zeroxlab.momodict.db.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import org.zeroxlab.momodict.model.Card

@Dao
interface RoomCardDao {
    @Query("SELECT * FROM cards")
    fun getCards(): List<Card>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addCard(card: RoomCard): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateCard(card: RoomCard): Int

    @Query("DELETE FROM cards where wordStr = :keyWord")
    fun removeCard(keyWord: String): Int
}