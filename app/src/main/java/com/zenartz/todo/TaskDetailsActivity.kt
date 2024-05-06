package com.zenartz.todo

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
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

        taskTitleTextView = findViewById(R.id.task_title)
        taskDescriptionTextView = findViewById(R.id.task_description)
        taskDueDateTextView = findViewById(R.id.task_due_date)
        completeTaskButton = findViewById(R.id.complete_task_button)

        task = intent.getParcelableExtra("task") ?: throw IllegalArgumentException("Task must be provided")

        taskTitleTextView.text = task.name
        taskDescriptionTextView.text = task.description
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        taskDueDateTextView.text = dateFormat.format(task.dueDate)
        updateDueDateColor()

        completeTaskButton.setOnClickListener {
            // Handle task completion
            CoroutineScope(Dispatchers.IO).launch {
                task.isCompleted = true
                taskDao.updateTask(task)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TaskDetailsActivity, "Task completed", Toast.LENGTH_SHORT).show()
                    finish()
                }
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