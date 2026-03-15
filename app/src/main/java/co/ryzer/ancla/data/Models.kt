package co.ryzer.ancla.data

data class UserProfile(
    val name: String = ""
)

data class SensoryProfile(
    val id: Int = 1,
    val name: String = "",
    val emergencyContact: String = "",
    val selectedColorId: String = "lavender"
)

data class Task(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val time: String,
    val isCompleted: Boolean = false
)

data class Script(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val subtitle: String,
    val message: String,
    val categoryId: String,
    val styleId: String,
    val position: Int,
    val showEmergencyContact: Boolean = false
)

object ToolIds {
    const val DECODER = "decoder"
    const val TASKS = "tasks"
    const val SCRIPTS = "scripts"
    const val BREATHING = "breathing"
    const val SOS = "sos"
}

data class ToolOrderEntry(
    val toolId: String,
    val position: Int
)

val DefaultToolOrder: List<ToolOrderEntry> = listOf(
    ToolOrderEntry(toolId = ToolIds.DECODER, position = 0),
    ToolOrderEntry(toolId = ToolIds.TASKS, position = 1),
    ToolOrderEntry(toolId = ToolIds.SCRIPTS, position = 2),
    ToolOrderEntry(toolId = ToolIds.BREATHING, position = 3),
    ToolOrderEntry(toolId = ToolIds.SOS, position = 4)
)

