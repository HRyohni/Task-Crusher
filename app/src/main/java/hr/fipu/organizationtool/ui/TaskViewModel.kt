package hr.fipu.organizationtool.ui

import android.app.Application
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.fipu.organizationtool.data.OnboardingRepository
import hr.fipu.organizationtool.data.Task
import hr.fipu.organizationtool.data.TaskRepository
import hr.fipu.organizationtool.widget.ZenStackWidget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository,
    private val onboardingRepository: OnboardingRepository,
    private val application: Application
) : ViewModel() {

    val savedTasks: StateFlow<List<Task>?> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val suggestedTasks: StateFlow<List<Task>> = repository.suggestedTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hasSeenBackTapGuide: StateFlow<Boolean> = onboardingRepository.hasSeenBackTapGuide
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    private val _brainDumpTasks = MutableStateFlow<List<Task>>(emptyList())
    val brainDumpTasks: StateFlow<List<Task>> = _brainDumpTasks.asStateFlow()

    private val _selectedPriorityIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedPriorityIds: StateFlow<Set<Int>> = _selectedPriorityIds.asStateFlow()

    private var nextTempId = -1

    fun addTask(title: String) {
        if (title.isBlank()) return
        val newTask = Task(id = nextTempId--, title = title)
        _brainDumpTasks.value = listOf(newTask) + _brainDumpTasks.value
    }

    fun addSuggestedTask(task: Task) {
        if (_brainDumpTasks.value.none { it.id == task.id }) {
            _brainDumpTasks.value = listOf(task) + _brainDumpTasks.value
        }
    }

    fun removeTask(task: Task) {
        _brainDumpTasks.value = _brainDumpTasks.value - task
        _selectedPriorityIds.value = _selectedPriorityIds.value - task.id
    }

    fun togglePriority(task: Task) {
        val currentSelected = _selectedPriorityIds.value
        if (currentSelected.contains(task.id)) {
            _selectedPriorityIds.value = currentSelected - task.id
        } else if (currentSelected.size < 3) {
            _selectedPriorityIds.value = currentSelected + task.id
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
            ZenStackWidget().updateAll(application)
        }
    }

    fun markBackTapGuideAsSeen() {
        viewModelScope.launch {
            onboardingRepository.setHasSeenBackTapGuide(true)
        }
    }

    fun saveSession() {
        viewModelScope.launch {
            repository.clearAllPriorities()
            val finalTasks = _brainDumpTasks.value.map { task ->
                val isPriority = _selectedPriorityIds.value.contains(task.id)
                if (task.id < 0) {
                    task.copy(id = 0, isPriority = isPriority)
                } else {
                    task.copy(isPriority = isPriority)
                }
            }
            finalTasks.forEach { repository.insertTask(it) }
            ZenStackWidget().updateAll(application)
            _brainDumpTasks.value = emptyList()
            _selectedPriorityIds.value = emptySet()
        }
    }
}
