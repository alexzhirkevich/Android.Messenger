package com.community.botapi.database.imp

import com.community.botapi.database.Config.BOT
import com.community.botapi.database.Config.BOTS
import com.community.botapi.database.Config.MESSAGES
import com.community.botapi.interfaces.IMessage
import com.google.firebase.database.FirebaseDatabase

data class Message protected constructor(
    override val id: String,
    override val chatId: String,
    override val senderId: String,
    override val text: String,
    override val time: Long,
) : IMessage {
    class Builder(private val chatId : String) {

        var text = ""

        fun setText(value: String) : Message.Builder{
            text = value
            return this
        }

        fun build(): Message {
            return Message(
                id = FirebaseDatabase.getInstance().reference.child(BOTS).child(MESSAGES).push().key,
                chatId = chatId,
                senderId = BOT,
                text = text,
                time = System.currentTimeMillis()
            )
        }
    }
}