package com.flaviu.timetable.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CardDatabaseDao {
    @Insert
    suspend fun insertCard(card: Card)
    @Update
    suspend fun updateCard(card: Card)
    @Query("DELETE FROM timetable_card_table WHERE cardId = :key")
    suspend fun deleteCard(key: Long)
    @Query("DELETE FROM timetable_card_table")
    suspend fun clear()
    @Insert
    suspend fun insertSubtask(subtask: Subtask)
    @Update
    suspend fun updateSubtask(subtask: Subtask)
    @Query("DELETE FROM timetable_subtask_table WHERE subtaskId = :key")
    suspend fun deleteSubtask(key: Long)
    @Query("SELECT * from timetable_card_table WHERE cardId = :key")
    fun getCard(key: Long): LiveData<Card>
    @Query("SELECT C.* FROM timetable_card_table C INNER JOIN timetable_card_label_table CL on C.cardId = CL.cardId INNER JOIN timetable_label_table L on CL.labelId = L.labelId WHERE L.name = :label ORDER BY weekday, timeBegin, timeEnd")
    fun getCardsWithLabel(label: String): LiveData<List<Card>>
    @Query("SELECT * FROM timetable_subtask_table WHERE subtaskId = :key")
    fun getSubtasksByCardId(key: Long): LiveData<List<Subtask>>
    @Query("SELECT * FROM timetable_subtask_table")
    fun getAllSubtasks(): LiveData<List<Subtask>>
    @Query("SELECT * FROM timetable_label_table")
    fun getAllLabels(): LiveData<List<Label>>
    @Query("SELECT L.* FROM timetable_card_table C INNER JOIN timetable_card_label_table CL on C.cardId = CL.cardId INNER JOIN timetable_label_table L on CL.labelId = L.labelId WHERE C.cardId = :cardId")
    fun getLabelsOfCard(cardId: Long): LiveData<List<Label>>
}
