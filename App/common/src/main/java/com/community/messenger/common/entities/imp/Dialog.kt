package com.community.messenger.common.entities.imp

import android.os.Parcel
import android.os.Parcelable
import com.community.messenger.common.entities.interfaces.IDialog

class Dialog(
        id : String="",
        override var user1: String="",
        override var user2: String="",
        lastMessageId: String="",
        lastMessageTime: Long=System.currentTimeMillis())
    : Chat(id=  id,lastMessageId = lastMessageId,lastMessageTime = lastMessageTime), IDialog, Parcelable {

    constructor(messageable: Chat, user1: String, user2: String) : this(
            id = messageable.id,
            user1 =  user1,
            user2 = user2,
            lastMessageId =  messageable.lastMessageId,
            lastMessageTime = messageable.lastMessageTime
    )

    init {
        sortedSetOf(user1,user2).apply {
            user1 = first()
            user2 = last()
        }
    }

    constructor(parcel: Parcel) : this(
            Chat.createFromParcel(parcel),
            user1 = parcel.readString().orEmpty(),
            user2 = parcel.readString().orEmpty()
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Dialog) return false
        if (!super.equals(other)) return false

        if (user1 != other.user1) return false
        if (user2 != other.user2) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + user1.hashCode()
        result = 31 * result + user2.hashCode()
        return result
    }

    override fun toString(): String {
        return "Dialog(user1='$user1', user2='$user2', lastMessageId='$lastMessageId', lastMessageTime=$lastMessageTime)"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel,flags)
        parcel.writeString(user1)
        parcel.writeString(user2)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Dialog> {
        override fun createFromParcel(parcel: Parcel): Dialog {
            return Dialog(parcel)
        }

        override fun newArray(size: Int): Array<Dialog?> {
            return arrayOfNulls(size)
        }
    }
}