package com.zenartz.todo

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.Locale

class TaskDetailsActivity : AppCompatActivity() {
    private lateinit var task: Task
    private lateinit var taskDao: TaskDao
    private lateinit var taskAdapter: TaskAdapter
    private var currentPosition: Int = -1

    private lateinit var calendarView: CalendarView
    private var selectedDate: Date? = null

    private lateinit var taskTitleTextView: TextView
    private lateinit var taskDescriptionTextView: TextView
    private lateinit var completeTaskButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)

        taskDao = TaskDatabase.getInstance(this).taskDao()
        taskAdapter = TaskAdapter(this)

        // Retrieve the task data from the intent
        task = intent.getParcelableExtra("task") ?: throw IllegalArgumentException("Данные задачи не предоставлены")

        // Initialize the views
        taskTitleTextView = findViewById(R.id.task_name)
        taskDescriptionTextView = findViewById(R.id.task_description)
        calendarView = findViewById(R.id.due_date_calendar)
        completeTaskButton = findViewById(R.id.complete_task_button)

        // Set the initial due date
        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.time = task.dueDate
        calendarView.date = selectedCalendar.timeInMillis
        selectedDate = task.dueDate

        // Set the task details
        taskTitleTextView.text = task.name
        taskDescriptionTextView.text = task.description
        updateDueDateColor()

        // Set the click listener for the complete task button
        completeTaskButton.setOnClickListener {
            completeTask()
        }

        // Set the date change listener for the calendar view
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)
            selectedDate = selectedCalendar.time
            updateDueDateColor()
        }

        currentPosition = taskAdapter.tasks.indexOfFirst { it.id == task.id }

        val saveTaskButton = findViewById<Button>(R.id.save_task_button)
        saveTaskButton.setOnClickListener {
            saveTask()
        }

        val deleteTaskButton = findViewById<Button>(R.id.delete_task_button)
        deleteTaskButton.setOnClickListener {
            deleteTask()
        }
    }

    private fun updateDueDateColor() {
        val currentDate = Date()
        if (task.dueDate.before(currentDate)) {
            calendarView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        } else {
            calendarView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
        }
    }

    private fun completeTask() {
        CoroutineScope(Dispatchers.IO).launch {
            task.isCompleted = true
            task.dueDate = selectedDate ?: task.dueDate
            taskDao.updateTask(task)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@TaskDetailsActivity, "Task completed", Toast.LENGTH_SHORT).show()

                // Найдите индекс задачи в списке задач
                currentPosition = taskAdapter.tasks.indexOfFirst { it.id == task.id }
                if (currentPosition != -1) {
                    taskAdapter.toggleTaskStatus(currentPosition)
                } else {
                    // Если задача не найдена, обновите список задач
                    taskAdapter.updateTasks(taskAdapter.tasks)
                }

                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.back_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_back -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveTask() {
        // Получите данные из полей ввода
        val taskName = taskTitleTextView.text.toString()
        val taskDescription = taskDescriptionTextView.text.toString()

        // Обновите задачу в базе данных
        CoroutineScope(Dispatchers.IO).launch {
            task.name = taskName
            task.description = taskDescription
            task.dueDate = selectedDate ?: task.dueDate
            taskDao.updateTask(task)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@TaskDetailsActivity, "Задача сохранена", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteTask() {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.deleteTask(task)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@TaskDetailsActivity, "Задача удалена", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}