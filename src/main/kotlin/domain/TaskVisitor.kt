package domain

import androidx.compose.runtime.mutableStateListOf
import state.*

interface TaskVisitor {
    fun visitTask(task: Task): Double
    fun visitEpic(epic: Epic): Double
}

class ProgressVisitor : TaskVisitor {
    override fun visitTask(task: Task): Double {
        return if (task.state is DoneState) 100.0 else 0.0
    }

    override fun visitEpic(epic: Epic): Double {
        if (epic.children.isEmpty()) return 100.0
        val sum = epic.children.sumOf { it.accept(this) }
        return sum / epic.children.size
    }
}
