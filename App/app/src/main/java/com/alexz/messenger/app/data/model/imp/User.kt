package com.alexz.messenger.app.data.model.imp

import android.os.Parcel
import android.os.Parcelable
import com.alexz.firerecadapter.BaseModel
import com.alexz.messenger.app.data.model.interfaces.IUser

class User(override var name: String = "",
           override var imageUri: String = "",
           override var lastOnline: Long = 0,
           override var isOnline: Boolean = false) :
        BaseModel(""), IUser, Parcelable, Comparable<User> {

    private constructor(parcel: Parcel) : this(
            name = parcel.readString().orEmpty(),
            imageUri = parcel.readString().orEmpty(),
            isOnline = parcel.readByte().toInt() != 0) {
        id = parcel.readString().orEmpty()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(name)
        dest.writeString(imageUri)
        dest.writeByte((if (isOnline) 1 else 0).toByte())
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun compareTo(other: User): Int {
        return name.compareTo(other.name)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        if (!super.equals(other)) return false

        if (name != other.name) return false
        if (imageUri != other.imageUri) return false
        if (lastOnline != other.lastOnline) return false
        if (isOnline != other.isOnline) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + imageUri.hashCode()
        result = 31 * result + lastOnline.hashCode()
        result = 31 * result + isOnline.hashCode()
        return result
    }

    override fun toString(): String {
        return "User(name='$name', imageUri='$imageUri', lastOnline=$lastOnline, isOnline=$isOnline)"
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User? {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}