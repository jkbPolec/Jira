package domain

interface Command {
    fun execute()
    fun undo()
}

class MoveForwardCommand(private val task: Task) : Command {
    private var previousMemento: memento.TaskMemento? = null

    override fun execute() {
        task.moveForward()
    }

    override fun undo() {
        task.undo()
    }
}

class AssignCommand(private val task: Task, private val newAssignee: String?) : Command {
    private var oldAssignee: String? = task.assignee

    override fun execute() {
        task.assignee = newAssignee
    }

    override fun undo() {
        task.assignee = oldAssignee
    }
}

class CommandInvoker {
    private val history = mutableListOf<Command>()

    fun executeCommand(command: Command) {
        command.execute()
        history.add(command)
    }

    fun undoLast() {
        if (history.isNotEmpty()) {
            val command = history.removeAt(history.size - 1)
            command.undo()
        }
    }
}
