package com.alexz.messenger.app.data.model.imp

import android.os.Parcel
import android.os.Parcelable
import com.alexz.messenger.app.data.model.interfaces.IUser
import com.alexz.messenger.app.util.FirebaseUtil

class User(override var name: String = "",
           override var imageUri: String = "",
           override var lastOnline: Long = 0,
           override var isOnline: Boolean = false)
    : BaseModel(""), IUser, Parcelable, Comparable<User> {

    private constructor(parcel: Parcel) : this(
            name = parcel.readString().orEmpty(),
            imageUri = parcel.readString().orEmpty(),
            isOnline = parcel.readByte().toInt() != 0){
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

    override fun compareTo(user: User): Int {
        return name.compareTo(user.name)
    }

    companion object {
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(`in`: Parcel): User? {
                return User(`in`)
            }

            override fun newArray(size: Int): Array<User?> {
                return arrayOfNulls(size)
            }
        }
    }
}