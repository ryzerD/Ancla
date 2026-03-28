package co.ryzer.ancla.data

import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/**
 * Retorna una copia de la tarea con los horarios desplazados visualmente.
 * NO modifica la tarea ni la BD, solo para presentación UI.
 */
fun Task.withPostponementOffset(offsetMinutes: Long): Task {
    if (offsetMinutes <= 0L) return this

    return try {
        val shiftedStart = LocalTime.parse(startTime).plusMinutes(offsetMinutes)
        val shiftedEnd = LocalTime.parse(endTime).plusMinutes(offsetMinutes)
        this.copy(
            startTime = shiftedStart.format(timeFormatter),
            endTime = shiftedEnd.format(timeFormatter)
        )
    } catch (_: Exception) {
        this
    }
}
