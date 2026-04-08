package co.ryzer.ancla.navigation

/**
 * Central repository for all navigation routes in the app.
 * Use these constants instead of hardcoding string routes to facilitate
 * future changes and maintain consistency across the application.
 */
object NavigationRoutes {
    // ── Main navigation ──────────────────────────────────────────
    const val HOME = "home"
    const val ONBOARDING = "onboarding"
    const val TOOLS = "tools"
    const val SETTINGS = "settings"

    // ── Settings sub-routes ──────────────────────────────────────
    const val SETTINGS_ORDER = "settings_order"
    const val SETTINGS_VISUAL = "settings_visual"

    // ── Tools sub-routes ────────────────────────────────────────
    const val TASKS = "tasks"
    const val SCRIPTS = "scripts"
    const val BREATHING = "breathing"
    const val CALMA_TOTAL = "calma_total"
    const val CALM_MAP = "calm_map"

    // ── Script management ────────────────────────────────────────
    const val NEW_SCRIPT = "new_script"
    const val SCRIPT_READER = "script_reader/{scriptId}"

    // ── Route arguments ─────────────────────────────────────────
    const val ARG_SCRIPT_ID = "scriptId"

    // ── Helper functions ────────────────────────────────────────
    fun scriptReaderRoute(scriptId: String): String = "script_reader/$scriptId"
}

/**
 * Default emergency contact placeholder.
 * Use this as a fallback when no actual emergency contact is set.
 */
const val EMERGENCY_CONTACT_DEFAULT = "123-456-789"

