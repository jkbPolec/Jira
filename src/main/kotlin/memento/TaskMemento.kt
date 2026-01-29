package memento

import domain.Priority
import state.TaskState

data class TaskMemento(
    val state: TaskState,
    val assignee: String?,
    val description: String,
    val priority: Priority
)