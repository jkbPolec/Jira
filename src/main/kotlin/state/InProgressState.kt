package state

import domain.Task

class InProgressState : TaskState {
    override fun start(task: Task) = throw IllegalStateException("Already in progress")
    override fun review(task: Task) {
        if (!canMoveForward(task)) throw IllegalStateException("Task description too short for high priority task!")
        task.changeState(ReviewState())
    }
    override fun complete(task: Task) = throw IllegalStateException("Must go through review")
    override fun name() = "IN PROGRESS"

    override fun canMoveForward(task: Task): Boolean {
        // High priority tasks require more detailed description before review
        if (task.priority == domain.Priority.HIGH || task.priority == domain.Priority.URGENT) {
            return task.description.length > 20
        }
        return true
    }
}