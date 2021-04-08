package com.flaviu.timetable.ui.editsubtask

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flaviu.timetable.database.CardDatabaseDao

class EditSubtaskViewModelFactory (
    private val cardId: Long,
    private val subtaskId: Long,
    private val database: CardDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T: ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditSubtaskViewModel::class.java)) {
            return EditSubtaskViewModel(cardId, subtaskId, database, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}