package state

import domain.Task

class DoneState : TaskState {
    override fun start(task: Task) = throw IllegalStateException("Task is done")
    override fun review(task: Task) = throw IllegalStateException("Task is done")
    override fun complete(task: Task) = throw IllegalStateException("Task is done")
    override fun name() = "DONE"
}