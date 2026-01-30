package cc.jchu.momodict.db.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cc.jchu.momodict.model.Card

@Dao
interface CardDao {
    @Query("SELECT * FROM cards")
    fun getCards(): List<Card>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addCard(card: Card): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateCard(card: Card): Int

    @Query("DELETE FROM cards where wordStr = :keyWord")
    fun removeCard(keyWord: String): Int
}
