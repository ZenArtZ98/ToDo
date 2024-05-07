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
    private lateinit var taskDueDateTextView: TextView
    private lateinit var completeTaskButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setHomeButtonEnabled(true)
        setContentView(R.layout.activity_task_details)

        taskDao = TaskDatabase.getInstance(this).taskDao()

        taskAdapter = TaskAdapter(this)

        taskTitleTextView = findViewById(R.id.task_name)
        taskDescriptionTextView = findViewById(R.id.task_description)
//        taskDueDateTextView = findViewById(R.id.due_date_calendar)
        completeTaskButton = findViewById(R.id.complete_task_button)

        calendarView = findViewById(R.id.due_date_calendar)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)
            selectedDate = selectedCalendar.time
            updateDueDateText()
        }

        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.time = task.dueDate
        calendarView.date = selectedCalendar.timeInMillis
        selectedDate = task.dueDate
        updateDueDateText()



        completeTaskButton.setOnClickListener {
            completeTask()
        }

        task = intent.getParcelableExtra("task") ?: throw IllegalArgumentException("Task must be provided")

        taskTitleTextView.text = task.name
        taskDescriptionTextView.text = task.description
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        taskDueDateTextView.text = dateFormat.format(task.dueDate)
        updateDueDateColor()

        completeTaskButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                task.isCompleted = true
                task.dueDate = selectedDate ?: task.dueDate
                taskDao.updateTask(task)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TaskDetailsActivity, "Task completed", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
        currentPosition = taskAdapter.tasks.indexOfFirst { it.id == task.id }
    }

    private fun updateDueDateText() {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        taskDueDateTextView.text = dateFormat.format(selectedDate ?: task.dueDate)
    }
    private fun completeTask() {
        CoroutineScope(Dispatchers.IO).launch {
            task.isCompleted = true
            taskDao.updateTask(task)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@TaskDetailsActivity, "Task completed", Toast.LENGTH_SHORT).show()
                taskAdapter.toggleTaskStatus(currentPosition)
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

    private fun updateDueDateColor() {
        val currentDate = Date()
        if (task.dueDate.before(currentDate)) {
            taskDueDateTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        } else {
            taskDueDateTextView.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        }
    }
}