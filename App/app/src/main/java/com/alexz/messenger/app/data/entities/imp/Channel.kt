package com.alexz.messenger.app.data.entities.imp

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.SET_DEFAULT
import com.alexz.firerecadapter.Entity
import com.alexz.messenger.app.data.entities.interfaces.IChannel
import com.alexz.messenger.app.util.FirebaseUtil
import java.util.*
import kotlin.collections.HashMap

@androidx.room.Entity(
        tableName = Channel.TABLE_NAME,
        foreignKeys = [
            ForeignKey(entity = User::class, parentColumns = ["id"],childColumns = ["creator_id"],onDelete = CASCADE),
            ForeignKey(entity = Post::class, parentColumns = ["id"],childColumns = ["last_post"],onDelete = SET_DEFAULT)
        ]
)
class Channel(
        id : String = "",
        name : String= "",
        @ColumnInfo(name = "image_uri")
        override var imageUri: String = "",
        @ColumnInfo(name = "last_post_id")
        override var lastPostId: String = "",
        @ColumnInfo(name = "last_post_time")
        override var lastPostTime: Long = 0L,
        @ColumnInfo(name = "creator_id")
        override var creatorId: String = FirebaseUtil.currentFireUser?.uid.orEmpty(),
        @ColumnInfo(name = "creation_time")
        override var creationTime: Long = System.currentTimeMillis(),
        @ColumnInfo(name = "admins")
        override var admins: MutableMap<String, ChannelAdmin> = HashMap()) :
        Entity(id), IChannel, Parcelable {

    override var name: String = name.trim()
        set(value) {
            field = value.trim()
            searchName = field.toLowerCase(Locale.getDefault())
        }
    @ColumnInfo(name = "search_name")
    private var searchName: String = name.filter { !it.isWhitespace() }.toLowerCase(Locale.getDefault())

    init {
        if (creatorId.isNotEmpty()) {
            admins[creatorId] = ChannelAdmin(creatorId, canEdit = true, canBan = true, canDelete = true, canPost = true)
        }
    }

    constructor(parcel: Parcel) : this(
            id = parcel.readString().orEmpty(),
            name = parcel.readString().orEmpty(),
            imageUri = parcel.readString().orEmpty(),
            lastPostId = parcel.readString().orEmpty(),
            lastPostTime = parcel.readLong(),
            creatorId = parcel.readString().orEmpty(),
            creationTime = parcel.readLong()) {
        parcel.readMap(admins, ChannelAdmin::class.java.classLoader)
        searchName = parcel.readString().orEmpty()
    }

    constructor(d: Channel) : this(
            id = d.id,
            name = d.name,
            imageUri = d.imageUri,
            lastPostId = d.lastPostId,
            lastPostTime = d.lastPostTime,
            creatorId = d.creatorId,
            creationTime = d.creationTime) {
        admins = HashMap(d.admins)
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(imageUri)
        parcel.writeString(lastPostId)
        parcel.writeLong(lastPostTime)
        parcel.writeString(creatorId)
        parcel.writeLong(creationTime)
        parcel.writeMap(admins)
        parcel.writeString(searchName)
    }

    override fun describeContents(): Int {
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
        if (admins != other.admins) return false
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
        result = 31 * result + admins.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + searchName.hashCode()
        return result
    }

    override fun toString(): String {
        return "Channel(imageUri='$imageUri', lastPostId='$lastPostId', lastPostTime=$lastPostTime, creatorId='$creatorId', creationTime=$creationTime, admins=$admins, name='$name', searchName='$searchName')"
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