package com.alexz.messenger.app.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexz.messenger.app.data.model.imp.Chat
import com.alexz.messenger.app.data.model.imp.Message
import com.alexz.messenger.app.data.repo.DialogsRepository.getChat
import com.alexz.messenger.app.data.repo.MessagesRepository
import com.alexz.messenger.app.ui.common.firerecyclerview.Listenable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.messenger.app.BuildConfig

class ChatActivityViewModel : ViewModel(), Listenable {

    private val chatInfoChanged = MutableLiveData<Chat>()
    private var listener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                chatInfoChanged.postValue(snapshot.getValue(Chat::class.java))
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Chat info updated")
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Error while updating chat info")
            }
        }
    }
    private var chatRef: DatabaseReference? = null
    var chatId: String? = null
    val chatChangingState: LiveData<Chat>
        get() = chatInfoChanged

    override fun startListening() {
        chatRef = getChat(chatId)
        chatRef?.addValueEventListener(listener!!)
    }

    override fun stopListening() {
        listener.let { chatRef?.removeEventListener(it) }
    }

    fun sendMessage(m: Message, replace: String) {
        MessagesRepository.sendMessage(m, replace)
    }

    fun deleteMessage(m: Message?) {
        MessagesRepository.deleteMessage(m!!)
    }

    companion object {
        private val TAG = ChatActivityViewModel::class.java.simpleName
    }
}