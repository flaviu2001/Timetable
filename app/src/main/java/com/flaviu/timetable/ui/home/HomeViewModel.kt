package com.flaviu.timetable.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.flaviu.timetable.database.CardDatabaseDao

class HomeViewModel(
    database: CardDatabaseDao
) : ViewModel() {
    var labels = database.getNonEmptyAndVisibleLabels()
    var cards = database.getAllCards()
    var navigateToEditCard = MutableLiveData<Long?>()

    fun onCardClicked(cardKey: Long) {
        navigateToEditCard.value = cardKey
    }

    fun onEditCardNavigated() {
        navigateToEditCard.value = null
    }
}