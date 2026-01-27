package state

import domain.Task

class TodoState : TaskState {
    override fun start(task: Task) { task.changeState(InProgressState()) }
    override fun review(task: Task) = throw IllegalStateException("Cannot review from TODO")
    override fun complete(task: Task) = throw IllegalStateException("Cannot complete from TODO")
    override fun name() = "TODO"
}