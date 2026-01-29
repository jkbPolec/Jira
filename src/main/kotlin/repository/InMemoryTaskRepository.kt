package repository

import androidx.compose.runtime.mutableStateListOf
import domain.TaskComponent

class InMemoryTaskRepository : TaskRepository {
    private val tasks = mutableStateListOf<TaskComponent>()
    override fun getAllTasks(): List<TaskComponent> = tasks
    override fun addTask(task: TaskComponent) { tasks.add(task) }
}