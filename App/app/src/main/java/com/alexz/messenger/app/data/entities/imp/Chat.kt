package com.alexz.messenger.app.data.entities.imp

import android.os.Parcel
import android.os.Parcelable
import com.alexz.firerecadapter.Entity
import com.alexz.messenger.app.data.entities.interfaces.IChat

open class Chat(
        id: String="",
        override var lastMessageId: String="",
        override var lastMessageTime: Long=Long.MAX_VALUE)
    : Entity(id), IChat,Parcelable {
    constructor(parcel: Parcel) : this(
            id = parcel.readString().orEmpty(),
            lastMessageId = parcel.readString().orEmpty(),
            lastMessageTime = parcel.readLong())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(lastMessageId)
        parcel.writeLong(lastMessageTime)
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Chat) return false
        if (!super.equals(other)) return false

        if (lastMessageId != other.lastMessageId) return false
        if (lastMessageTime != other.lastMessageTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + lastMessageId.hashCode()
        result = 31 * result + lastMessageTime.hashCode()
        return result
    }

    override fun toString(): String {
        return "Messageable(lastMessageId='$lastMessageId', lastMessageTime=$lastMessageTime)"
    }

    companion object CREATOR : Parcelable.Creator<Chat> {
        override fun createFromParcel(parcel: Parcel): Chat {
            return Chat(parcel)
        }

        override fun newArray(size: Int): Array<Chat?> {
            return arrayOfNulls(size)
        }
    }
}