package com.alexz.messenger.app.util

import com.alexz.messenger.app.data.ChatApplication.Companion.AppContext
import com.messenger.app.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

private val RUSSIAN = Locale("ru","RU")

class TimeVisualizer(private var ms : Long) {

    companion object {
        const val hour : Long = 3_600_000
        const val day : Long = hour * 24
        const val week : Long = day * 7
        const val month : Long = 30 * day
        const val year :Long = day * 365
    }

    val time: CharSequence
    get() = SimpleDateFormat("H:mm",Locale.getDefault()).format(ms)

    val timeNearly: CharSequence
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

    val dateNoYear: CharSequence
        get() =  SimpleDateFormat("dd.MM", Locale.getDefault()).format(ms)

}

fun Long.timeVisualizer() = TimeVisualizer(this)