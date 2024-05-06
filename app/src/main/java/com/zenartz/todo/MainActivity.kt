package com.zenartz.todo

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var taskDao: TaskDao
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var addTaskButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskDao = TaskDatabase.getInstance(this).taskDao()

        taskAdapter = TaskAdapter(this, mutableListOf())

        val recyclerView = findViewById<RecyclerView>(R.id.task_list)
        recyclerView.adapter = taskAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        addTaskButton = findViewById<FloatingActionButton>(R.id.add_task_button)
        addTaskButton.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        loadTasks()
    }

    private fun sortTasksByDueDate(tasks: MutableList<Task>) {
        tasks.sortBy { it.dueDate }
    }

    private fun displayTasks(tasks: MutableList<Task>) {
        taskAdapter.updateTasks(tasks)
    }

    private fun loadTasks() {
        lifecycleScope.launch {
            val tasks = withContext(Dispatchers.IO) {
                taskDao.getAllTasks()
            }
            sortTasksByDueDate(tasks.toMutableList())
            displayTasks(tasks.toMutableList())
        }
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }
}
