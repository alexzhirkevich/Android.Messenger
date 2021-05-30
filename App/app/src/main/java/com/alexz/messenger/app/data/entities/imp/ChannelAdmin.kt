package com.alexz.messenger.app.data.entities.imp

import android.os.Parcel
import android.os.Parcelable
import com.alexz.firerecadapter.Entity
import com.alexz.firerecadapter.IEntity
import com.alexz.messenger.app.data.entities.interfaces.IChannelAdmin

class ChannelAdmin(
        id: String = User().id,
        override var canEdit: Boolean = false,
        override var canPost: Boolean = false,
        override var canDelete: Boolean = false,
        override var canBan: Boolean = false) : Entity(id), IChannelAdmin,Parcelable {

    constructor(parcel: Parcel) : this(
            id = parcel.readString().orEmpty(),
            canEdit= parcel.readByte() != 0.toByte(),
            canPost = parcel.readByte() != 0.toByte(),
            canDelete = parcel.readByte() != 0.toByte(),
            canBan = parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeByte(if (canEdit) 1 else 0)
        parcel.writeByte(if (canPost) 1 else 0)
        parcel.writeByte(if (canDelete) 1 else 0)
        parcel.writeByte(if (canBan) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun compareTo(other: IEntity): Int {
        if (other is ChannelAdmin){
            canBan.compareTo(other.canBan).takeIf { it != 0 }?.let { return it }
            canEdit.compareTo(other.canEdit).takeIf { it != 0 }?.let { return it }
            canDelete.compareTo(other.canDelete).takeIf { it != 0 }?.let { return it }
            return canPost.compareTo(other.canPost)
        }
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChannelAdmin) return false
        if (!super.equals(other)) return false

        if (id != other.id) return false
        if (canEdit != other.canEdit) return false
        if (canPost != other.canPost) return false
        if (canDelete != other.canDelete) return false
        if (canBan != other.canBan) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + canEdit.hashCode()
        result = 31 * result + canPost.hashCode()
        result = 31 * result + canDelete.hashCode()
        result = 31 * result + canBan.hashCode()
        return result
    }

    override fun toString(): String {
        return "ChannelAdmin(id='$id', canEdit=$canEdit, canPost=$canPost, canDelete=$canDelete, canBan=$canBan)"
    }


    companion object CREATOR : Parcelable.Creator<ChannelAdmin> {
        override fun createFromParcel(parcel: Parcel): ChannelAdmin {
            return ChannelAdmin(parcel)
        }

        override fun newArray(size: Int): Array<ChannelAdmin?> {
            return arrayOfNulls(size)
        }
    }
}