package com.flaviu.timetable.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.flaviu.timetable.database.Card
import com.flaviu.timetable.database.CardDatabaseDao

class HomeViewModel(
    database: CardDatabaseDao
) : ViewModel() {
    var cards = database.getAllCards()
    val tabs: LiveData<List<String>> = Transformations.switchMap(cards) { cards: List<Card> ->
        val newList = cards.map {
            it.label
        }.toSortedSet().toList()
        MutableLiveData(newList)
    }
}