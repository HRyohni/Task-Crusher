package hr.fipu.organizationtool.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

    Column(modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        // Header — Change 2: titleLarge replaces headlineMedium
        Text(
            text = "Activity",
            style = MaterialTheme.typography.titleLarge,
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

        // Non-lazy grid — avoids nested scroll conflict with the outer verticalScroll Column.
        // The cell count is bounded (max 96 cells for 90 days + up to 6 padding cells).
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
            dateCells.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { date ->
                        if (date == null) {
                            // Change 1: empty cell uses weight+aspectRatio (no nested Box)
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                        } else {
                            // Change 1: single Box with weight+aspectRatio replaces nested Box(weight) { Box(size) }
                            // Change 3: today indicator border appended after background
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(heatmapColor(completionHistory[date] ?: 0))
                                    .then(if (date == today) Modifier.border(1.5.dp, ZenIndigo, RoundedCornerShape(4.dp)) else Modifier)
                                    .clickable { onDaySelected(date) }
                            )
                        }
                    }
                    // Fill remaining cells in last row if week is not full
                    repeat(7 - week.size) {
                        // Change 1: trailing filler cells also use weight+aspectRatio
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }

        // Change 4: Legend row below heatmap grid
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

        // Selected day label
        if (selectedDay != null) {
            Text(
                text = selectedDay.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp)
            )
        }

        // Change 5: Divider only renders when a day is selected
        if (selectedDay != null) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Change 6: Left-aligned detail panel — removed contentAlignment = Alignment.Center
        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
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
                    Column {
                        tasksForSelectedDay.forEach { task ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
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
