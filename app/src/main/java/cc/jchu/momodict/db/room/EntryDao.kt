package cc.jchu.momodict.db.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cc.jchu.momodict.model.Entry

@Dao
interface EntryDao {
    @Query("SELECT * FROM entries WHERE wordStr = :keyword")
    fun getEntries(keyword: String): List<Entry>

    // TODO: can the limitation be configurable?
    @Query("SELECT * FROM entries WHERE wordStr LIKE '%' || :keyword || '%' LIMIT 0, 1000")
    fun queryEntries(keyword: String): List<Entry>

    // TODO: can the limitation be configurable?
    @Query("SELECT * FROM entries WHERE wordStr LIKE '%' || :keyword || '%' AND source is :bookName LIMIT 0, 1000")
    fun queryEntries(
        keyword: String,
        bookName: String,
    ): List<Entry>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addEntries(entries: List<Entry>)

    @Query("DELETE FROM entries WHERE source = :bookName")
    fun removeEntriesByBookName(bookName: String)
}
