@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.ledger.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ledger.data.getCurrentDay
import com.example.ledger.data.getCurrentMonth
import com.example.ledger.data.getCurrentYear
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

// ============ STATE CLASSES ============

data class DateState(
    val year: Int = getCurrentYear(),
    val month: Int = getCurrentMonth(),  // 1-12
    val day: Int = getCurrentDay()
) {
    fun toDisplayString(): String {
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        return "${monthNames[month - 1]} $day, $year"
    }
    
    fun toFormattedString(): String = toDisplayString()
    
    fun toMillis(): Long {
        return try {
            // Use UTC so Material3 DatePicker (which works in UTC midnight) shows the correct date
            val date = LocalDate(year, month, day)
            val instant = date.atStartOfDayIn(TimeZone.UTC)
            instant.toEpochMilliseconds()
        } catch (e: Exception) {
            // Fallback for invalid dates
            val daysFromEpoch = (year - 1970) * 365L + (month - 1) * 30L + day
            daysFromEpoch * 24 * 60 * 60 * 1000
        }
    }
    
    fun toIsoString(): String {
        return "${year.toString().padStart(4, '0')}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
    }
    
    companion object {
        fun fromMillis(millis: Long): DateState {
            return try {
                // Use UTC to match how DatePicker stores dates (UTC midnight)
                val instant = Instant.fromEpochMilliseconds(millis)
                val localDate = instant.toLocalDateTime(TimeZone.UTC).date
                DateState(
                    year = localDate.year,
                    month = localDate.monthNumber,
                    day = localDate.dayOfMonth
                )
            } catch (e: Exception) {
                // Fallback
                val days = millis / (24 * 60 * 60 * 1000)
                val year = 1970 + (days / 365).toInt()
                val remainingDays = days % 365
                val month = (remainingDays / 30).toInt() + 1
                val day = (remainingDays % 30).toInt() + 1
                DateState(
                    year = year.coerceIn(1970, 2100),
                    month = month.coerceIn(1, 12),
                    day = day.coerceIn(1, 31)
                )
            }
        }
        
        fun now(): DateState {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            return DateState(now.year, now.monthNumber, now.dayOfMonth)
        }
        
        fun fromFormattedString(formatted: String): DateState {
            return try {
                // formatted is like "Oct 24, 2023"
                val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                val parts = formatted.replace(",", "").split(" ")
                val month = monthNames.indexOf(parts[0]) + 1
                val day = parts[1].toInt()
                val year = parts[2].toInt()
                DateState(year, month, day)
            } catch (e: Exception) {
                now()
            }
        }
    }
}

data class TimeState(
    val hour: Int = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour,
    val minute: Int = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).minute,
    val is24Hour: Boolean = false
) {
    fun toDisplayString(): String {
        return if (is24Hour) {
            "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
        } else {
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            val period = if (hour < 12) "AM" else "PM"
            "${displayHour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')} $period"
        }
    }
    
    companion object {
        fun now(): TimeState {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            return TimeState(now.hour, now.minute, false)
        }
        
        fun fromDisplayString(display: String): TimeState {
            return try {
                // display is like "10:30 AM" or "22:30"
                if (display.contains("AM") || display.contains("PM")) {
                    val parts = display.split(" ")
                    val timeParts = parts[0].split(":")
                    var h = timeParts[0].toInt()
                    val m = timeParts[1].toInt()
                    val period = parts[1]
                    if (period == "PM" && h < 12) h += 12
                    if (period == "AM" && h == 12) h = 0
                    TimeState(h, m, false)
                } else {
                    val timeParts = display.split(":")
                    TimeState(timeParts[0].toInt(), timeParts[1].toInt(), true)
                }
            } catch (e: Exception) {
                now()
            }
        }
    }
}

// ============ DATE PICKER DIALOG ============

@Composable
fun DatePickerDialog(
    selectedDate: DateState,
    onDismiss: () -> Unit,
    onConfirm: (DateState) -> Unit,
    modifier: Modifier = Modifier
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.toMillis()
    )
    
    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onConfirm(DateState.fromMillis(millis))
                    } ?: onConfirm(selectedDate)
                }
            ) {
                Text("OK", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        modifier = modifier
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = true,
            title = {
                Text(
                    text = "Select date",
                    modifier = Modifier.padding(start = 24.dp, top = 16.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            },
            headline = {
                Text(
                    text = datePickerState.selectedDateMillis?.let {
                        DateState.fromMillis(it).toDisplayString()
                    } ?: selectedDate.toDisplayString(),
                    modifier = Modifier.padding(start = 24.dp, bottom = 12.dp),
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        )
    }
}

// ============ TIME PICKER DIALOG ============

@Composable
fun TimePickerDialog(
    selectedTime: TimeState,
    onDismiss: () -> Unit,
    onConfirm: (TimeState) -> Unit,
    modifier: Modifier = Modifier
) {
    val timePickerState = rememberTimePickerState(
        initialHour = selectedTime.hour,
        initialMinute = selectedTime.minute,
        is24Hour = selectedTime.is24Hour
    )
    
    var showingInput by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select time",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Toggle between dial and input
                    IconButton(onClick = { showingInput = !showingInput }) {
                        Text(
                            text = if (showingInput) "DIAL" else "INPUT",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(Modifier.height(20.dp))
                
                // Time picker
                if (showingInput) {
                    TimeInput(
                        state = timePickerState,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    TimePicker(
                        state = timePickerState
                    )
                }
                
                Spacer(Modifier.height(24.dp))
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            onConfirm(
                                TimeState(
                                    hour = timePickerState.hour,
                                    minute = timePickerState.minute,
                                    is24Hour = timePickerState.is24hour
                                )
                            )
                        }
                    ) {
                        Text("OK", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
