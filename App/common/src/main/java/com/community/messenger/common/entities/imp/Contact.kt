package com.community.messenger.common.entities.imp

import android.os.Parcel
import android.os.Parcelable
import com.community.messenger.common.entities.interfaces.IContact

open class Contact(
    id: String = "",
    override var name: String="",
    override var phone: String=""
    ) : Entity(id), IContact, Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString().orEmpty(),
        name  = parcel.readString().orEmpty(),
        phone= parcel.readString().orEmpty(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(phone)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Contact) return false
        if (!super.equals(other)) return false

        if (name != other.name) return false
        if (phone != other.phone) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + phone.hashCode()
        return result
    }

    override fun toString(): String {
        return "Contact(name='$name', phone='$phone')"
    }

    companion object CREATOR : Parcelable.Creator<Contact> {
        override fun createFromParcel(parcel: Parcel): Contact {
            return Contact(parcel)
        }

        override fun newArray(size: Int): Array<Contact?> {
            return arrayOfNulls(size)
        }
    }
}