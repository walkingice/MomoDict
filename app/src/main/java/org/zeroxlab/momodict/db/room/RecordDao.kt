package org.zeroxlab.momodict.db.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import org.zeroxlab.momodict.model.Record

@Dao
interface RecordDao {
    @Query("SELECT * FROM records")
    fun getRecords(): List<Record>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addRecord(record: Record): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateRecord(record: Record): Int

    @Query("DELETE FROM records where wordStr = :keyWord")
    fun removeRecord(keyWord: String): Int
}