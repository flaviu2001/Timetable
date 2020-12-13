package com.flaviu.timetable

import android.app.Activity
import android.content.Context

object NotificationIdManipulator {
    fun generateId(activity: Activity): Int {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        val toReturn = sharedPref.getInt("notificationId", 1)
        with(sharedPref.edit()) {
            putInt("notificationId", toReturn+1)
            apply()
        }
        return toReturn
    }
}