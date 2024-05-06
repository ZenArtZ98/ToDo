package com.zenartz.todo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [Task::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        private var instance: TaskDatabase? = null

        fun getInstance(context: Context): TaskDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context, TaskDatabase::class.java, "task_database")
                    .build()
            }
            return instance!!
        }
    }
}