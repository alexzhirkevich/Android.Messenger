package com.alexz.messenger.app.data.entities.imp

import android.os.Parcel
import android.os.Parcelable
import com.alexz.firerecadapter.Entity
import com.alexz.firerecadapter.IEntity
import com.alexz.messenger.app.data.entities.interfaces.IUser
import com.alexz.messenger.app.util.FirebaseUtil


class User(
        id : String = "",
        override var name: String = "",
        override var imageUri: String = "",
        override var phone: String = "",
        override var username: String = "",
        override var description: String="",
        override var creationTime: Long =  0,
        override var lastOnline: Long = 0,
        override var isOnline: Boolean = false) :
        Entity(id), IUser {

    private constructor(parcel: Parcel) : this(
            id = parcel.readString().orEmpty(),
            name = parcel.readString().orEmpty(),
            phone = parcel.readString().orEmpty(),
            username = parcel.readString().orEmpty(),
            imageUri = parcel.readString().orEmpty(),
            description = parcel.readString().orEmpty(),
            creationTime = parcel.readLong(),
            isOnline = parcel.readByte().toInt() != 0)

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(name)
        dest.writeString(imageUri)
        dest.writeString(phone)
        dest.writeString(username)
        dest.writeString(description)
        dest.writeLong(creationTime)
        dest.writeByte((if (isOnline) 1 else 0).toByte())

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun compareTo(other: IEntity): Int {
        return if (other is User) name.compareTo(other.name) else 0
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + imageUri.hashCode()
        result = 31 * result + phone.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + creationTime.hashCode()
        result = 31 * result + lastOnline.hashCode()
        result = 31 * result + isOnline.hashCode()
        return result
    }

    override fun toString(): String {
        return "User(name='$name', imageUri='$imageUri', phone='$phone', username='$username'," +
                " description='$description', creationTime=$creationTime, lastOnline=$lastOnline, isOnline=$isOnline)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        if (!super.equals(other)) return false

        if (name != other.name) return false
        if (imageUri != other.imageUri) return false
        if (phone != other.phone) return false
        if (username != other.username) return false
        if (description != other.description) return false
        if (creationTime != other.creationTime) return false
        if (lastOnline != other.lastOnline) return false
        if (isOnline != other.isOnline) return false

        return true
    }


    companion object CREATOR : Parcelable.Creator<User> {

        const val TABLE_NAME = FirebaseUtil.USERS

        override fun createFromParcel(parcel: Parcel): User? {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}