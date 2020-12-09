package com.flaviu.timetable.ui.subtask

import androidx.lifecycle.ViewModel
import com.flaviu.timetable.database.CardDatabaseDao

class SubtaskViewModel(private val cardId: Long?, private val database: CardDatabaseDao) : ViewModel() {
    val subtasks = if (cardId == null)
        database.getAllSubtasks()
    else database.getSubtasksByCardId(cardId)


}