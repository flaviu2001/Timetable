package com.flaviu.timetable

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