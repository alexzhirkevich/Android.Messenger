package com.alexz.messenger.app.data.entities.imp

import android.os.Parcel
import android.os.Parcelable
import com.alexz.firerecadapter.IEntity
import com.alexz.messenger.app.data.entities.EntityCollection
import com.alexz.messenger.app.data.entities.interfaces.IChat
import com.alexz.messenger.app.util.FirebaseUtil

//@androidx.room.Entity(
//        tableName = Chat.TABLE_NAME,
//        foreignKeys = [
//            ForeignKey(entity = User::class, parentColumns = ["id"],childColumns = ["creator_id"],onDelete = ForeignKey.CASCADE),
//            ForeignKey(entity = Message::class, parentColumns = ["id"],childColumns = ["last_message_id"],onDelete = ForeignKey.SET_DEFAULT)
//        ],
//        inheritSuperIndices = true,
//        indices = [Index(value = ["creator_id"]),Index(value = ["last_message_id"])]
//)
class Chat(
        id : String = "",
  //      @ColumnInfo(name = "name")
        override var name: String = "",
   //     @ColumnInfo(name = "image_uri")
        override var imageUri: String = "",
   //     @ColumnInfo(name = "last_message_id")
        override val lastMessageId : String = "",
    //    @ColumnInfo(name = "last_message_time")
        override val lastMessageTime: Long = Long.MAX_VALUE,
    //    @ColumnInfo(name = "creator_id")
        override var creatorId: String = "",
    //    @ColumnInfo(name = "creation_time")
        override var creationTime: Long = 0,
    //    @ColumnInfo(name = "is_group")
        override var isGroup: Boolean = false

) : EntityCollection(id, setOf(Message::class.java)), IChat, Parcelable {

    constructor(di: Chat) :this(
            id = di.id,
            name = di.name,
            imageUri = di.imageUri,
            lastMessageId = di.lastMessageId,
            lastMessageTime = di.lastMessageTime,
            creatorId = di.creatorId,
            creationTime = di.creationTime,
            isGroup =  di.isGroup)

    private constructor(parcel: Parcel) : this(
            id = parcel.readString().orEmpty(),
            imageUri = parcel.readString().orEmpty(),
            name = parcel.readString().orEmpty(),
            lastMessageId = parcel.readString().orEmpty(),
            lastMessageTime = parcel.readLong(),
            creatorId = parcel.readString().orEmpty(),
            creationTime = parcel.readLong(),
            isGroup = parcel.readByte().toInt() != 0)

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(imageUri)
        dest.writeString(name)
        dest.writeString(lastMessageId)
        dest.writeLong(lastMessageTime)
        dest.writeString(creatorId)
        dest.writeLong(creationTime)
        dest.writeByte((if (isGroup) 1 else 0).toByte())
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun compareTo(other: IEntity): Int {
        if (other is Chat) {
            if (lastMessageTime != 0L && other.lastMessageTime != 0L)
                return lastMessageTime.compareTo(other.lastMessageTime)
            return other.creationTime.compareTo(other.creationTime)
        }
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Chat> {

        const val TABLE_NAME = FirebaseUtil.CHATS

        override fun createFromParcel(parcel: Parcel): Chat? {
            return Chat(parcel)
        }

        override fun newArray(size: Int): Array<Chat?> {
            return arrayOfNulls(size)
        }
    }
}