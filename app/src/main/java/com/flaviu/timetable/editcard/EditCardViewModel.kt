package com.flaviu.timetable.editcard

import androidx.lifecycle.ViewModel
import com.flaviu.timetable.database.CardDatabaseDao
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
        info: String
    ) {
        val newCard = card.value ?: throw Exception("Invalid card")
        newCard.place = place
        newCard.info = info
        newCard.name = name
        newCard.timeBegin = start.toInt()
        newCard.timeEnd = finish.toInt()
        newCard.weekday = weekday.toInt()
        uiScope.launch {
            withContext(Dispatchers.IO) {
                dataSource.update(newCard)
            }
        }
    }
}