package repository

import domain.TaskComponent

interface TaskRepository {
    fun getAllTasks(): List<TaskComponent>
    fun addTask(task: TaskComponent)
}