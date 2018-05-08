package org.zeroxlab.momodict.db.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
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