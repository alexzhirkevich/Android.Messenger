package com.alexz.messenger.app.data.model.imp

import android.os.Parcel
import android.os.Parcelable
import com.alexz.messenger.app.data.model.interfaces.IChat

class Chat(override var imageUri: String = "",
           override var name: String = "Chat Name",
           override var creatorId: String = "",
           override var creationTime: Long = 0,
           override var lastMessage: Message? = null,
           override var isGroup: Boolean = false

) : BaseModel(""), IChat, Parcelable, Comparable<Chat> {

    constructor(di: Chat) :
            this(di.imageUri, di.name, di.creatorId, di.creationTime, di.lastMessage, di.isGroup){
        id = di.id
    }

    private constructor(parcel: Parcel) : this(
            imageUri = parcel.readString().orEmpty(),
            name = parcel.readString().orEmpty(),
            creatorId = parcel.readString().orEmpty(),
            creationTime = parcel.readLong(),
            lastMessage = parcel.readParcelable(Message::class.java.classLoader),
            isGroup = parcel.readByte().toInt() != 0) {
        id = parcel.readString().orEmpty()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(imageUri)
        dest.writeString(name)
        dest.writeString(creatorId)
        dest.writeLong(creationTime)
        dest.writeParcelable(lastMessage, flags)
        dest.writeByte((if (isGroup) 1 else 0).toByte())
        dest.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun compareTo(c: Chat): Int {
        if (c.lastMessage != null && lastMessage != null) {
            return c.lastMessage!!.time.compareTo(lastMessage!!.time)
        }
        return c.creationTime.compareTo(c.creationTime)
    }

    companion object CREATOR : Parcelable.Creator<Chat> {
        override fun createFromParcel(`in`: Parcel): Chat? {
            return Chat(`in`)
        }

        override fun newArray(size: Int): Array<Chat?> {
            return arrayOfNulls(size)
        }
    }
}