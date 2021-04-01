package com.alexz.messenger.app.data.model.imp

import android.os.Parcel
import android.os.Parcelable
import com.alexz.messenger.app.data.model.interfaces.IVoiceMessage

class VoiceMessage : Message, IVoiceMessage, Parcelable {

    override var voiceUri: String = ""
    override var voiceLen: Int = 0

    protected constructor() :super("")

    constructor(chatId : String) :super(chatId)

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
        override fun createFromParcel(parcel: Parcel): VoiceMessage {
            return VoiceMessage(parcel)
        }

        override fun newArray(size: Int): Array<VoiceMessage?> {
            return arrayOfNulls(size)
        }
    }
}