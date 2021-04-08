package com.flaviu.timetable.ui.addsubtask

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flaviu.timetable.database.CardDatabaseDao

class AddSubtaskViewModelFactory (
    private val database: CardDatabaseDao,
    private val viewModelApplication: Application,
    private val cardId: Long
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T: ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddSubtaskViewModel::class.java)) {
            return AddSubtaskViewModel(database, viewModelApplication, cardId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}