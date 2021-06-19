package com.alexz.messenger.app.data.entities.imp

import android.os.Parcel
import android.os.Parcelable
import com.alexz.firerecadapter.Entity
import com.alexz.firerecadapter.IEntity
import com.alexz.messenger.app.data.entities.interfaces.IUser
import com.alexz.messenger.app.util.FirebaseUtil
import com.google.firebase.auth.FirebaseUser


class User(
        id : String = FirebaseUtil.currentFireUser?.uid.orEmpty(),
        override var name: String = FirebaseUtil.currentFireUser?.displayName.orEmpty(),
        override var imageUri: String = FirebaseUtil.currentFireUser?.photoUrl.toString(),
        override var lastOnline: Long = System.currentTimeMillis(),
        override var isOnline: Boolean = true) :
        Entity(id), IUser, Parcelable {

    private constructor(parcel: Parcel) : this(
            id = parcel.readString().orEmpty(),
            name = parcel.readString().orEmpty(),
            imageUri = parcel.readString().orEmpty(),
            isOnline = parcel.readByte().toInt() != 0)

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(name)
        dest.writeString(imageUri)
        dest.writeByte((if (isOnline) 1 else 0).toByte())

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun compareTo(other: IEntity): Int {
        return if (other is User) name.compareTo(other.name) else 0
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

        const val TABLE_NAME = FirebaseUtil.USERS

        override fun createFromParcel(parcel: Parcel): User? {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}

fun User.CREATOR.fromFire(user : FirebaseUser) : User {
    return User(
            id = user.uid,
            name = user.displayName.orEmpty(),
            imageUri = user.photoUrl.toString())
}