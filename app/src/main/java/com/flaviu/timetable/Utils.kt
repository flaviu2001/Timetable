package com.flaviu.timetable

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

fun weekdayLongToString(number: Long): String {
    return when(number) {
        0L -> "Monday"
        1L -> "Tuesday"
        2L -> "Wednesday"
        3L -> "Thursday"
        4L -> "Friday"
        5L -> "Saturday"
        6L -> "Sunday"
        else -> "What???"
    }
}

fun hideKeyboard(activity: Activity) {
    val inputMethodManager =
        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    // Check if no view has focus
    val currentFocusedView = activity.currentFocus
    currentFocusedView?.let {
        inputMethodManager.hideSoftInputFromWindow(
            currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}