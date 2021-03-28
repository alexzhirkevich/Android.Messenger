package com.alexz.messenger.app.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.messenger.app.R

class MessageInput : LinearLayout {
    lateinit var sendButton: ImageButton
        private set
    lateinit var attachButton: ImageButton
        private set
    lateinit var inputTextView: TextView
        private set
    lateinit var progressBar: ProgressBar
        private set

    constructor(context: Context, sendButton: ImageButton) : super(context) {
        init(context)

        this.sendButton = sendButton
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        View.inflate(context, R.layout.view_message_input, this)
        sendButton = findViewById(R.id.button_message_send)
        inputTextView = findViewById(R.id.edit_message_input)
        attachButton = findViewById(R.id.button_message_attach)
        progressBar = findViewById(R.id.input_attach_progress)
        attachButton.setColorFilter(ContextCompat.getColor(attachButton.context, R.color.color_primary))
    }
}