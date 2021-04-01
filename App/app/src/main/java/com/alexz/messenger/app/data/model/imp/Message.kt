package com.alexz.messenger.app.data.model.imp

import android.os.Parcel
import android.os.Parcelable
import com.alexz.firerecadapter.BaseModel
import com.alexz.messenger.app.data.model.interfaces.IMessage
import com.alexz.messenger.app.util.FirebaseUtil.getCurrentUser

open class Message(override var chatId: String,
                   override var text: String = "",
                   override var senderId: String = getCurrentUser().id,
                   override var senderName: String = getCurrentUser().name,
                   override var senderPhotoUrl: String = getCurrentUser().imageUri.toString(),
                   override var time: Long = System.currentTimeMillis(),
                   override var isPrivate: Boolean = false) : BaseModel(""), IMessage,Parcelable {

    private constructor() : this("")

    constructor(m: Message) :
            this(m.chatId,m.text,m.senderId,m.senderName,m.senderPhotoUrl,m.time,m.isPrivate){
        id = m.id
    }

    protected constructor(parcel: Parcel) : this(
                chatId = parcel.readString().orEmpty(),
                text = parcel.readString().orEmpty(),
                senderId = parcel.readString().orEmpty(),
                senderName = parcel.readString().orEmpty(),
                senderPhotoUrl = parcel.readString().orEmpty(),
                time = parcel.readLong(),
                isPrivate = parcel.readByte().toInt() != 0)

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(chatId)
        dest.writeString(text)
        dest.writeString(senderId)
        dest.writeString(senderName)
        dest.writeString(senderPhotoUrl)
        dest.writeLong(time)
        dest.writeByte((if (isPrivate) 1 else 0).toByte())
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Message) return false
        if (!super.equals(other)) return false

        if (chatId != other.chatId) return false
        if (text != other.text) return false
        if (senderId != other.senderId) return false
        if (senderName != other.senderName) return false
        if (senderPhotoUrl != other.senderPhotoUrl) return false
        if (time != other.time) return false
        if (isPrivate != other.isPrivate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + chatId.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + senderId.hashCode()
        result = 31 * result + senderName.hashCode()
        result = 31 * result + senderPhotoUrl.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + isPrivate.hashCode()
        return result
    }

    override fun toString(): String {
        return buildString {
            append("Message(chatId='$chatId', text='$text', senderId='$senderId', ")
            append("senderName='$senderName', senderPhotoUrl='$senderPhotoUrl', ")
            append("time=$time, isPrivate=$isPrivate)")
        }
    }

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }
}