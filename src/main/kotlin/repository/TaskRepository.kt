package repository

import domain.Task

interface TaskRepository {
    fun getAllTasks(): List<Task>
    fun addTask(task: Task)
}