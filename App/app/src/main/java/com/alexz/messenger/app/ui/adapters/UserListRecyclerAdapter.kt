//package com.alexz.messenger.app.ui.adapters
//
//import android.net.Uri
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import com.alexz.firerecadapter.firestore.FirestoreMapRecyclerAdapter
//import com.alexz.firerecadapter.viewholder.FirebaseViewHolder
//import com.alexz.messenger.app.data.entities.imp.User
//import com.alexz.messenger.app.ui.adapters.UserListRecyclerAdapter.UserViewHolder
//import com.alexz.messenger.app.ui.views.AvatarImageView
//import com.alexz.messenger.app.util.FirebaseUtil.USERS
//import com.alexz.messenger.app.util.FirebaseUtil.chatsCollection
//import com.alexz.messenger.app.util.FirebaseUtil.usersCollection
//import com.alexz.messenger.app.util.getTime
//import com.google.firebase.firestore.DocumentReference
//import com.messenger.app.R
//
//class UserListRecyclerAdapter(private val chatId: String) :
//        FirestoreMapRecyclerAdapter<User, UserViewHolder>(User::class.java,
//                chatsCollection.document(chatId).collection(USERS)
//        ) {
//
//
//    override fun onCreateEntityReference(id: String): DocumentReference =
//            usersCollection.document(id)
//
//    override fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
//        return UserViewHolder(view)
//    }
//
//    class UserViewHolder(itemView: View) : FirebaseViewHolder<User>(itemView) {
//        private val avatar: AvatarImageView = itemView.findViewById(R.id.user_avatar)
//        private val name: TextView = itemView.findViewById(R.id.user_name)
//        private val online: TextView = itemView.findViewById(R.id.user_last_online)
//
//        override fun bind(entity: User) {
//            super.bind(entity)
//            if (entity.imageUri.isNotEmpty()) {
//                avatar.setImageURI(Uri.parse(entity.imageUri))
//            }
//            name.text = entity.name
//            if (entity.isOnline) {
//                online.text = online.resources.getString(R.string.title_online)
//            } else {
//                online.text = getTime(entity.lastOnline)
//            }
//        }
//    }
//
//}