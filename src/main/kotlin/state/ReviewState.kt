package state

import domain.Task

class ReviewState : TaskState {
    override fun start(task: Task) = throw IllegalStateException("Already started")
    override fun review(task: Task) = throw IllegalStateException("Already in review")
    override fun complete(task: Task) { task.changeState(DoneState()) }
    override fun name() = "REVIEW"
}