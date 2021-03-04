package com.flaviu.timetable.ui.addsubtask

import androidx.lifecycle.ViewModel
import com.flaviu.timetable.database.CardDatabaseDao
import com.flaviu.timetable.database.Subtask
import kotlinx.coroutines.*
import java.util.*

class AddSubtaskViewModel(private val database: CardDatabaseDao, cardId: Long) : ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    val card = database.getCard(cardId)

    fun addSubtask(cardId: Long, description: String, dueDate: Calendar?, reminderDate: Calendar?, reminderId: Int) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                database.insertSubtask(Subtask(cardId = cardId, description = description, dueDate = dueDate, reminderDate = reminderDate, reminderId = reminderId))
            }
        }
    }
}