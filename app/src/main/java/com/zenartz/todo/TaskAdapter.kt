package com.zenartz.todo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class TaskAdapter(private val context: Context, private var _tasks: MutableList<Task>) :
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
        holder.taskDueDate.text = task.dueDate.toString()
        // Bind other task properties to views here
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
        val taskDueDate: TextView = itemView.findViewById(R.id.task_due_date)
        // Declare other views here
    }
}