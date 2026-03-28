package co.ryzer.ancla.ui.common

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

private val storageTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val singleDigitStorageFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")

fun formatTimeForDisplay(rawValue: String, locale: Locale = Locale.getDefault()): String {
    val parsed = parseTime(rawValue, locale) ?: return rawValue
    return parsed.format(DateTimeFormatter.ofPattern("hh:mm a", locale))
}

fun formatTimeRangeForDisplay(
    startTime: String,
    endTime: String,
    locale: Locale = Locale.getDefault()
): String {
    val formattedStart = formatTimeForDisplay(startTime, locale)
    val formattedEnd = formatTimeForDisplay(endTime, locale)
    return "$formattedStart - $formattedEnd"
}

private fun parseTime(rawValue: String, locale: Locale): LocalTime? {
    val normalized = rawValue.trim()
    if (normalized.isEmpty()) return null

    val formatters = listOf(
        storageTimeFormatter,
        singleDigitStorageFormatter,
        DateTimeFormatter.ofPattern("hh:mm a", locale),
        DateTimeFormatter.ofPattern("h:mm a", locale)
    )

    formatters.forEach { formatter ->
        try {
            return LocalTime.parse(normalized, formatter)
        } catch (_: DateTimeParseException) {
            // Keep trying supported formats.
        }
    }

    return null
}

