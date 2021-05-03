package com.alexz.messenger.app.util

import java.text.SimpleDateFormat
import java.util.*

private val RUSSIAN = Locale("ru","RU")

fun getDayMonth(time : Long): String {
    val pattern = when(Locale.getDefault()){
        RUSSIAN -> "dd MMMM"
        else -> "MMMM dd"
    }
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(Date(time))
}

fun getTime(time : Long): String {
    val sdf = SimpleDateFormat("k:mm", Locale.getDefault())
    return sdf.format(Date(time))
}