package xyz.keyurved.processwatcher.entities

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Message(val processName: String, val message: String, val messageType: Message.MessageType,
              val arrivalTime: LocalDateTime): Parcelable {

    constructor(parcel: Parcel) : this (
            parcel.readString(),
            parcel.readString(),
            parcel.readSerializable() as MessageType,
            LocalDateTime.parse(parcel.readString(), DateTimeFormatter.ofPattern("hh:mm:ss MM-dd-yyyy"))
    )


    enum class MessageType {
        INFO, ERROR, EXIT
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(processName)
        parcel.writeString(message)
        parcel.writeSerializable(messageType)
        parcel.writeString(arrivalTime.format(DateTimeFormatter.ofPattern("hh:mm:ss MM-dd-yyyy")))
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        fun fromJSON(json: JSONObject): Message {
            val processName: String = json["processName"] as String
            val message: String = json["message"] as String
            val type = when {
                json["type"] == "info" -> MessageType.INFO
                json["type"] == "error" -> MessageType.ERROR
                json["type"] == "exit" -> MessageType.EXIT
                else -> throw Error("Invalid message type")
            }

            return Message(processName, message, type, LocalDateTime.now())
        }

        @JvmField val CREATOR = object: Parcelable.Creator<Message> {
            override fun createFromParcel(parcel: Parcel): Message {
                return Message(parcel)
            }

            override fun newArray(size: Int): Array<Message?> {
                return arrayOfNulls(size)
            }
        }
    }
}