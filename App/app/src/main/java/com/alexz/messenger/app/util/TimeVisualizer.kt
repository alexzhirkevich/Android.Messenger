package com.alexz.messenger.app.util

import com.alexz.messenger.app.data.ChatApplication.Companion.AppContext
import com.messenger.app.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

private val RUSSIAN = Locale("ru","RU")

class TimeVisualizer(private var ms : Long) {

    companion object {
        const val hour = 3600
        const val day = hour * 24
        const val week = day * 7
        const val month = 30 * day
        const val year = day * 365
    }

    val time: String
        get() = run {
            val diff = abs(System.currentTimeMillis() - ms)
            when {
                diff >= year ->
                    "${abs(diff / year)}${AppContext.getString(R.string.short_year)}"
                diff >= month ->
                    "${abs(diff / month)}${AppContext.getString(R.string.short_month)}"
                diff >= week ->
                    "${diff / week}${AppContext.getString(R.string.short_week)}"
                diff >= day ->
                    "${diff / day}${AppContext.getString(R.string.short_day)}"
                diff >= hour ->
                    "${diff / hour}${AppContext.getString(R.string.short_hour)}"
                else -> SimpleDateFormat("H:mm", Locale.getDefault()).format(Date(ms))
            }
        }

    fun dayMonth(time : Long): String {
        val pattern = when(Locale.getDefault()){
            RUSSIAN -> "dd MMMM"
            else -> "MMMM dd"
        }
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(time))
    }
}

fun Long.timeVisualizer() = TimeVisualizer(this)