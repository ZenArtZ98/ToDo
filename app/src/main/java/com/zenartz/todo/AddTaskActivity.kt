package com.zenartz.todo

import android.icu.util.Calendar
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setHomeButtonEnabled(true)
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
                val dueDate = selectedDate ?: throw IllegalStateException("Дата не выбрана")


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
    private fun validateForm(): Boolean {
        val taskName = findViewById<EditText>(R.id.task_name).text.toString()
        val taskDescription = findViewById<EditText>(R.id.task_description).text.toString()

        if (taskName.isEmpty()) {
            Toast.makeText(this, "Введите заголовок", Toast.LENGTH_SHORT).show()
            return false
        }
        if (selectedDate == null) {
            Toast.makeText(this, "Выберите дату", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}