package com.community.messenger.common.entities.imp

import android.os.Parcel
import android.os.Parcelable
import com.community.messenger.common.entities.interfaces.IChannel
import com.community.messenger.common.entities.interfaces.IEntity
import java.util.*


class Channel(
    id : String = "",
    name : String= "",
    override var tag: String="",
    override var description: String="",
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
            tag = parcel.readString().orEmpty(),
            description = parcel.readString().orEmpty(),
            imageUri = parcel.readString().orEmpty(),
            lastPostId = parcel.readString().orEmpty(),
            lastPostTime = parcel.readLong(),
            creatorId = parcel.readString().orEmpty(),
            creationTime = parcel.readLong()) {
        searchName = parcel.readString().orEmpty()
    }

//    constructor(d: Channel) : this(
//            id = d.id,
//            name = d.name,
//            tag = d.tag,
//            description = d.description,
//            imageUri = d.imageUri,
//            lastPostId = d.lastPostId,
//            lastPostTime = d.lastPostTime,
//            creatorId = d.creatorId,
//            creationTime = d.creationTime,
//            subscribers = d.subscribers)


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(tag)
        parcel.writeString(description)
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
        return if (other !is Channel) 0
            else other.lastPostTime.compareTo(lastPostTime)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Channel) return false
        if (!super.equals(other)) return false

        if (tag != other.tag) return false
        if (description != other.description) return false
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
        result = 31 * result + tag.hashCode()
        result = 31 * result + description.hashCode()
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
        return "Channel(tag='$tag', description='$description', imageUri='$imageUri', lastPostId='$lastPostId', lastPostTime=$lastPostTime, creatorId='$creatorId', creationTime=$creationTime, name='$name', searchName='$searchName')"
    }


    companion object CREATOR : Parcelable.Creator<Channel> {

        override fun createFromParcel(parcel: Parcel): Channel {
            return Channel(parcel)
        }

        override fun newArray(size: Int): Array<Channel?> {
            return arrayOfNulls(size)
        }
    }
}