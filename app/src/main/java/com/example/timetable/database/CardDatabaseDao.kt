package com.example.timetable.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CardDatabaseDao {
    @Insert
    suspend fun insert(card: Card)
    @Update
    suspend fun update(card: Card)
    @Query("DELETE FROM timetable_card_table")
    suspend fun clear()
    @Query("SELECT * from timetable_card_table WHERE cardId = :key")
    fun get(key: Long): LiveData<Card>
    @Query("SELECT * FROM timetable_card_table ORDER BY weekday, timeBegin")
    fun getAllNights(): LiveData<List<Card>>
}
