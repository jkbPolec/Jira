import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import domain.*
import iterator.TaskBoard
import observer.TaskObserver
import repository.InMemoryTaskRepository
import state.DoneState

class UiLogger : TaskObserver {
    val logs = mutableStateListOf<String>()
    override fun onStateChange(taskId: Long, newState: String) {
        logs.add(0, "[Task #$taskId] Status changed to: $newState")
    }
    override fun onUndo(taskId: Long) {
        logs.add(0, "[Task #$taskId] Action undone")
    }
    override fun onMessage(message: String) {
        logs.add(0, "[System] $message")
    }
}

@Composable
fun TaskCard(component: TaskComponent, invoker: CommandInvoker) {
    val visitor = remember { ProgressVisitor() }
    val progress = component.accept(visitor)

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (component is Task) {
                val priorityColor = when (component.priority) {
                    Priority.LOW -> Color(0xFF81C784)
                    Priority.MEDIUM -> Color(0xFF64B5F6)
                    Priority.HIGH -> Color(0xFFFFB74D)
                    Priority.URGENT -> Color(0xFFE57373)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(priorityColor, RoundedCornerShape(6.dp))
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(component.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Text(component.description, fontSize = 12.sp, color = Color.Gray)
                Text("Priority: ${component.priority}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("Assignee: ${component.assignee ?: "Unassigned"}", fontSize = 11.sp)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    if (component.canUndo()) {
                        IconButton(onClick = { invoker.undoLast() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Undo", tint = Color.Red)
                        }
                    }
                    if (component.state !is DoneState) {
                        IconButton(onClick = {
                            invoker.executeCommand(MoveForwardCommand(component))
                        }) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Next", tint = Color(0xFF4CAF50))
                        }
                    }
                }
            } else if (component is Epic) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "Epic", tint = Color.Magenta)
                    Spacer(Modifier.width(8.dp))
                    Text(component.title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.Magenta)
                }
                Text("Epic Progress: ${progress.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                LinearProgressIndicator(
                    progress = (progress / 100.0).toFloat(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    color = Color.Magenta
                )
                Text("Subtasks: ${component.children.size}", fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun TaskColumn(title: String, tasks: List<TaskComponent>, modifier: Modifier, invoker: CommandInvoker) {
    Column(modifier = modifier.fillMaxHeight().padding(4.dp)) {
        Text(
            title,
            modifier = Modifier.padding(8.dp),
            fontWeight = FontWeight.Black,
            fontSize = 18.sp
        )
        Divider(thickness = 2.dp, color = Color.LightGray)
        LazyColumn {
            items(tasks) { task ->
                TaskCard(task, invoker)
            }
        }
    }
}

fun main() = application {
    val repository = remember { InMemoryTaskRepository() }
    val board = remember { TaskBoard(repository) }
    val logger = remember { UiLogger() }
    val assignmentStrategy = remember { PriorityBasedAssignmentStrategy() }
    val invoker = remember { CommandInvoker() }

    Window(onCloseRequest = ::exitApplication, title = "Advanced Jira Workflow (Complexity Demo)") {
        MaterialTheme {
            Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F0F0))) {

                TopAppBar(title = { Text("Advanced Mini-Jira") }, actions = {
                    Button(onClick = {
                        val epic = Epic(System.currentTimeMillis(), "Epic ${repository.getAllTasks().size + 1}")
                        repository.addTask(epic)
                        logger.onMessage("Created new Epic: ${epic.title}")
                    }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Magenta)) {
                        Text("New Epic", color = Color.White)
                    }

                    Priority.values().forEach { priority ->
                        Button(
                            onClick = {
                                val newTask = Task(
                                    id = System.currentTimeMillis(),
                                    title = "Task ${repository.getAllTasks().size + 1}",
                                    initialDescription = "Complex task with $priority priority",
                                    priority = priority
                                )
                                newTask.addObserver(logger)
                                
                                // Automatic assignment based on strategy
                                val assignee = assignmentStrategy.findAssignee(newTask, repository)
                                newTask.assignee = assignee
                                
                                // Add to last Epic if exists
                                val lastEpic = repository.getAllTasks().filterIsInstance<Epic>().lastOrNull()
                                if (lastEpic != null) {
                                    lastEpic.addSubtask(newTask)
                                    logger.onMessage("Added task to Epic: ${lastEpic.title}")
                                }
                                
                                repository.addTask(newTask)
                                logger.onMessage("Auto-assigned $assignee to new ${priority} task")
                            },
                            modifier = Modifier.padding(horizontal = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = when (priority) {
                                    Priority.LOW -> Color(0xFF81C784)
                                    Priority.MEDIUM -> Color(0xFF64B5F6)
                                    Priority.HIGH -> Color(0xFFFFB74D)
                                    Priority.URGENT -> Color(0xFFE57373)
                                }
                            )
                        ) {
                            Text("+ $priority", fontSize = 10.sp)
                        }
                    }
                })

                Row(modifier = Modifier.weight(0.7f).fillMaxWidth()) {
                    val states = listOf("TODO", "IN PROGRESS", "REVIEW", "DONE")
                    states.forEach { stateName ->
                        TaskColumn(
                            title = stateName,
                            tasks = board.getTasksInState(stateName),
                            modifier = Modifier.weight(1f),
                            invoker = invoker
                        )
                    }
                }

                Card(modifier = Modifier.weight(0.3f).fillMaxWidth().padding(8.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Event Logs (Observer)", fontWeight = FontWeight.Bold)
                        Divider()
                        LazyColumn {
                            items(logger.logs) { log ->
                                Text(log, fontSize = 12.sp, modifier = Modifier.padding(vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}