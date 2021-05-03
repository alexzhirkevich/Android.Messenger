package com.alexz.messenger.app.data.entities.imp

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import com.alexz.firerecadapter.Entity
import com.alexz.messenger.app.data.entities.interfaces.IPost
import com.alexz.messenger.app.util.FirebaseUtil

@androidx.room.Entity(
        tableName = Chat.TABLE_NAME,
        foreignKeys = [
            ForeignKey(entity = Channel::class, parentColumns = ["id"],childColumns = ["channel_id"],onDelete = ForeignKey.CASCADE)
        ]
)
class Post(
        id : String = "",
        @ColumnInfo(name = "channel_id")
        override var channelId: String = "",
        @ColumnInfo(name = "text")
        override var text: String = "",
        @ColumnInfo(name = "time")
        override var time: Long = System.currentTimeMillis(),
        @ColumnInfo(name = "content")
        override var content: MutableList<MediaContent> = mutableListOf()
        ) : Entity(channelId), IPost, Parcelable {

    constructor(parcel: Parcel) : this(
            id = parcel.readString().orEmpty(),
            channelId = parcel.readString().orEmpty(),
            text  = parcel.readString().orEmpty(),
            time = parcel.readLong()) {
        parcel.readList(content, MediaContent::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(channelId)
        parcel.writeString(text)
        parcel.writeLong(time)
        parcel.writeList(content)
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
        if (time != other.time) return false
        if (content != other.content) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + channelId.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + content.hashCode()
        return result
    }

    override fun toString(): String {
        return "Post(id = '$id', channelId='$channelId', text='$text',  time=$time, content=$content)"
    }

    companion object CREATOR : Parcelable.Creator<Post> {

        const val TABLE_NAME = FirebaseUtil.POSTS

        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}