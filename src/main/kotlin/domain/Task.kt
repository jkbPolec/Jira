package domain

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import memento.*
import observer.*
import state.*

enum class Priority { LOW, MEDIUM, HIGH, URGENT }

interface TaskComponent {
    val id: Long
    val title: String
    val state: TaskState
    fun accept(visitor: TaskVisitor): Double
}

class Epic(
    override val id: Long,
    override val title: String
) : TaskComponent {
    val children = mutableStateListOf<TaskComponent>()
    override val state: TaskState
        get() = if (children.all { it.state is DoneState } && children.isNotEmpty()) DoneState() else InProgressState()

    override fun accept(visitor: TaskVisitor): Double = visitor.visitEpic(this)

    fun addSubtask(component: TaskComponent) {
        children.add(component)
    }
}

class Task(
    override val id: Long,
    override val title: String,
    initialDescription: String,
    val priority: Priority = Priority.MEDIUM,
    initialAssignee: String? = null
) : TaskComponent {
    override var state by mutableStateOf<TaskState>(TodoState())
    var description by mutableStateOf(initialDescription)
    var assignee by mutableStateOf(initialAssignee)

    private val history = TaskHistory()
    private val observers = mutableListOf<TaskObserver>()

    fun addObserver(observer: TaskObserver) { observers.add(observer) }

    override fun accept(visitor: TaskVisitor): Double = visitor.visitTask(this)

    private fun saveToHistory() {
        history.push(TaskMemento(state, assignee, description, priority))
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
        // priority is val, we don't restore it as it shouldn't change
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