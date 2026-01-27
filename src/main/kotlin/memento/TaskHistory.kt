package memento

import domain.Task
import java.util.Stack

class TaskHistory {
    private val history = Stack<TaskMemento>()

    fun push(memento: TaskMemento) {
        history.push(memento)
    }

    fun undo(task: Task) {
        if (history.isNotEmpty()) {
            val memento = history.pop()
            task.restore(memento)
        }
    }

    fun canUndo(): Boolean = history.isNotEmpty()
}