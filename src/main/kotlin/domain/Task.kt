package domain

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import memento.*
import observer.*
import state.*

class Task(
    val id: Long,
    val title: String,
    initialDescription: String,
    initialAssignee: String? = null
) {
    var state by mutableStateOf<TaskState>(TodoState())
    var description by mutableStateOf(initialDescription)
    var assignee by mutableStateOf(initialAssignee)

    private val history = TaskHistory()
    private val observers = mutableListOf<TaskObserver>()

    fun addObserver(observer: TaskObserver) { observers.add(observer) }

    private fun saveToHistory() {
        history.push(TaskMemento(state, assignee, description))
    }

    fun changeState(newState: TaskState) {
        saveToHistory()
        state = newState
        observers.forEach { it.onStateChange(id, newState.name()) }
    }

    fun undo() {
        if (history.canUndo()) {
            history.undo(this)
            observers.forEach { it.onUndo(id) }
        }
    }

    fun restore(memento: TaskMemento) {
        this.state = memento.state
        this.assignee = memento.assignee
        this.description = memento.description
    }

    fun moveForward() {
        try {
            when (state) {
                is TodoState -> state.start(this)
                is InProgressState -> state.review(this)
                is ReviewState -> state.complete(this)
                is DoneState -> {}
            }
        } catch (e: Exception) {
            observers.forEach { it.onMessage("Error: ${e.message}") }
        }
    }

    fun canUndo() = history.canUndo()
}