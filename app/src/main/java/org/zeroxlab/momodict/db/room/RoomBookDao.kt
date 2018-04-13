package org.zeroxlab.momodict.db.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import org.zeroxlab.momodict.model.Book

@Dao
interface RoomBookDao {

    @Query("SELECT * from dictionaries")
    fun getAll(): List<Book>

    @Insert
    fun addBook(book: RoomBook)
}