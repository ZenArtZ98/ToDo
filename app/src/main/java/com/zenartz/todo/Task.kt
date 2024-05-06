package com.zenartz.todo

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val dueDate: Date,
    var isCompleted: Boolean = false
)