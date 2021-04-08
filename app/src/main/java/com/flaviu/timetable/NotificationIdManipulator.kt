package com.flaviu.timetable

import android.content.Context
import com.flaviu.timetable.database.CardDatabase
import kotlin.math.max

object NotificationIdManipulator {
    suspend fun generateId(context: Context): Int {
        val sharedPref = context.getSharedPreferences("com.flaviu.timetable", Context.MODE_PRIVATE)
        var toReturn = sharedPref.getInt("notificationId", -1)
        if (toReturn == -1) {
            toReturn = 0
            for (card in CardDatabase.getInstance(context).cardDatabaseDao.getAllCardsNow()) {
                if (card.reminderId != null)
                    toReturn = max(toReturn, card.reminderId!!)
                if (card.expirationId != null)
                    toReturn = max(toReturn, card.expirationId!!)
            }
            for (subtask in CardDatabase.getInstance(context).cardDatabaseDao.getAllSubtasksNow())
                if (subtask.reminderId != null)
                    toReturn = max(toReturn, subtask.reminderId!!)
            toReturn += 1
        }
        with(sharedPref.edit()) {
            putInt("notificationId", toReturn+1)
            apply()
        }
        return toReturn
    }
}