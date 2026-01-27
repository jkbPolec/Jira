package iterator

import domain.Task
import repository.TaskRepository

class TaskBoard(private val repository: TaskRepository) {
    fun getTasksInState(stateName: String): List<Task> {
        val filtered = mutableListOf<Task>()
        val iterator = repository.getAllTasks().iterator()
        while (iterator.hasNext()) {
            val t = iterator.next()
            if (t.state.name() == stateName) filtered.add(t)
        }
        return filtered
    }
}