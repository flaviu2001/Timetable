package com.flaviu.timetable.notes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.flaviu.timetable.database.Card
import com.flaviu.timetable.database.CardDatabaseDao

class NotesViewModel(dataSource: CardDatabaseDao) : ViewModel() {
    var cards: LiveData<List<Card>> = Transformations.map(dataSource.getAllCards()) {
        val data = mutableListOf<Card>()
        it.forEach{card ->
            if (card.notes.isNotEmpty())
                data.add(card)
        }
        data
    }

    var navigateToEditCard = MutableLiveData<Long>()

    fun onCardClicked(cardKey: Long) {
        navigateToEditCard.value = cardKey
    }

    fun onEditCardNavigated() {
        navigateToEditCard.value = null
    }
}