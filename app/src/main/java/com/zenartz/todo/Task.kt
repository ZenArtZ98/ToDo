package com.zenartz.todo

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @NonNull var name: String,
    var description: String?,
    @NonNull var dueDate: Date,
    var isCompleted: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        Date(parcel.readLong()),
        parcel.readBoolean()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeLong(dueDate.time)
        parcel.writeBoolean(isCompleted)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }

    fun compareTo(other: Task): Int {
        return this.dueDate.compareTo(other.dueDate)
    }
}