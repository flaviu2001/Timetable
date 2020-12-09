package com.flaviu.timetable.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CardDatabaseDao {
    @Insert
    suspend fun insert(card: Card)
    @Update
    suspend fun update(card: Card)
    @Query("DELETE FROM timetable_card_table WHERE cardId = :key")
    suspend fun delete(key: Long)
    @Query("DELETE FROM timetable_card_table")
    suspend fun clear()
    @Query("SELECT * from timetable_card_table WHERE cardId = :key")
    fun get(key: Long): LiveData<Card>
    @Query("SELECT * FROM timetable_card_table ORDER BY label, weekday, timeBegin, timeEnd")
    fun getAllCards(): LiveData<List<Card>>
    @Query("SELECT * FROM timetable_subtask_table WHERE subtaskId = :key")
    fun getSubtasksByCardId(key: Long): LiveData<List<Subtask>>
    @Query("SELECT * FROM timetable_subtask_table")
    fun getAllSubtasks(): LiveData<List<Subtask>>
}
