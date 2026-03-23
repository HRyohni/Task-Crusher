package hr.fipu.organizationtool.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()
    
    val priorityTasks: Flow<List<Task>> = taskDao.getPriorityTasks()

    val allNonPriorityTasks: Flow<List<Task>> = taskDao.getNonPriorityTasks(Int.MAX_VALUE)

    fun getNonPriorityTasks(limit: Int): Flow<List<Task>> = taskDao.getNonPriorityTasks(limit)

    val suggestedTasks: Flow<List<Task>> = taskDao.getSuggestedTasks()
    
    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task.copy(updatedAt = System.currentTimeMillis()))
    }
    
    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.copy(updatedAt = System.currentTimeMillis()))
    }
    
    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }
    
    suspend fun deleteAllTasks() {
        taskDao.deleteAllTasks()
    }
    
    suspend fun clearAllPriorities() {
        taskDao.clearAllPriorities()
    }
    
    suspend fun toggleTaskCompletion(task: Task) {
        val newStatus = if (task.status == "COMPLETED") "TODO" else "COMPLETED"
        val newCompletedAt = if (newStatus == "COMPLETED") System.currentTimeMillis() else null
        updateTask(task.copy(status = newStatus, completedAt = newCompletedAt))
    }

    fun getTasksCompletedOn(startOfDay: Long, endOfDay: Long): Flow<List<Task>> =
        taskDao.getTasksCompletedOn(startOfDay, endOfDay)
}
