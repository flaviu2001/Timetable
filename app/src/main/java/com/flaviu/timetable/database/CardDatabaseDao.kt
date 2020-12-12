package com.flaviu.timetable.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CardDatabaseDao {
    @Insert
    suspend fun insertCard(card: Card): Long
    @Update
    suspend fun updateCard(card: Card)
    @Query("DELETE FROM timetable_card_table WHERE cardId = :key")
    suspend fun deleteCard(key: Long)
    @Query("DELETE FROM timetable_card_table")
    suspend fun clearCards()
    @Query("DELETE FROM timetable_label_table")
    suspend fun clearLabels()
    @Insert
    suspend fun insertSubtask(subtask: Subtask)
    @Update
    suspend fun updateSubtask(subtask: Subtask)
    @Query("DELETE FROM timetable_subtask_table WHERE subtaskId = :key")
    suspend fun deleteSubtask(key: Long)
    @Query("DELETE FROM timetable_card_label_table WHERE cardId = :key")
    suspend fun deleteAllLabelsFromCard(key: Long)
    @Insert
    suspend fun insertLabel(label: Label)
    @Query("DELETE FROM timetable_label_table WHERE labelId = :key")
    suspend fun deleteLabel(key: Long)
    @Update
    suspend fun updateLabel(label: Label)
    @Query("DELETE FROM timetable_card_table WHERE NOT EXISTS (select * from timetable_card_label_table CL WHERE timetable_card_table.cardId = CL.cardId)")
    suspend fun deleteCardsWithoutLabels()
    @Query("INSERT INTO timetable_card_label_table(cardId, labelId) values(:cardId, :labelId)")
    suspend fun connectLabelToCard(cardId: Long, labelId: Long)
    @Query("SELECT * from timetable_card_table WHERE cardId = :key")
    fun getCard(key: Long): LiveData<Card>
    @Query("SELECT C.* FROM timetable_card_table C INNER JOIN timetable_card_label_table CL on C.cardId = CL.cardId INNER JOIN timetable_label_table L on CL.labelId = L.labelId WHERE L.name = :label ORDER BY weekday, timeBegin, timeEnd")
    fun getCardsWithLabel(label: String): LiveData<List<Card>>
    @Query("SELECT * FROM timetable_card_table")
    fun getAllCards(): LiveData<List<Card>>
    @Query("SELECT * FROM timetable_subtask_table WHERE subtaskId = :key")
    fun getSubtasksByCardId(key: Long): LiveData<List<Subtask>>
    @Query("SELECT * FROM timetable_subtask_table")
    fun getAllSubtasks(): LiveData<List<Subtask>>
    @Query("SELECT * FROM timetable_label_table ORDER BY name")
    fun getAllLabels(): LiveData<List<Label>>
    @Query("SELECT * FROM timetable_label_table L WHERE EXISTS (select * from timetable_card_label_table CL WHERE L.labelId = CL.labelId) ORDER BY name")
    fun getNonEmptyLabels(): LiveData<List<Label>>
    @Query("SELECT L.* FROM timetable_card_table C INNER JOIN timetable_card_label_table CL on C.cardId = CL.cardId INNER JOIN timetable_label_table L on CL.labelId = L.labelId WHERE C.cardId = :cardId ORDER BY L.name")
    fun getLabelsOfCard(cardId: Long): LiveData<List<Label>>
}
