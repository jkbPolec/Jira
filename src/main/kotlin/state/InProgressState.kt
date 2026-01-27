package state

import domain.Task

class InProgressState : TaskState {
    override fun start(task: Task) = throw IllegalStateException("Already in progress")
    override fun review(task: Task) { task.changeState(ReviewState()) }
    override fun complete(task: Task) = throw IllegalStateException("Must go through review")
    override fun name() = "IN PROGRESS"
}