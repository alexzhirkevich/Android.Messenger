package com.alexz.messenger.app.data.entities.imp

import android.os.Parcel
import android.os.Parcelable
import com.alexz.firerecadapter.Entity
import com.alexz.firerecadapter.IEntity
import com.alexz.messenger.app.data.entities.interfaces.IChannel
import com.alexz.messenger.app.util.FirebaseUtil
import java.util.*


class Channel(
        id : String = "",
        name : String= "",
        override var imageUri: String = "",
        override var lastPostId: String = "",
        override var lastPostTime: Long = Long.MAX_VALUE,
        override var creatorId: String = "",
        override var creationTime: Long = System.currentTimeMillis())
    : Entity(id), IChannel, Parcelable {

    override var name: String = name.trim()
        set(value) {
            field = value.trim()
            searchName = field.toLowerCase(Locale.getDefault())
        }
    var searchName: String = name.filter { !it.isWhitespace() }.toLowerCase(Locale.getDefault())

    constructor(parcel: Parcel) : this(
            id = parcel.readString().orEmpty(),
            name = parcel.readString().orEmpty(),
            imageUri = parcel.readString().orEmpty(),
            lastPostId = parcel.readString().orEmpty(),
            lastPostTime = parcel.readLong(),
            creatorId = parcel.readString().orEmpty(),
            creationTime = parcel.readLong()) {
        searchName = parcel.readString().orEmpty()
    }

    constructor(d: Channel) : this(
            id = d.id,
            name = d.name,
            imageUri = d.imageUri,
            lastPostId = d.lastPostId,
            lastPostTime = d.lastPostTime,
            creatorId = d.creatorId,
            creationTime = d.creationTime)


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(imageUri)
        parcel.writeString(lastPostId)
        parcel.writeLong(lastPostTime)
        parcel.writeString(creatorId)
        parcel.writeLong(creationTime)
        parcel.writeString(searchName)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun compareTo(other: IEntity): Int {
        if (other is Channel){
            lastPostTime.compareTo(other.lastPostTime).let {
                if (it !=0)
                    return it
            }
            return creationTime.compareTo(other.creationTime)
        }
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Channel) return false
        if (!super.equals(other)) return false
        if (imageUri != other.imageUri) return false
        if (lastPostId != other.lastPostId) return false
        if (lastPostTime != other.lastPostTime) return false
        if (creatorId != other.creatorId) return false
        if (creationTime != other.creationTime) return false
        if (name != other.name) return false
        if (searchName != other.searchName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + imageUri.hashCode()
        result = 31 * result + lastPostId.hashCode()
        result = 31 * result + lastPostTime.hashCode()
        result = 31 * result + creatorId.hashCode()
        result = 31 * result + creationTime.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + searchName.hashCode()
        return result
    }

    override fun toString(): String {
        return "Channel(imageUri='$imageUri', lastPostId='$lastPostId', lastPostTime=$lastPostTime, creatorId='$creatorId', creationTime=$creationTime,  name='$name', searchName='$searchName')"
    }


    companion object CREATOR : Parcelable.Creator<Channel> {

        const val TABLE_NAME = FirebaseUtil.CHANNELS

        override fun createFromParcel(parcel: Parcel): Channel {
            return Channel(parcel)
        }

        override fun newArray(size: Int): Array<Channel?> {
            return arrayOfNulls(size)
        }
    }
}