package com.community.messenger.app.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaRecorder
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.community.messenger.app.data.ChatApplication.Companion.AppContext
import com.community.messenger.common.util.VibrateUtil
import com.community.messenger.app.R
import com.community.messenger.app.databinding.LayoutMessageInputBinding
import com.vanniktech.emoji.EmojiPopup
import io.reactivex.Maybe
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@SuppressLint("ClickableViewAccessibility")
class MessageInput : LinearLayout, View.OnTouchListener,TextWatcher,View.OnClickListener {

    companion object{
        const val INPUT_VOICE = 1
        const val INPUT_TEXT = 2
    }

    val binding : LayoutMessageInputBinding


    private lateinit var recorder :MediaRecorder
    var onVoiceTouched: (View) -> Unit = {}
    var onVoiceReleased: (View) -> Unit = {}
    var onSendClicked: (View) -> Unit = {}
    var onAttachClicked: (View) -> Unit = {}
//    var text : String
//     get() = binding.editInput.text.toString()
//        set(value) {
//            binding.editInput.text = value
//        }

    var recorderOutput : String =
            "${AppContext.cacheDir.absolutePath}/voice${System.currentTimeMillis()}.mp4"

    var inVoiceMode = true
        set(value) {
            if (isModCanBeChanged) {
                if (!inVoiceMode && value) {
                    binding.btnSend.setOnTouchListener(this)
                    binding.btnSend.setOnClickListener(null)
                    binding.btnSend.setImageResource(R.drawable.ic_mic)
                } else if (inVoiceMode && !value) {
                    binding.btnSend.setOnTouchListener(null)
                    binding.btnSend.setOnClickListener(onSendClicked)
                    binding.btnSend.setImageResource(R.drawable.ic_send)
                }
                field = value
            }
        }
    var isVoiceTouched = false
        private set
    var isRecording = false
        private set

    var isModCanBeChanged = true

    private var recordTime : Long = 0
    private val vibrator = com.community.messenger.common.util.VibrateUtil.with(context)

    init{

        View.inflate(context, R.layout.layout_message_input, this)
        binding = LayoutMessageInputBinding.bind(this)
        //binding.editInput = findViewById(R.id.edit_input)
        //attachButton = findViewById(R.id.btn_emoji)
        //attachButton.setColorFilter(ContextCompat.getColor(attachButton.context, R.color.color_primary))
        binding.btnSend.setOnTouchListener(this)
        binding.editInput.addTextChangedListener(this)
        binding.btnEmoji.setOnClickListener(this::onEmojiClick)


    }

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)



    val emojiPopup = EmojiPopup.Builder.fromRootView(this)
        .setOnEmojiPopupShownListener { binding.btnEmoji.setImageResource(R.drawable.ic_keyboard) }
        .setOnEmojiPopupDismissListener { binding.btnEmoji.setImageResource(R.drawable.ic_emoji) }
        .build(binding.editInput)

//    val emojiTextView = EmojiTextView(context).apply {
//        setEmojiSizeRes(R.dimen.font_size_large)
//    }

    private fun onEmojiClick(v:View){
//        emojiTextView.text = binding.editInput.text
//
//        addView(emojiTextView)
//        binding.editInput.text?.clear()
        emojiPopup.toggle()
    }

    fun startRecording(){

        GlobalScope.launch {
            try {
                if (!isRecording) {
                    recorder = MediaRecorder().apply {
                        setAudioSource(MediaRecorder.AudioSource.MIC)
                        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                        setAudioChannels(1)
                        setAudioEncodingBitRate(128000)
                        setAudioSamplingRate(44100)
                        setOutputFile(recorderOutput)
                        prepare()
                        start()
                    }
                    recordTime = System.currentTimeMillis()
                    isRecording = true
                }
            } catch (ignore: IllegalStateException) {
            }
        }
    }

    fun stopRecording() : Maybe<Long> = Maybe.create {
        GlobalScope.launch {
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
        }
    }

    override fun onTouch(v: View, m: MotionEvent): Boolean {
        return if (inVoiceMode) {
            when (m.action) {
                MotionEvent.ACTION_DOWN -> {
                    vibrator.vibrate(20, VibrateUtil.POWER_LOW)
                    onVoiceTouched.invoke(v)
                    false
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