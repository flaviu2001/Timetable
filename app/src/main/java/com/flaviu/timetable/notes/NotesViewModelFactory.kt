package com.flaviu.timetable.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flaviu.timetable.database.CardDatabaseDao

class NotesViewModelFactory(
    private val dataSource: CardDatabaseDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T: ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            return NotesViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}