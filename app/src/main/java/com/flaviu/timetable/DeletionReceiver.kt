package com.flaviu.timetable

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.flaviu.timetable.database.CardDatabase
import kotlinx.coroutines.*
import java.util.*

class DeletionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            null -> {
                val cardId = intent.getLongExtra("cardId", 0)
                val job = Job()
                val scope = CoroutineScope(Dispatchers.Main + job)
                scope.launch {
                    withContext(Dispatchers.IO) {
                        val dao = CardDatabase.getInstance(context).cardDatabaseDao
                        dao.deleteCard(cardId)
                    }
                }
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                val job = Job()
                val scope = CoroutineScope(Dispatchers.Main + job)
                scope.launch {
                    val dao = CardDatabase.getInstance(context).cardDatabaseDao
                    val cards = dao.getAllCardsNow()
                    val currentTime = Calendar.getInstance()
                    currentTime.add(Calendar.MINUTE, -10)
                    for (card in cards)
                        if (card.reminderDate != null && card.reminderDate!! > currentTime)
                            scheduleDeletion(
                                context,
                                card.cardId,
                                card.expirationId!!,
                                card.reminderDate
                            )
                }
            }
        }

    }

    companion object {
        private const val CHANNEL_ID = "com.flaviu.timetable.channelId"
    }
}
