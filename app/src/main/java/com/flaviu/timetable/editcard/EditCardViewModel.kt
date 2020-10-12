package com.flaviu.timetable.editcard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.flaviu.timetable.database.Card
import com.flaviu.timetable.database.CardDatabaseDao
import com.flaviu.timetable.weekdayToInt
import kotlinx.coroutines.*

class EditCardViewModel(
    cardKey: Long,
    private var dataSource: CardDatabaseDao,
    private val viewModelApplication: Application
) : AndroidViewModel(viewModelApplication) {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    val card = dataSource.get(cardKey)

    fun onDeleteButtonPressed() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                card.value?.let { dataSource.delete(it.cardId) }
            }
        }
    }

    fun clearNotes() {
        val newCard = card.value ?: throw Exception("Invalid card")
        newCard.notes = ""
        uiScope.launch {
            withContext(Dispatchers.IO) {
                dataSource.update(newCard)
            }
        }
    }

    fun editCard(
        start: String,
        finish: String,
        weekday: String,
        place: String,
        name: String,
        info: String,
        label: String,
        notes: String,
        color: Int
    ) {
        listOf(start, finish, weekday, place, name, label).forEach{
            if (it.isEmpty())
                throw Exception("All fields must be completed.")
        }
        val newCard = card.value ?: throw Exception("Invalid card")
        newCard.place = place
        newCard.info = info
        newCard.name = name
        newCard.timeBegin = start
        newCard.timeEnd = finish
        newCard.weekday = weekdayToInt(weekday, viewModelApplication.resources)
        newCard.label = label
        newCard.notes = notes
        newCard.color = color
        uiScope.launch {
            withContext(Dispatchers.IO) {
                dataSource.update(newCard)
            }
        }
    }

    fun cloneCard(
        start: String,
        finish: String,
        weekday: String,
        place: String,
        name: String,
        info: String,
        label: String,
        notes: String,
        color: Int
    ) {
        listOf(start, finish, weekday, place, name, label).forEach{
            if (it.isEmpty())
                throw Exception("All fields must be completed.")
        }
        val newCard = Card(timeBegin = start,
            timeEnd = finish,
            weekday = weekdayToInt(weekday, viewModelApplication.resources),
            place = place,
            name = name,
            info = info,
            label = label,
            notes = notes,
            color = color
        )
        uiScope.launch {
            withContext(Dispatchers.IO) {
                dataSource.insert(newCard)
            }
        }
    }
}