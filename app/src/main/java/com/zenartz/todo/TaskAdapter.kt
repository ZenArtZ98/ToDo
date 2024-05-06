package com.zenartz.todo

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

interface OnTaskClickListener {
    fun onTaskClick(task: Task)
}

class TaskAdapter(private val context: Context, private var _tasks: MutableList<Task>, private val onTaskClickListener: OnTaskClickListener) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    val tasks: List<Task>
        get() = _tasks

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = _tasks[position]
        holder.taskTitle.text = task.name
        holder.taskDescription.text = task.description
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        holder.taskDueDate.text = dateFormat.format(task.dueDate)
        // Bind other task properties to views here
        holder.itemView.setOnClickListener {
            onTaskClickListener.onTaskClick(task)
        }
    }

    override fun getItemCount(): Int {
        return _tasks.size
    }

    fun updateTasks(newTasks: MutableList<Task>) {
        _tasks.clear()
        _tasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.findViewById(R.id.task_name)
        val taskDescription: TextView = itemView.findViewById(R.id.task_description)
        val taskDueDate: TextView = itemView.findViewById(R.id.task_due_date)
        // Declare other views here
    }
}