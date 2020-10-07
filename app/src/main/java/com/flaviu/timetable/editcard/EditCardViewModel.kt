package com.flaviu.timetable.editcard

import androidx.lifecycle.ViewModel
import com.flaviu.timetable.database.Card
import com.flaviu.timetable.database.CardDatabaseDao
import com.flaviu.timetable.weekdayToInt
import kotlinx.coroutines.*
import java.lang.Exception

class EditCardViewModel(
    cardKey: Long,
    private var dataSource: CardDatabaseDao
) : ViewModel() {
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

    fun editCard(
        start: String,
        finish: String,
        weekday: String,
        place: String,
        name: String,
        info: String,
        label: String
    ) {
        listOf(start, finish, weekday, place, name, info, label).forEach{
            if (it.isEmpty())
                throw Exception("All fields must be completed.")
        }
        val newCard = card.value ?: throw Exception("Invalid card")
        newCard.place = place
        newCard.info = info
        newCard.name = name
        newCard.timeBegin = start
        newCard.timeEnd = finish
        newCard.weekday = weekdayToInt(weekday)
        newCard.label = label
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
        label: String
    ) {
        listOf(start, finish, weekday, place, name, info, label).forEach{
            if (it.isEmpty())
                throw Exception("All fields must be completed.")
        }
        val newCard = Card(timeBegin = start,
            timeEnd = finish,
            weekday = weekdayToInt(weekday),
            place = place,
            name = name,
            info = info,
            label = label)
        uiScope.launch {
            withContext(Dispatchers.IO) {
                dataSource.insert(newCard)
            }
        }
    }
}