package iterator

import domain.TaskComponent
import repository.TaskRepository

class TaskBoard(private val repository: TaskRepository) {
    fun getTasksInState(stateName: String): List<TaskComponent> {
        val filtered = mutableListOf<TaskComponent>()
        val iterator = repository.getAllTasks().iterator()
        while (iterator.hasNext()) {
            val t = iterator.next()
            if (t.state.name() == stateName) filtered.add(t)
        }
        return filtered
    }
}