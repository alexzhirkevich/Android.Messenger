package com.community.messenger.app.ui.adapters.recycler

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.community.messenger.app.R
import com.community.messenger.app.databinding.ItemUserBinding
import com.community.messenger.common.entities.interfaces.IUser
import com.community.messenger.common.util.TimeVisualizer
import com.community.messenger.common.util.dateTime
import com.community.recadapter.BaseRecyclerAdapter
import com.community.recadapter.BaseViewHolder

class UserRecyclerAdapter : BaseRecyclerAdapter<IUser, BaseViewHolder<IUser>>() {

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        BaseViewHolder<IUser>(binding.root) {

        override fun onBind(entity: IUser) {
            with(binding) {

                binding.cbSelected.apply {
                    isVisible = inSelectingMode
                    isChecked = isSelected(entity.id)
                }

                if (entity.imageUri.isNotEmpty()) {
                    binding.ivAvatar.setImageURI(Uri.parse(entity.imageUri))
                } else {
                    binding.ivAvatar.setupWithText(
                        entity.name,
                        R.dimen.font_size_medium,
                        R.color.blue
                    )
                }
                binding.tvName.text = entity.name
                binding.userPhone.text = entity.phone
                val time = entity.lastOnline.dateTime(itemView.context).timeNearly
                binding.tvOnline.text = when {
                    entity.isOnline -> {
                        itemView.resources.getString(R.string.online)
                    }
                    System.currentTimeMillis() - entity.lastOnline < TimeVisualizer.HOUR -> {
                        itemView.resources.getString(R.string.last_online_few, time)
                    }
                    else -> {
                        itemView.resources.getString(R.string.last_online_many, time)
                    }
                }
                binding.vOnlineIndicator.isVisible = entity.isOnline
            }
        }
    }

    override fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }
}