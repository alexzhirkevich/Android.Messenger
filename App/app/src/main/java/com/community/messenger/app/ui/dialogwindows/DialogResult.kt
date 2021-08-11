package com.community.messenger.app.ui.dialogwindows

import android.content.Intent

interface DialogResult {
    fun onDialogResult(requestCode: Int, resultCode: Int, resultIntent: Intent?)
}