package com.alexz.messenger.app.data.entities.imp

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.alexz.messenger.app.data.entities.interfaces.IVoiceMessage
import com.alexz.messenger.app.util.FirebaseUtil

@Entity(tableName = VoiceMessage.TABLE_NAME)
class VoiceMessage : Message, IVoiceMessage, Parcelable {

    @ColumnInfo(name = "voice_uri")
    override var voiceUri: String = ""

    @ColumnInfo(name = "voice_len")
    override var voiceLen: Int = 0

    protected constructor() :super("")

    constructor(chatId : String,voiceUri : String = "", voiceLen : Int = 0) :super(chatId){
        this.voiceUri = voiceUri;
        this.voiceLen = voiceLen;
    }

    private constructor(parcel: Parcel) : super(parcel){
        voiceUri = parcel.readString().orEmpty()
        voiceLen = parcel.readInt()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeString(voiceUri)
        dest.writeInt(voiceLen)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VoiceMessage) return false
        if (!super.equals(other)) return false

        if (voiceUri != other.voiceUri) return false
        if (voiceLen != other.voiceLen) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + voiceUri.hashCode()
        result = 31 * result + voiceLen
        return result
    }

    override fun toString(): String {
        return "VoiceMessage(voiceUri='$voiceUri', voiceLen=$voiceLen)"
    }

    companion object CREATOR : Parcelable.Creator<VoiceMessage> {

        const val TABLE_NAME = FirebaseUtil.VOICE_MESSAGES

        override fun createFromParcel(parcel: Parcel): VoiceMessage {
            return VoiceMessage(parcel)
        }

        override fun newArray(size: Int): Array<VoiceMessage?> {
            return arrayOfNulls(size)
        }
    }
}