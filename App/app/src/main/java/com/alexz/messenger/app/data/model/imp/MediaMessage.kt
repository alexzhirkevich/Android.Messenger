package com.alexz.messenger.app.data.model.imp

import android.os.Parcel
import android.os.Parcelable
import com.alexz.messenger.app.data.model.interfaces.IMediaMessage

class MediaMessage: Message,IMediaMessage<MediaContent>, Parcelable {
    override var mediaContent: List<MediaContent> = ArrayList()

    protected constructor() :super("")

    constructor(chatId : String) :super(chatId)

    private constructor(parcel: Parcel) : super(parcel){
        parcel.readList(mediaContent,MediaContent::class.java.classLoader)
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