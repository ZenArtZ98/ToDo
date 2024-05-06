package com.zenartz.todo

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class TaskCardActivity : AppCompatActivity() {
    private lateinit var taskDao: TaskDao
    private lateinit var task: Task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_card)

        taskDao = TaskDatabase.getInstance(this).taskDao()
        task = intent.getParcelableExtra("task", Task::class.java)!!

        findViewById<TextView>(R.id.task_name).text = task.name
        findViewById<TextView>(R.id.task_description).text = task.description
        findViewById<TextView>(R.id.due_date).text = task.dueDate.toString()

        findViewById<Button>(R.id.complete_button).setOnClickListener {
            taskDao.updateTask(task.copy(isCompleted = true))
            finish()
        }
    }
    private fun displayTask(task: Task) {
        findViewById<TextView>(R.id.task_name).text = task.name
        findViewById<TextView>(R.id.task_description).text = task.description
        findViewById<TextView>(R.id.due_date).text = task.dueDate.toString()
    }

    override fun onResume() {
        super.onResume()
        displayTask(task)
    }

}