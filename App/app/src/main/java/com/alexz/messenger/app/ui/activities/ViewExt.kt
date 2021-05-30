package com.alexz.messenger.app.ui.activities

import android.widget.Button
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.core.widget.doOnTextChanged

fun Button.enableWithEditTexts(@ColorRes enableTextClr : Int, @ColorRes disabledTextClr: Int,
                               additionalBlocker : () -> Boolean, vararg companions: EditText) {
    companions.forEach {
        it.doOnTextChanged { _, _, _, _ ->
            isEnabled = companions.all { c -> c.text.isNotEmpty() } && additionalBlocker()
            setTextColor(resources.getColor(if (isEnabled) enableTextClr else disabledTextClr))
        }
    }
}