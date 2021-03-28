package com.alexz.messenger.app.data.model.imp

import android.os.Parcel
import android.os.Parcelable
import com.alexz.messenger.app.data.model.interfaces.IPost
import java.io.IOError

open class Post constructor(override var channelId: String) : BaseModel(channelId), IPost, Parcelable {
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

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}