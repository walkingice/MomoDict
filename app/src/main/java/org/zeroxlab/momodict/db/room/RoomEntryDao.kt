package org.zeroxlab.momodict.db.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import org.zeroxlab.momodict.model.Entry

@Dao
interface RoomEntryDao {

    @Query("SELECT * FROM entries WHERE wordStr = :keyword")
    fun getEntries(keyword: String): List<Entry>

    @Query("SELECT * FROM entries WHERE wordStr LIKE '%' || :keyword || '%'")
    fun queryEntries(keyword: String): List<Entry>

    @Query("SELECT * FROM entries WHERE wordStr LIKE '%' || :keyword || '%' AND source is :bookName")
    fun queryEntries(keyword: String, bookName: String): List<Entry>

    @Insert
    fun addEntries(entries: List<RoomEntry>)

    @Query("DELETE FROM entries WHERE source = :bookName")
    fun removeEntriesByBookName(bookName: String)
}