package hr.fipu.organizationtool.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import hr.fipu.organizationtool.data.Task
import hr.fipu.organizationtool.ui.theme.ZenGrayLight
import hr.fipu.organizationtool.ui.theme.ZenGrayMedium
import hr.fipu.organizationtool.ui.theme.ZenIndigo
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private fun heatmapColor(count: Int): Color = when {
    count == 0 -> ZenGrayLight
    count == 1 -> ZenIndigo.copy(alpha = 0.30f)
    count in 2..3 -> ZenIndigo.copy(alpha = 0.60f)
    else -> ZenIndigo
}

@Composable
fun CalendarScreen(
    completionHistory: Map<LocalDate, Int>,
    selectedDay: LocalDate?,
    tasksForSelectedDay: List<Task>,
    onDaySelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val dates = (89 downTo 0).map { today.minusDays(it.toLong()) }
    val firstDate = dates.first()
    // Monday=1..Sunday=7, padding to align Monday to column 0
    val paddingCellCount = firstDate.dayOfWeek.value % 7

    Column(modifier = modifier.fillMaxSize()) {
        // Header
        Text(
            text = "Activity",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Day-of-week labels row
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
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        // Heatmap grid
        // Build cell list: null padding cells + date cells
        val nullPadding = List<LocalDate?>(paddingCellCount) { null }
        val dateCells: List<LocalDate?> = nullPadding + dates

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .heightIn(max = 300.dp)
        ) {
            items(dateCells) { date ->
                if (date == null) {
                    Box(modifier = Modifier.size(32.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(heatmapColor(completionHistory[date] ?: 0))
                            .clickable { onDaySelected(date) }
                    )
                }
            }
        }

        // Selected day label
        if (selectedDay != null) {
            Text(
                text = selectedDay.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp)
            )
        }

        // Divider
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Day detail panel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when {
                selectedDay == null -> {
                    Text(
                        text = "Tap a day to see completed tasks",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                tasksForSelectedDay.isEmpty() -> {
                    Text(
                        text = "No tasks completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(tasksForSelectedDay) { task ->
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
                }
            }
        }
    }
}
