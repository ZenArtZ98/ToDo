package com.zenartz.todo

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.util.Date


class AddTaskActivity : AppCompatActivity() {
    private lateinit var taskDao: TaskDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        taskDao = TaskDatabase.getInstance(this).taskDao()

        findViewById<Button>(R.id.add_task_button).setOnClickListener {
            val taskName = findViewById<EditText>(R.id.task_name).text.toString()
            val taskDescription = findViewById<EditText>(R.id.task_description).text.toString()
            val dueDate = findViewById<EditText>(R.id.due_date).text.toString()

            if (validateForm()) {
                val task = Task(0, taskName, taskDescription, Date(dueDate))
                taskDao.addTask(task)
                finish()
            }
        }
    }
    private fun validateForm(): Boolean {
        val taskName = findViewById<EditText>(R.id.task_name).text.toString()
        val taskDescription = findViewById<EditText>(R.id.task_description).text.toString()
        val dueDate = findViewById<EditText>(R.id.due_date).text.toString()

        if (taskName.isEmpty() || taskDescription.isEmpty() || dueDate.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

}
