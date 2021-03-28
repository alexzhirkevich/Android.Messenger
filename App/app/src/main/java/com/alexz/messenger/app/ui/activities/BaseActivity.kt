package com.alexz.messenger.app.ui.activities

import androidx.appcompat.app.AppCompatActivity
import com.alexz.messenger.app.ChatApplication

open class BaseActivity : AppCompatActivity() {
    override fun onResume() {
        super.onResume()
        (application as? ChatApplication)?.isRunning = true
    }

    override fun onPause() {
        super.onPause()
        (application as? ChatApplication)?.isRunning = false
    }
}