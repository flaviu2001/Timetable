package com.flaviu.timetable.ui.subtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flaviu.timetable.database.CardDatabaseDao

class SubtaskViewModelFactory(
    private val cardId: Long?,
    private val dataSource: CardDatabaseDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T: ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubtaskViewModel::class.java)) {
            return SubtaskViewModel(cardId, dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
