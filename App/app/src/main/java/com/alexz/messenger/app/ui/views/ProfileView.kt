package com.alexz.messenger.app.ui.views

import android.animation.ValueAnimator
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import com.alexz.messenger.app.data.entities.interfaces.IUser
import com.alexz.messenger.app.util.timeVisualizer
import com.messenger.app.R

class ProfileView @JvmOverloads constructor(context: Context,attributeSet: AttributeSet?=null,defStyle : Int=0)
    : LinearLayout(context,attributeSet,defStyle){

    init {
        View.inflate(context, R.layout.layout_profile,this)
    }

    private val ivAvatar = findViewById<AvatarImageView>(R.id.profile_avatar)
    private val tvName = findViewById<TextView>(R.id.profile_name)
    private val tvUsername = findViewById<AutoLinkTextView>(R.id.profile_username)
    private val tvDescription = findViewById<AutoLinkTextView>(R.id.profile_description_text)
    private val tvOnline = findViewById<TextView>(R.id.profile_online)

    private val titleDescription = findViewById<TextView>(R.id.tv_profile_edit_description)

    private val avatarChange = findViewById<View>(R.id.profile_avatar_change)
    private val pbUploading = findViewById<ProgressBar>(R.id.profile_pb)

    var isSelf : Boolean = true
    set(value) {
        field = value
        avatarChange.isVisible = value
    }

    var online : String
        get() = tvOnline.text.toString()
        set(value)  {
            tvOnline.isVisible = value.isNotEmpty()
            tvOnline.text = value
        }

    var avatarUrl : Uri?
        get() = ivAvatar.imageUri
        set(value) {
            if (value.toString().isNotEmpty()) ivAvatar.setImageURI(value)
            else {
                ivAvatar.setupWithText(name,R.dimen.font_size_large,R.color.blue)
            }
        }

    var name : String
        get() = tvName.text.toString()
        set(value) {
            tvName.text = value
        }

    var userName : String
        get() = tvUsername.text.toString()
        set(value) {
            tvUsername.isVisible = value.isNotEmpty()
            tvUsername.text = value
        }
    var description : String
        get() = tvDescription.text.toString()
        set(value) {
            tvDescription.isVisible = value.isNotEmpty()
            titleDescription.isVisible = value.isNotEmpty()
            tvDescription.text = value
        }

    var uploadingProgress : Float
    get() = pbUploading.progress/100f
    set(value) {
        ValueAnimator.ofInt((uploadingProgress * 100).toInt(), (value * 100).toInt()).apply {
            addUpdateListener {
                pbUploading.progress = it.animatedValue as Int
                if (it.animatedValue as Int >= 99){
                    pbUploading.progress =0
                }
            }
            duration = context.resources.getInteger(R.integer.anim_duration_short).toLong()
        }.start()
    }

    fun setOnAvatarChangeClickListener(listener : OnClickListener)=
            avatarChange.setOnClickListener(listener)

    fun setOnAvatarChangeClickListener(listener : (View) -> Unit)=
            avatarChange.setOnClickListener(listener)

    fun setOnAvatarClickListener(listener : OnClickListener)=
            ivAvatar.setOnClickListener(listener)

    fun setOnAvatarClickListener(listener : (View) -> Unit)=
            ivAvatar.setOnClickListener(listener)
}



fun ProfileView.setUser(user : IUser){
    name = user.name
    avatarUrl = Uri.parse(user.imageUri)
    userName = if (user.username.isNotEmpty()) "@"+user.username else ""
    description = user.description
    online = if (user.isOnline)  {
        resources.getString(R.string.online)
    } else {
        val lastOnline = user.lastOnline.timeVisualizer().timeNearly
        resources.getString(
                if (System.currentTimeMillis() - user.lastOnline < 3_600_000)
                    R.string.last_online_few
                else
                    R.string.last_online_many,
                lastOnline
        )
    }
}