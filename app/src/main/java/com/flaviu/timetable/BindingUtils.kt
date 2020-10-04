package com.flaviu.timetable

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.flaviu.timetable.database.Card

@BindingAdapter("timeText")
fun TextView.setTimeText(item: Card?) {
    item?.let{
        var begin = item.timeBegin.toString()
        var end = item.timeEnd.toString()
        if (begin.length == 1)
            begin = "0$begin"
        if (end.length == 1)
            end = "0$end"
        text="Time: $begin-$end"
    }
}

@BindingAdapter("placeText")
fun TextView.setPlaceText(item: Card?) {
    item?.let{
        text="Place: ${item.place}"
    }
}

@BindingAdapter("nameText")
fun TextView.setNameText(item: Card?) {
    item?.let{
        text="Name: ${item.name}"
    }
}

@BindingAdapter("infoText")
fun TextView.setInfoText(item: Card?) {
    item?.let{
        text="Description: ${item.info}"
    }
}