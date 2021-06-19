package com.alexz.messenger.app.data.entities.imp

import android.os.Parcel
import android.os.Parcelable
import com.alexz.firerecadapter.IEntity
import com.alexz.messenger.app.data.entities.interfaces.IChat
import com.alexz.messenger.app.util.FirebaseUtil

class Chat(
        id : String = "",
        override var name: String = "",
        override var imageUri: String = "",
        override var creatorId: String = "",
        override var creationTime: Long = 0,
        lastMessageId : String = "",
        lastMessageTime: Long = Long.MAX_VALUE
) : Messageable(id = id,lastMessageId = lastMessageId, lastMessageTime = lastMessageTime), IChat, Parcelable {

    constructor(messageable: Messageable, name: String,imageUri: String,creatorId: String,creationTime: Long) :
        this(
                id = messageable.id,
                name =  name,
                imageUri = imageUri,
                creatorId = creatorId,
                creationTime = creationTime,
                lastMessageId = messageable.lastMessageId,
                lastMessageTime = messageable.lastMessageTime)


    constructor(di: Chat) :this(
            id = di.id,
            name = di.name,
            imageUri = di.imageUri,
            lastMessageId = di.lastMessageId,
            lastMessageTime = di.lastMessageTime,
            creatorId = di.creatorId,
            creationTime = di.creationTime)

    private constructor(parcel: Parcel) :this(
        Messageable.createFromParcel(parcel),
        name = parcel.readString().orEmpty(),
        imageUri = parcel.readString().orEmpty(),
        creatorId = parcel.readString().orEmpty(),
        creationTime = parcel.readLong()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest,flags)
        dest.writeString(name)
        dest.writeString(imageUri)
        dest.writeString(creatorId)
        dest.writeLong(creationTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun compareTo(other: IEntity): Int {
        if (other is Chat) {
            if (lastMessageTime != 0L && other.lastMessageTime != 0L)
                return lastMessageTime.compareTo(other.lastMessageTime)
            return other.creationTime.compareTo(other.creationTime)
        }
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Chat> {

        const val TABLE_NAME = FirebaseUtil.CHATS

        override fun createFromParcel(parcel: Parcel): Chat? {
            return Chat(parcel)
        }

        override fun newArray(size: Int): Array<Chat?> {
            return arrayOfNulls(size)
        }
    }
}