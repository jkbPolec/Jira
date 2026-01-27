package state

import domain.Task

sealed interface TaskState {
    fun start(task: Task)
    fun review(task: Task)
    fun complete(task: Task)
    fun name(): String
}