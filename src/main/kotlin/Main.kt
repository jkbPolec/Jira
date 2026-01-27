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
import domain.Task
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
fun TaskCard(task: Task) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(task.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(task.description, fontSize = 12.sp, color = Color.Gray)
            Text("Assignee: ${task.assignee ?: "Unassigned"}", fontSize = 11.sp)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (task.canUndo()) {
                    IconButton(onClick = { task.undo() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Undo", tint = Color.Red)
                    }
                }
                if (task.state !is DoneState) {
                    IconButton(onClick = { task.moveForward() }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next", tint = Color(0xFF4CAF50))
                    }
                }
            }
        }
    }
}

@Composable
fun TaskColumn(title: String, tasks: List<Task>, modifier: Modifier) {
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
                TaskCard(task)
            }
        }
    }
}

fun main() = application {
    val repository = remember { InMemoryTaskRepository() }
    val board = remember { TaskBoard(repository) }
    val logger = remember { UiLogger() }

    Window(onCloseRequest = ::exitApplication, title = "Mini-Jira Workflow (Patterns Demo)") {
        MaterialTheme {
            Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F0F0))) {

                TopAppBar(title = { Text("Mini-Jira Desktop") }, actions = {
                    Button(onClick = {
                        val newTask = Task(
                            id = System.currentTimeMillis(),
                            title = "Task ${repository.getAllTasks().size + 1}",
                            initialDescription = "Implement design patterns"
                        )
                        newTask.addObserver(logger)
                        repository.addTask(newTask)
                    }) {
                        Icon(Icons.Default.Add, null)
                        Text("Add Task")
                    }
                })

                Row(modifier = Modifier.weight(0.7f).fillMaxWidth()) {
                    val states = listOf("TODO", "IN PROGRESS", "REVIEW", "DONE")
                    states.forEach { stateName ->
                        TaskColumn(
                            title = stateName,
                            tasks = board.getTasksInState(stateName),
                            modifier = Modifier.weight(1f)
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