package com.alexz.messenger.app.ui.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alexz.firerecadapter.FirebaseMapRecyclerAdapter
import com.alexz.firerecadapter.FirebaseViewHolder
import com.alexz.messenger.app.data.model.imp.User
import com.alexz.messenger.app.data.repo.UserListRepository.getUser
import com.alexz.messenger.app.data.repo.UserListRepository.getUsers
import com.alexz.messenger.app.ui.adapters.UserListRecyclerAdapter.UserViewHolder
import com.alexz.messenger.app.ui.views.AvatarImageView
import com.alexz.messenger.app.util.DateUtil
import com.google.firebase.database.Query
import com.messenger.app.R
import java.util.*

class UserListRecyclerAdapter(private val chatId: String) :
        FirebaseMapRecyclerAdapter<User, UserViewHolder>(User::class.java) {
    override fun onCreateKeyQuery(): Query {
        return getUsers(chatId)
    }

    override fun onCreateModelQuery(modelId: String): Query {
        return getUser(modelId)
    }

    override fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    class UserViewHolder(itemView: View) : FirebaseViewHolder<User>(itemView) {
        private val avatar: AvatarImageView = itemView.findViewById(R.id.user_avatar)
        private val name: TextView = itemView.findViewById(R.id.user_name)
        private val online: TextView = itemView.findViewById(R.id.user_last_online)

        override fun bind(model: User) {
            super.bind(model)
            if (model.imageUri.isNotEmpty()) {
                avatar.setImageURI(Uri.parse(model.imageUri))
            }
            name.text = model.name
            if (model.isOnline) {
                online.text = online.resources.getString(R.string.title_online)
            } else {
                online.text = DateUtil.getTime(Date(model.lastOnline))
            }
        }
    }
}