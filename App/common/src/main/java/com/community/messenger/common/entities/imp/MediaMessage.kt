package com.community.messenger.common.entities.imp

import android.os.Parcel
import android.os.Parcelable
import com.community.messenger.common.entities.interfaces.IMediaMessage


//@Entity(tableName = MediaMessage.TABLE_NAME,
//        inheritSuperIndices = true)
class MediaMessage(
        id : String="",
        chatId: String="",
        text:String="",
        senderId: String="",
        time: Long=System.currentTimeMillis(),
        isPrivate: Boolean = false,
        override var mediaContent: List<MediaContent> = listOf())
    : Message(id = id,chatId = chatId,text = text,senderId = senderId,time = time,isPrivate = isPrivate),
        IMediaMessage<MediaContent>, Parcelable {

    constructor(message: Message,mediaContent: List<MediaContent> = listOf()) : this(
            id = message.id,
            chatId = message.chatId,
            text = message.text,
            senderId = message.senderId,
            time = message.time,
            isPrivate = message.isPrivate,
            mediaContent = mediaContent
    )

    private constructor(parcel: Parcel) : this(Message.createFromParcel(parcel)) {
        parcel.readList(mediaContent, MediaContent::class.java.classLoader)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeList(mediaContent)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MediaMessage) return false
        if (!super.equals(other)) return false

        if (mediaContent != other.mediaContent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + mediaContent.hashCode()
        return result
    }

    override fun toString(): String {
        return "MediaMessage(mediaContent=$mediaContent)"
    }

    companion object CREATOR : Parcelable.Creator<MediaMessage> {


        override fun createFromParcel(parcel: Parcel): MediaMessage {
            return MediaMessage(parcel)
        }

        override fun newArray(size: Int): Array<MediaMessage?> {
            return arrayOfNulls(size)
        }
    }
}