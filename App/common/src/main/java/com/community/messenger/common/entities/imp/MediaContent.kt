package com.community.messenger.common.entities.imp

import android.os.Parcel
import android.os.Parcelable
import com.community.messenger.common.entities.interfaces.IMediaContent

open class MediaContent(
        id : String = "",
        override var type: Int = IMediaContent.IMAGE,
        override var url: String ="" ) : Entity(id),IMediaContent {

    constructor(parcel: Parcel) :
            this(
                    id = parcel.readString().orEmpty(),
                    type = parcel.readInt(),
                    url = parcel.readString().orEmpty()
            )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeInt(type)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MediaContent) return false
        if (!super.equals(other)) return false

        if (type != other.type) return false
        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + type
        result = 31 * result + url.hashCode()
        return result
    }

    override fun toString(): String {
        return "MediaContent(id=$id, type=$type, url='$url')"
    }


    companion object CREATOR : Parcelable.Creator<MediaContent> {
        override fun createFromParcel(parcel: Parcel): MediaContent {
            return MediaContent(parcel)
        }

        override fun newArray(size: Int): Array<MediaContent?> {
            return arrayOfNulls(size)
        }
    }
}