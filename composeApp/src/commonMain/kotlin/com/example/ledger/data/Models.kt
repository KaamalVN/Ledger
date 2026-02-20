package com.example.ledger.data

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.random.Random
import kotlin.time.Clock

// ============ DATA MODELS ============

@Serializable
data class Transaction(
    val id: String = generateId(),
    val title: String,
    val amount: Double,
    val date: String,
    val time: String,
    val category: TransactionCategory = TransactionCategory.OTHER,
    val isIncome: Boolean,
    val isRecurring: Boolean = false,
    val recurringFrequency: RecurringFrequency? = null,
    val nextPaymentDate: String? = null,
    val isDue: Boolean = false,
    val isProcessed: Boolean = false,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val lastModified: Long = Clock.System.now().toEpochMilliseconds(),
    val notes: String = "",
    val account: TransactionAccount = TransactionAccount.MAIN,
    val transferToAccount: TransactionAccount? = null
)




@Serializable
enum class TransactionAccount(val displayName: String) {
    MAIN("Main Account"),
    POCKET("Mini Pocket")
}

@Serializable
enum class RecurringFrequency(val displayName: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly")
}

@Serializable
enum class TransactionCategory(val displayName: String) {
    // Income categories
    SALARY("Salary"),
    FREELANCE("Freelance"),
    INVESTMENT("Investment"),
    GIFT("Gift"),
    POCKET_TRANSFER("Pocket Transfer"),
    OTHER_INCOME("Other Income"),
    
    // Expense categories
    FOOD("Food & Dining"),
    TRANSPORT("Transport"),
    SHOPPING("Shopping"),
    BILLS("Bills & Utilities"),
    ENTERTAINMENT("Entertainment"),
    HEALTH("Health"),
    EDUCATION("Education"),
    OTHER("Other")
}

// ============ APP SETTINGS ============

@Serializable
data class AppSettings(
    val themeMode: String = "DEFAULT",
    val lastSyncTimestamp: Long = 0L,
    val lastLocalBackupTimestamp: Long = 0L,
    val googleDriveConnected: Boolean = false,
    val autoBackupIntervalLocal: String = "OFF", // OFF, DAILY, WEEKLY
    val autoBackupIntervalCloud: String = "OFF", // OFF, DAILY, WEEKLY
    val localBackupLocation: String? = null
)

// ============ GOOGLE DRIVE MODELS ============

@Serializable
data class DriveFileMetadata(
    val id: String = "",
    val name: String = "",
    val mimeType: String = "",
    val modifiedTime: String = ""
)

@Serializable
data class DriveFileList(
    val files: List<DriveFileMetadata> = emptyList()
)

@Serializable
data class LedgerBackup(
    val version: Int = 1,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    val transactions: List<Transaction> = emptyList(),
    val recurringTemplates: List<Transaction> = emptyList(),
    val settings: AppSettings = AppSettings()
)

// ============ FORMATTERS ============

fun Double.toPriceString(): String {
    val integerPart = this.toLong()
    val fractionalPart = ((this - integerPart) * 100).toLong().let { if (it < 0) -it else it }
    // Add commas to integer part
    val intStr = integerPart.toString()
    val formatted = buildString {
        val startIndex = if (intStr.startsWith("-")) 1 else 0
        val digits = intStr.substring(startIndex)
        if (startIndex == 1) append("-")
        for (i in digits.indices) {
            if (i > 0 && (digits.length - i) % 3 == 0) append(",")
            append(digits[i])
        }
    }
    return "$formatted.${fractionalPart.toString().padStart(2, '0')}"
}

// ============ DATE/TIME HELPERS ============

fun getCurrentDateFormatted(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    return "${monthNames[now.monthNumber - 1]} ${now.dayOfMonth}, ${now.year}"
}

fun getCurrentTimeFormatted(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = now.hour
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val period = if (hour < 12) "AM" else "PM"
    return "${displayHour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')} $period"
}

fun formatTimestampFull(timestamp: Long): String {
    if (timestamp <= 0L) return "Never"
    // Temporary workaround for build issue
    return "Timestamp: $timestamp"
    /*
    val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(timestamp)
    val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val hour = dt.hour
    val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    val period = if (hour < 12) "AM" else "PM"
    val minute = dt.minute.toString().padStart(2, '0')
    return "${monthNames[dt.monthNumber - 1]} ${dt.dayOfMonth}, ${dt.year} $displayHour:$minute $period"
    */
}

