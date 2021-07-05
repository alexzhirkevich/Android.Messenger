package com.alexz.messenger.app.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.fragment.app.Fragment
import com.messenger.app.BuildConfig

interface FlagManager {
    fun check(pref : String, default: Boolean) : Boolean
    fun set(pref : String, value: Boolean)
    fun remove(pref : String)
}

private class FlagManagerImpl(context: Context) : FlagManager{

    override fun check(pref : String, default: Boolean) : Boolean=
        sharedPreferences.getBoolean(pref,default)


    override fun set(pref : String, value: Boolean) : Unit =
            sharedPreferences.edit().putBoolean(pref,value)
                    .apply()

    override fun remove(pref : String) : Unit =
            sharedPreferences.edit().remove(pref).apply()

    private var sharedPreferences = context.getSharedPreferences(path, MODE_PRIVATE)


    private companion object {
        private val path = BuildConfig.APPLICATION_ID + "SHARED_PREFERENCES"
    }
}

val Fragment.flags : FlagManager
    get() = requireContext().flags

val Context.flags : FlagManager
    get() = FlagManagerImpl(this)