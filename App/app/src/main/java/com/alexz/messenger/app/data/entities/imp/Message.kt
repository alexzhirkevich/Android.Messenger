package com.alexz.messenger.app.data.entities.imp

import android.os.Parcel
import android.os.Parcelable
import com.alexz.firerecadapter.Entity
import com.alexz.firerecadapter.IEntity
import com.alexz.messenger.app.data.entities.interfaces.IMessage
import com.alexz.messenger.app.util.FirebaseUtil

//@androidx.room.Entity(
//        tableName = Message.TABLE_NAME,
//        foreignKeys = [
//            ForeignKey(entity = User::class, parentColumns = ["id"],childColumns = ["sender_id"],onDelete = ForeignKey.CASCADE),
//            ForeignKey(entity = Chat::class, parentColumns = ["id"],childColumns = ["chat_id"],onDelete = ForeignKey.SET_DEFAULT)
//        ],
//        inheritSuperIndices = true,
//        indices = [Index(value = ["sender_id"]), Index(value = ["chat_id"])]
//
//)
open class Message(
        id : String = "",
      //  @ColumnInfo(name = "chat_id")
        override var chatId: String = "",
       // @ColumnInfo(name = "text")
        override var text: String = "",
      //  @ColumnInfo(name = "sender_id")
        override var senderId: String = User().id,
      //  @ColumnInfo(name = "time")
        override var time: Long = System.currentTimeMillis(),
      //  @ColumnInfo(name = "is_private")
        override var isPrivate: Boolean = false) : Entity(id), IMessage,Parcelable {

    constructor(m: Message) : this(
            id = m.id,
            chatId = m.chatId,
            text = m.text,
            senderId = m.senderId,
            time = m.time,
            isPrivate = m.isPrivate)

    protected constructor(parcel: Parcel) : this(
                id = parcel.readString().orEmpty(),
                chatId = parcel.readString().orEmpty(),
                text = parcel.readString().orEmpty(),
                senderId = parcel.readString().orEmpty(),
                time = parcel.readLong(),
                isPrivate = parcel.readByte().toInt() != 0)

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(chatId)
        dest.writeString(text)
        dest.writeString(senderId)
        dest.writeLong(time)
        dest.writeByte((if (isPrivate) 1 else 0).toByte())
    }

    override fun compareTo(other: IEntity): Int =
        if (other is Message) time.compareTo(other.time) else 0


    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Message) return false
        if (!super.equals(other)) return false
        if (chatId != other.chatId) return false
        if (text != other.text) return false
        if (senderId != other.senderId) return false
        if (time != other.time) return false
        if (isPrivate != other.isPrivate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + chatId.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + senderId.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + isPrivate.hashCode()
        return result
    }

    override fun toString(): String {
        return buildString {
            append("Message(chatId='$chatId', text='$text', senderId='$senderId', ")
            append("time=$time, isPrivate=$isPrivate)")
        }
    }

    companion object CREATOR : Parcelable.Creator<Message> {

        const val TABLE_NAME = FirebaseUtil.MESSAGES

        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }
}