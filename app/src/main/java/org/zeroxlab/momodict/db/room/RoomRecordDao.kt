package org.zeroxlab.momodict.db.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import org.zeroxlab.momodict.model.Record

@Dao
interface RoomRecordDao {
    @Query("SELECT * FROM records")
    fun getRecords(): List<Record>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addRecord(record: RoomRecord): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateRecord(record: RoomRecord): Int

    @Query("DELETE FROM records where wordStr = :keyWord")
    fun removeRecord(keyWord: String): Int
}