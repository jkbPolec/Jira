package memento

import state.TaskState

data class TaskMemento(
    val state: TaskState,
    val assignee: String?,
    val description: String
)