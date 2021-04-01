package com.alexz.messenger.app.data.model.imp

import android.os.Parcel
import android.os.Parcelable
import com.alexz.firerecadapter.BaseModel
import com.alexz.messenger.app.data.model.interfaces.IPost

class Post constructor(override var channelId: String) : BaseModel(channelId), IPost, Parcelable {
    override var text: String = ""
    override var content: List<String> = ArrayList()
    override var time: Long = 0

    constructor(parcel: Parcel) : this(channelId = parcel.readString().orEmpty()) {
        text = parcel.readString().orEmpty()
        content = parcel.createStringArrayList() ?: ArrayList()
        time = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(channelId)
        parcel.writeString(text)
        parcel.writeStringList(content)
        parcel.writeLong(time)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Post) return false
        if (!super.equals(other)) return false

        if (channelId != other.channelId) return false
        if (text != other.text) return false
        if (content != other.content) return false
        if (time != other.time) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + channelId.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + time.hashCode()
        return result
    }

    override fun toString(): String {
        return "Post(channelId='$channelId', text='$text', content=$content, time=$time)"
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}