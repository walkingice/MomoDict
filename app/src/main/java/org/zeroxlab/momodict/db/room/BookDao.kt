package org.zeroxlab.momodict.db.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import org.zeroxlab.momodict.model.Book

@Dao
interface BookDao {
    @Query("DELETE FROM dictionaries WHERE bookName = :bookName")
    fun removeBook(bookName: String): Int

    @Query("SELECT * from dictionaries")
    fun getAll(): List<Book>

    @Insert
    fun addBook(book: Book)
}