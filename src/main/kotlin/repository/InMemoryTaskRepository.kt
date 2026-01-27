package repository

import androidx.compose.runtime.mutableStateListOf
import domain.Task

class InMemoryTaskRepository : TaskRepository {
    private val tasks = mutableStateListOf<Task>()
    override fun getAllTasks(): List<Task> = tasks
    override fun addTask(task: Task) { tasks.add(task) }
}