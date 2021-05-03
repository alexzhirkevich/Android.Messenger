package com.alexz.messenger.app.data.entities.imp

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.alexz.messenger.app.data.entities.interfaces.IMediaMessage
import com.alexz.messenger.app.util.FirebaseUtil


@Entity(tableName = MediaMessage.TABLE_NAME)
class MediaMessage: Message, IMediaMessage<MediaContent>, Parcelable {

    @ColumnInfo(name = "media_content",defaultValue = "")
    override var mediaContent: List<MediaContent> = ArrayList()

    protected constructor() :super()

    constructor(chatId : String) :super(chatId)

    private constructor(parcel: Parcel) : super(parcel) {
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

        const val TABLE_NAME = FirebaseUtil.MEDIA_MESSAGES

        override fun createFromParcel(parcel: Parcel): MediaMessage {
            return MediaMessage(parcel)
        }

        override fun newArray(size: Int): Array<MediaMessage?> {
            return arrayOfNulls(size)
        }
    }
}