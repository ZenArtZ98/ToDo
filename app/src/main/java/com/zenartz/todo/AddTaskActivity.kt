package com.zenartz.todo

import android.icu.util.Calendar
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import android.widget.Button
import android.widget.CalendarView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTaskActivity : AppCompatActivity() {
    private lateinit var taskDao: TaskDao
    private var selectedDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        taskDao = TaskDatabase.getInstance(this).taskDao()

        val dueDateCalendar = findViewById<CalendarView>(R.id.due_date_calendar)
        val addTaskButton = findViewById<Button>(R.id.add_task_button)

        dueDateCalendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.time
        }

        addTaskButton.setOnClickListener {
            if (validateForm()) {
                val taskName = findViewById<EditText>(R.id.task_name).text.toString()
                val taskDescription = findViewById<EditText>(R.id.task_description).text.toString()
                val dueDate = selectedDate ?: throw IllegalStateException("No date selected")


                CoroutineScope(Dispatchers.IO).launch {
                    val task = Task(name = taskName, description = taskDescription, dueDate = dueDate)
                    taskDao.addTask(task)

                    withContext(Dispatchers.Main) {
                        finish()
                    }
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        val taskName = findViewById<EditText>(R.id.task_name).text.toString()
        val taskDescription = findViewById<EditText>(R.id.task_description).text.toString()

        if (taskName.isEmpty() || taskDescription.isEmpty() || selectedDate == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}