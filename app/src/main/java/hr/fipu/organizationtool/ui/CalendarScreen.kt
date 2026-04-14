package hr.fipu.organizationtool.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.fipu.organizationtool.data.Task
import hr.fipu.organizationtool.ui.theme.ZenGrayLight
import hr.fipu.organizationtool.ui.theme.ZenGrayMedium
import hr.fipu.organizationtool.ui.theme.ZenIndigo
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private fun heatmapColor(count: Int): Color = when {
    count == 0 -> ZenGrayLight
    count == 1 -> ZenIndigo.copy(alpha = 0.30f)
    count in 2..3 -> ZenIndigo.copy(alpha = 0.60f)
    else -> ZenIndigo
}

private fun heatmapTextColor(count: Int): Color = when {
    count == 0 -> Color.Black.copy(alpha = 0.7f)
    count == 1 -> Color.Black.copy(alpha = 0.8f)
    else -> Color.White
}

@Composable
fun CalendarScreen(
    completionHistory: Map<LocalDate, Int>,
    selectedDay: LocalDate?,
    tasksForSelectedDay: List<Task>,
    scheduledTasksForSelectedDay: List<Task>,
    onDaySelected: (LocalDate) -> Unit,
    onAddScheduledTask: (String, LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    var displayedMonth by remember { mutableStateOf(YearMonth.now()) }
    val earliestMonth = YearMonth.now().minusMonths(11)

    val daysInMonth = displayedMonth.lengthOfMonth()
    val firstDayOfMonth = displayedMonth.atDay(1)
    val paddingCells = (firstDayOfMonth.dayOfWeek.value - 1) % 7

    var newTaskText by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {

        // Month navigation header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { displayedMonth = displayedMonth.minusMonths(1) },
                enabled = displayedMonth > earliestMonth
            ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous month")
            }
            Text(
                text = displayedMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) +
                        " ${displayedMonth.year}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            IconButton(
                onClick = { displayedMonth = displayedMonth.plusMonths(1) },
                enabled = true
            ) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next month")
            }
        }

        // Day-of-week labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su").forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                    color = ZenGrayMedium,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // Build cell list
        val cells: List<LocalDate?> = List(paddingCells) { null } +
                (1..daysInMonth).map { displayedMonth.atDay(it) }

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
            cells.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { date ->
                        if (date == null) {
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                        } else {
                            val count = completionHistory[date] ?: 0
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(heatmapColor(count))
                                    .then(
                                        if (date == selectedDay) Modifier.border(2.dp, ZenIndigo, RoundedCornerShape(4.dp))
                                        else if (date == today) Modifier.border(1.5.dp, ZenIndigo.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                        else Modifier
                                    )
                                    .clickable { onDaySelected(date) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    fontSize = 11.sp,
                                    fontWeight = if (date == today) FontWeight.Bold else FontWeight.Normal,
                                    color = heatmapTextColor(count),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    repeat(7 - week.size) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }

        // Legend
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Less", style = MaterialTheme.typography.labelSmall, color = ZenGrayMedium)
            Spacer(Modifier.width(4.dp))
            listOf(0, 1, 2, 4).forEach { count ->
                Box(
                    Modifier
                        .size(12.dp)
                        .padding(1.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(heatmapColor(count))
                )
            }
            Spacer(Modifier.width(4.dp))
            Text("More", style = MaterialTheme.typography.labelSmall, color = ZenGrayMedium)
        }

        // Selected day detail
        if (selectedDay != null) {
            Text(
                text = selectedDay.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp)
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            // Add task input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = newTaskText,
                    onValueChange = { newTaskText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Add task for this day...") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (newTaskText.isNotBlank()) {
                            onAddScheduledTask(newTaskText, selectedDay)
                            newTaskText = ""
                        }
                    })
                )
                IconButton(
                    onClick = {
                        if (newTaskText.isNotBlank()) {
                            onAddScheduledTask(newTaskText, selectedDay)
                            newTaskText = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add task", tint = ZenIndigo)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Scheduled tasks
            if (scheduledTasksForSelectedDay.isNotEmpty()) {
                Text(
                    text = "Scheduled",
                    style = MaterialTheme.typography.labelMedium,
                    color = ZenIndigo,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                scheduledTasksForSelectedDay.forEach { task ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = task.title,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // Completed tasks
            if (tasksForSelectedDay.isNotEmpty()) {
                Text(
                    text = "Completed",
                    style = MaterialTheme.typography.labelMedium,
                    color = ZenGrayMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                tasksForSelectedDay.forEach { task ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = task.title,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            if (scheduledTasksForSelectedDay.isEmpty() && tasksForSelectedDay.isEmpty()) {
                Text(
                    text = "No tasks for this day",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Tap a day to see tasks",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}
