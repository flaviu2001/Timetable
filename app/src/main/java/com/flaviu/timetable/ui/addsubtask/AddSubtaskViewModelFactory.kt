package com.flaviu.timetable.ui.addsubtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flaviu.timetable.database.CardDatabaseDao

class AddSubtaskViewModelFactory (
    private val database: CardDatabaseDao,
    private val cardId: Long
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T: ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddSubtaskViewModel::class.java)) {
            return AddSubtaskViewModel(database, cardId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}