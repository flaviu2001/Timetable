package com.flaviu.timetable

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import java.lang.Exception
import java.util.*

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

fun timeFormatter(hour: Int, minutes: Int): String {
    var hourStr = hour.toString()
    if (hourStr.length == 1)
        hourStr = "0$hourStr"
    var minutesStr = minutes.toString()
    if (minutesStr.length == 1)
        minutesStr = "0$minutesStr"
    return "$hourStr:$minutesStr"
}

fun editTextTimeDialogInject(context: Context?, editText: EditText) {
    editText.setOnClickListener{
        val hour = 0
        val minute = 0
        val timePicker: TimePickerDialog
        timePicker = TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute -> editText.setText(
                timeFormatter(selectedHour, selectedMinute)) },
            hour,
            minute,
            true
        )
        timePicker.setTitle("Select Time")
        timePicker.show()
    }
}

fun intToWeekday(day: Int): String {
    return listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")[day]
}

fun weekdayToInt(day: String): Int {
    val l = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    for (i in l.indices)
        if (l[i] == day)
            return i
    throw Exception("Bad")
}

fun editTextWeekdayDialogInject(context: Context?, editText: EditText) {
    editText.setOnClickListener{
        var selectedItem: Int? = null
        if (context == null)
            return@setOnClickListener
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Set your weekday")
            .setSingleChoiceItems(R.array.weekdays, -1
            ) { _, which ->
                selectedItem = which
            }
            .setPositiveButton("Ok"
            ) { _, _ ->
                if (selectedItem != null)
                    editText.setText(intToWeekday(selectedItem!!))
            }
            .setNegativeButton("Cancel"
            ) { _, _ ->
                return@setNegativeButton
            }

        builder.create()
        builder.show()

    }
}