fun getCurrentYear(): Int {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
}

fun getCurrentMonth(): Int {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).monthNumber
}

fun getCurrentDay(): Int {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfMonth
}

fun isPaymentDueCheck(dateString: String): Boolean {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val currentYear = now.year
    val currentMonth = now.monthNumber
    val currentDay = now.dayOfMonth
    
    val parts = dateString.split(" ")
    if (parts.size < 3) return false
    
    val month = parts[0]
    val day = parts[1].replace(",", "").toIntOrNull() ?: return false
    val year = parts[2].toIntOrNull() ?: return false
    
    val monthMap = mapOf(
        "Jan" to 1, "Feb" to 2, "Mar" to 3, "Apr" to 4,
        "May" to 5, "Jun" to 6, "Jul" to 7, "Aug" to 8,
        "Sep" to 9, "Oct" to 10, "Nov" to 11, "Dec" to 12
    )
    
    val paymentMonthNum = monthMap[month] ?: return false
    
    return when {
        year < currentYear -> true
        year > currentYear -> false
        paymentMonthNum < currentMonth -> true
        paymentMonthNum > currentMonth -> false
        day <= currentDay -> true
        else -> false
    }
}

fun calculateNextPaymentDateFromTemplate(template: Transaction): String {
    val currentDate = template.nextPaymentDate ?: template.date
    val parts = currentDate.split(" ")
    
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    
    if (parts.size >= 2) {
        val month = parts[0]
        val day = parts[1].replace(",", "").toIntOrNull() ?: 1
        val year = parts.getOrNull(2)?.toIntOrNull() ?: getCurrentYear()
        
        val monthIndex = months.indexOf(month)
        if (monthIndex == -1) return currentDate
        
        when (template.recurringFrequency) {
            RecurringFrequency.DAILY -> {
                // Simple day increment
                val daysInMonth = getDaysInMonth(monthIndex + 1, year)
                val newDay = day + 1
                return if (newDay > daysInMonth) {
                    val nextMonthIndex = (monthIndex + 1) % 12
                    val nextYear = if (nextMonthIndex == 0) year + 1 else year
                    "${months[nextMonthIndex]} 1, $nextYear"
                } else {
                    "$month $newDay, $year"
                }
            }
            RecurringFrequency.WEEKLY -> {
                val daysInMonth = getDaysInMonth(monthIndex + 1, year)
                val newDay = day + 7
                return if (newDay > daysInMonth) {
                    val nextMonthIndex = (monthIndex + 1) % 12
                    val nextYear = if (nextMonthIndex == 0) year + 1 else year
                    "${months[nextMonthIndex]} ${newDay - daysInMonth}, $nextYear"
                } else {
                    "$month $newDay, $year"
                }
            }
            RecurringFrequency.MONTHLY -> {
                val nextMonthIndex = (monthIndex + 1) % 12
                val nextYear = if (nextMonthIndex == 0) year + 1 else year
                val maxDay = getDaysInMonth(nextMonthIndex + 1, nextYear)
                val safeDay = if (day > maxDay) maxDay else day
                return "${months[nextMonthIndex]} $safeDay, $nextYear"
            }
            RecurringFrequency.YEARLY -> {
                return "$month $day, ${year + 1}"
            }
            null -> {
                // Default to monthly
                val nextMonthIndex = (monthIndex + 1) % 12
                val nextYear = if (nextMonthIndex == 0) year + 1 else year
                return "${months[nextMonthIndex]} $day, $nextYear"
            }
        }
    }
    
    return currentDate
}

private fun getDaysInMonth(month: Int, year: Int): Int {
    return when (month) {
        1 -> 31
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        3 -> 31
        4 -> 30
        5 -> 31
        6 -> 30
        7 -> 31
        8 -> 31
        9 -> 30
        10 -> 31
        11 -> 30
        12 -> 31
        else -> 30
    }
}

// Generate unique ID
fun generateId(): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    val timestamp = Clock.System.now().toEpochMilliseconds().toString().takeLast(6)
    val random = (1..10).map { chars[Random.nextInt(chars.length)] }.joinToString("")
    return "$timestamp$random"
}
