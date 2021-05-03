package com.alexz.messenger.app.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaRecorder
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.alexz.messenger.app.ChatApplication.Companion.AppContext
import com.alexz.messenger.app.util.VibrateUtil
import com.messenger.app.R
import io.reactivex.rxjava3.core.Maybe

@SuppressLint("ClickableViewAccessibility")
class MessageInput : LinearLayout, View.OnTouchListener,TextWatcher,View.OnClickListener {

    companion object{
        val INPUT_VOICE = 1
        val INPUT_TEXT = 2
    }

    private val sendButton: ImageButton
    val attachButton: ImageButton
    private val inputTextView: TextView
    val progressBar: ProgressBar
    private lateinit var recorder :MediaRecorder
    var onVoiceTouched: (View) -> Unit = {}
    var onVoiceReleased: (View) -> Unit = {}
    var onSendClicked: (View) -> Unit = {}
    var onAttachClicked: (View) -> Unit = {}
    var text : String
     get() = inputTextView.text.toString()
        set(value) {
            inputTextView.text = value
        }

    var recorderOutput : String =
            "${AppContext.cacheDir.absolutePath}/voice${System.currentTimeMillis()}.mp4"

    var inVoiceMode = true
        set(value) {
            if (isModCanBeChanged) {
                if (!inVoiceMode && value) {
                    sendButton.setOnTouchListener(this)
                    sendButton.setOnClickListener(null)
                    sendButton.setImageResource(R.drawable.ic_mic)
                } else if (inVoiceMode && !value) {
                    sendButton.setOnTouchListener(null)
                    sendButton.setOnClickListener(onSendClicked)
                    sendButton.setImageResource(R.drawable.ic_send)
                }
                field = value
            }
        }
    var isVoiceTouched = false
        private set
    var isRecording = false
        private set

    var isModCanBeChanged = true;

    private var recordTime : Long = 0
    private val vibrator = VibrateUtil.with(context)


    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init{
        View.inflate(context, R.layout.view_message_input, this)
        sendButton = findViewById(R.id.button_message_send)
        inputTextView = findViewById(R.id.edit_message_input)
        attachButton = findViewById(R.id.button_message_attach)
        progressBar = findViewById(R.id.input_attach_progress)
        attachButton.setColorFilter(ContextCompat.getColor(attachButton.context, R.color.color_primary))
        sendButton.setOnTouchListener(this)
        inputTextView.addTextChangedListener(this)
    }

    fun startRecording(){
        Thread {
            try {
                if (!isRecording) {
                    recorder = MediaRecorder().apply {
                        setAudioSource(MediaRecorder.AudioSource.MIC)
                        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                        setAudioChannels(1)
                        setAudioEncodingBitRate(128000)
                        setAudioSamplingRate(44100)
                        recorderOutput?.let { setOutputFile(it) }
                        prepare()
                        start()
                    }
                    recordTime = System.currentTimeMillis()
                    isRecording = true
                }
            } catch (ignore: IllegalStateException) {
            }
        }.start()
    }

    fun stopRecording() : Maybe<Long> = Maybe.create {
        Thread {
            try {
                if (isRecording) {
                    recorder.stop()
                    recorder.release()
                    it.onSuccess(System.currentTimeMillis() - recordTime)
                    recordTime = 0
                    isRecording = false
                }
            } catch (t: Throwable) {
                it.tryOnError(t)
            }
        }.start()
    }

    override fun onTouch(v: View, m: MotionEvent): Boolean {
        return if (inVoiceMode) {
            when (m.action) {
                MotionEvent.ACTION_DOWN -> {
                    vibrator.vibrate(20,VibrateUtil.POWER_LOW)
                    onVoiceTouched.invoke(v)
                    true
                }
                MotionEvent.ACTION_UP ->{
                    onVoiceReleased.invoke(v)
                    false
                }
                else -> super.onTouchEvent(m)
            }
        } else false
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        inVoiceMode = p0 == null || p0.isEmpty()
    }


    protected fun finalize(){
        recorder.release()
    }

    override fun onClick(v: View) {
        onSendClicked.invoke(v)
    }
}