package com.alexz.messenger.app.ui.viewholders

import android.net.Uri
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.alexz.firerecadapter.viewholder.BaseViewHolder
import com.alexz.messenger.app.data.entities.interfaces.IUser
import com.alexz.messenger.app.ui.views.AvatarImageView
import com.alexz.messenger.app.util.TimeVisualizer
import com.alexz.messenger.app.util.timeVisualizer
import com.messenger.app.R

class UserViewHolder(itemView: View) : BaseViewHolder<IUser>(itemView){

    private val ivAvatar = itemView.findViewById<AvatarImageView>(R.id.user_avatar)
    private val tvName = itemView.findViewById<TextView>(R.id.user_name)
    private val tvPhone = itemView.findViewById<TextView>(R.id.user_phone)
    private val tvOnline = itemView.findViewById<TextView>(R.id.user_online)
    private val vOnlineIndicator = itemView.findViewById<View>(R.id.user_online_indicator)

    override fun bind(entity: IUser) {
        super.bind(entity)

        if (entity.imageUri.isNotEmpty()) {
            ivAvatar.setImageURI(Uri.parse(entity.imageUri))
        } else{
            ivAvatar.setupWithText(entity.name,R.dimen.font_size_min,R.color.blue)
        }
        tvName.text = entity.name
        tvPhone.text = entity.phone
        val time = entity.lastOnline.timeVisualizer().timeNearly
        tvOnline.text = when {
            entity.isOnline -> {
                itemView.resources.getString(R.string.online)
            }
            System.currentTimeMillis() - entity.lastOnline < TimeVisualizer.hour -> {
                itemView.resources.getString(R.string.last_online_few,time)
            }
            else -> {
                itemView.resources.getString(R.string.last_online_many,time)
            }
        }
        vOnlineIndicator.isVisible = entity.isOnline
    }
}