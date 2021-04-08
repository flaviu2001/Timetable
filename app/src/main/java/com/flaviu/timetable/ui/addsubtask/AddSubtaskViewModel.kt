package com.flaviu.timetable.ui.addsubtask

import android.app.Application
import androidx.lifecycle.ViewModel
import com.flaviu.timetable.NotificationIdManipulator
import com.flaviu.timetable.database.CardDatabaseDao
import com.flaviu.timetable.database.Subtask
import com.flaviu.timetable.scheduleNotification
import kotlinx.coroutines.*
import java.util.*

class AddSubtaskViewModel(private val database: CardDatabaseDao, private val viewModelApplication: Application, cardId: Long) : ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    val card = database.getCard(cardId)

    fun addSubtask(
        cardId: Long,
        description: String,
        dueDate: Calendar?,
        reminderDate: Calendar?
    ) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val subtask = Subtask(
                    cardId = cardId,
                    description = description,
                    dueDate = dueDate,
                    reminderDate = reminderDate,
                    reminderId = NotificationIdManipulator.generateId(viewModelApplication.applicationContext)
                )
                database.insertSubtask(subtask)
                scheduleNotification(viewModelApplication.applicationContext, subtask.reminderId!!, subtask.reminderDate)
            }
        }
    }
}