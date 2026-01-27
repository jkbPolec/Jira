package observer

interface TaskObserver {
    fun onStateChange(taskId: Long, newState: String)
    fun onUndo(taskId: Long)
    fun onMessage(message: String)
}