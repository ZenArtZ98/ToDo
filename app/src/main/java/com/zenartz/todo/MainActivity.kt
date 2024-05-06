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

class MainActivity : AppCompatActivity(), OnTaskClickListener {
    private lateinit var taskDao: TaskDao
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var addTaskButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskDao = TaskDatabase.getInstance(this).taskDao()

        taskAdapter = TaskAdapter(this, mutableListOf(), this)


        val recyclerView = findViewById<RecyclerView>(R.id.task_list)
        recyclerView.adapter = taskAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        addTaskButton = findViewById<FloatingActionButton>(R.id.add_task_button)
        addTaskButton.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
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
        CoroutineScope(Dispatchers.IO).launch {
            val tasks = taskDao.getAllTasks()
            withContext(Dispatchers.Main) {
                taskAdapter.updateTasks(tasks.toMutableList())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    override fun onTaskClick(task: Task) {
        // Handle task click, e.g., navigate to task details screen
        val intent = Intent(this, TaskDetailsActivity::class.java)
        intent.putExtra("task", task)
        startActivity(intent)
    }
}
