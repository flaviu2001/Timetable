package com.flaviu.timetable.ui.editsubtask

import androidx.lifecycle.ViewModel
import com.flaviu.timetable.database.CardDatabaseDao
import com.flaviu.timetable.database.Subtask
import kotlinx.coroutines.*
import java.util.*

class EditSubtaskViewModel(cardId: Long, subtaskId: Long, private val database: CardDatabaseDao) : ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    val subtask = database.getSubtask(subtaskId)
    val card = database.getCard(cardId)

    fun deleteSubtask() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                database.deleteSubtask(subtask.value!!.subtaskId)
            }
        }
    }

    fun updateSubtask(description: String, deadline: Calendar?, reminder: Calendar?, reminderId: Int?) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                database.updateSubtask(Subtask(subtaskId = subtask.value!!.subtaskId,
                    cardId = subtask.value!!.cardId,
                    description = description,
                    dueDate = deadline,
                    reminderDate = reminder,
                    reminderId = reminderId))
            }
        }
    }
}