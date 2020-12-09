package com.flaviu.timetable.ui.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.flaviu.timetable.database.CardDatabaseDao

class ListViewModel (
    database: CardDatabaseDao
) : ViewModel() {
    var cards = database.getAllCards()

    var navigateToEditCard = MutableLiveData<Long>()

    fun onCardClicked(cardKey: Long) {
        navigateToEditCard.value = cardKey
    }

    fun onEditCardNavigated() {
        navigateToEditCard.value = null
    }
}