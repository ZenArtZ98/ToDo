package com.zenartz.todo

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Date
import java.util.Locale

interface OnTaskClickListener {
    fun onTaskClick(task: Task)
}

class TaskAdapter(
    private val context: Context,
    private var _tasks: MutableList<Task> = mutableListOf(),
    private val onTaskClickListener: OnTaskClickListener? = null
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val completedTasks = mutableListOf<Task>()
    private val incompleteTasks = mutableListOf<Task>()

    val tasks: List<Task>
        get() = _tasks

    companion object {
        private const val MAX_TITLE_LENGTH = 15
        private const val MAX_DESCRIPTION_LENGTH = 70
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = _tasks[position]
        holder.bind(task)
        holder.taskTitle.text = task.name.take(MAX_TITLE_LENGTH).let {
            if (it.length == MAX_TITLE_LENGTH) "$it..." else it
        }
        holder.taskDescription.text = task.description?.take(MAX_DESCRIPTION_LENGTH)?.let {
            if (it.length == MAX_DESCRIPTION_LENGTH) "$it..." else it
        }
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        holder.taskDueDate.text = dateFormat.format(task.dueDate)
        holder.itemView.setOnClickListener {
            onTaskClickListener?.onTaskClick(task)
        }
    }

    override fun getItemCount(): Int {
        return _tasks.size
    }

    fun updateTasks(newTasks: List<Task>) {
        completedTasks.clear()
        incompleteTasks.clear()

        for (task in newTasks) {
            if (task.isCompleted) {
                completedTasks.add(task)
            } else {
                incompleteTasks.add(task)
            }
        }

        _tasks = (incompleteTasks + completedTasks).toMutableList()
        notifyDataSetChanged()
    }

    fun toggleTaskStatus(position: Int) {
        val task = _tasks[position]
        task.isCompleted = !task.isCompleted

        if (task.isCompleted) {
            completedTasks.add(task)
            incompleteTasks.remove(task)
        } else {
            incompleteTasks.add(task)
            completedTasks.remove(task)
        }

        _tasks = (incompleteTasks + completedTasks).toMutableList()
        notifyItemChanged(position)
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.findViewById(R.id.task_name)
        val taskDescription: TextView = itemView.findViewById(R.id.task_description)
        val taskDueDate: TextView = itemView.findViewById(R.id.task_due_date)

        fun bind(task: Task) {
            taskTitle.text = task.name?.take(MAX_TITLE_LENGTH)?.let {
                if (it.length == MAX_TITLE_LENGTH) "$it..." else it
            }
            taskDescription.text = task.description?.take(MAX_DESCRIPTION_LENGTH)?.let {
                if (it.length == MAX_DESCRIPTION_LENGTH) "$it..." else it
            }

            updateTaskStatus(task)
            updateTaskDueDate(task)
        }

        private fun updateTaskStatus(task: Task) {
            if (task.isCompleted) {
                taskTitle.paintFlags = taskTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                taskTitle.setTextColor(Color.GRAY)
                taskDescription.paintFlags = taskDescription.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                taskDescription.setTextColor(Color.GRAY)
                taskDueDate.paintFlags = taskDueDate.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                taskDueDate.setTextColor(Color.GRAY)
            } else {
                taskTitle.paintFlags = taskTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                taskTitle.setTextColor(Color.BLACK)
                taskDescription.paintFlags = taskDescription.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                taskDueDate.paintFlags = taskDueDate.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }

        private fun updateTaskDueDate(task: Task) {
            val currentDate = Date()
            if (task.dueDate.before(currentDate)) {
                taskDueDate.setTextColor(Color.RED)
            } else {
                taskDueDate.setTextColor(itemView.context.getColor(R.color.black))
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            taskDueDate.text = dateFormat.format(task.dueDate)
        }
    }
}