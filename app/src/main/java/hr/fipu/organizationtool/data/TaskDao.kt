package hr.fipu.organizationtool.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasksOnce(): List<Task>

    @Query("SELECT * FROM tasks WHERE isPriority = 1")
    fun getPriorityTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isPriority = 0 ORDER BY createdAt DESC LIMIT :limit")
    fun getNonPriorityTasks(limit: Int): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE status != 'COMPLETED' AND (isPriority = 1 OR status = 'TODO') ORDER BY createdAt DESC")
    fun getSuggestedTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks WHERE completedAt BETWEEN :startOfDay AND :endOfDay")
    fun getTasksCompletedOn(startOfDay: Long, endOfDay: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE scheduledFor BETWEEN :startOfDay AND :endOfDay ORDER BY createdAt DESC")
    fun getTasksScheduledOn(startOfDay: Long, endOfDay: Long): Flow<List<Task>>

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Query("UPDATE tasks SET isPriority = 0")
    suspend fun clearAllPriorities()
}
