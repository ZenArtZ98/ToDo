package com.zenartz.todo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
        @Query("SELECT * FROM tasks")
        fun getAllTasks(): List<Task>
        @Insert
        fun addTask(task: Task)
        @Update
        fun updateTask(task: Task)
        @Delete
        fun deleteTask(task: Task)
}