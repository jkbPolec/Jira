package domain

import repository.TaskRepository

interface AssignmentStrategy {
    fun findAssignee(task: Task, repository: TaskRepository): String?
}

class LeastLoadedAssignmentStrategy : AssignmentStrategy {
    private val developers = listOf("Alice", "Bob", "Charlie", "Dave")

    override fun findAssignee(task: Task, repository: TaskRepository): String? {
        val allTasks = repository.getAllTasks().filterIsInstance<Task>()
        val loadMap = developers.associateWith { dev ->
            allTasks.count { it.assignee == dev && it.state.name() != "DONE" }
        }
        
        return loadMap.minByOrNull { it.value }?.key
    }
}

class PriorityBasedAssignmentStrategy : AssignmentStrategy {
    private val seniors = listOf("Alice", "Bob")
    private val juniors = listOf("Charlie", "Dave")

    override fun findAssignee(task: Task, repository: TaskRepository): String? {
        val candidates = if (task.priority == Priority.HIGH || task.priority == Priority.URGENT) {
            seniors
        } else {
            juniors + seniors
        }

        val allTasks = repository.getAllTasks().filterIsInstance<Task>()
        return candidates.minByOrNull { dev ->
            allTasks.count { it.assignee == dev && it.state.name() != "DONE" }
        }
    }
}
