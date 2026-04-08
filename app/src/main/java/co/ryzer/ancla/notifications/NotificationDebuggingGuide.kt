/**
 * GUÍA DE DEBUGGING - NOTIFICACIONES
 *
 * Las notificaciones ahora son mucho más robustas. Si aún tienes problemas,
 * usa estos logs para diagnosticar:
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * FLUJO ESPERADO DE LOGS:
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * 1. Cuando programas una alarma (TaskAlarmManager):
 *    D/AnclaTaskAlarmManager: Scheduling alarm for taskId=TASK_ID, title=..., triggerTime=...
 *    I/AnclaTaskAlarmManager: Exact alarm scheduled for taskId=TASK_ID
 *    (o) W/AnclaTaskAlarmManager: Exact alarms not permitted... using inexact alarm
 *
 * 2. Cuando suena la alarma (TaskAlarmReceiver):
 *    D/AnclaTaskAlarmReceiver: Alarm received for taskId=TASK_ID, showing notification
 *
 * 3. Cuando se muestra la notificación (NotificationHelper):
 *    D/AnclaNotifications: Notification channel created successfully
 *    D/AnclaNotifications: Notification posted successfully for taskId=TASK_ID
 *
 * 4. Cuando tocas la notificación:
 *    - La app abre en MainActivity
 *    - Se navega a Home automáticamente
 *    - La notificación desaparece (autoCancel=true)
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * TROUBLESHOOTING:
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * ❌ Log: "Skipping notification: title or message is empty"
 *    → La alarma se dispara pero title o message son vacíos
 *    → Revisa que TASK_TITLE y TASK_CATEGORY se pasan correctamente en TaskAlarmManager
 *
 * ❌ Log: "POST_NOTIFICATIONS permission not granted"
 *    → Falta permisos de notificaciones en Android 13+
 *    → Solución: Pedirle permisos al usuario (ya se hace en MainActivity.kt)
 *
 * ❌ Log: "Color resource not found, using default"
 *    → R.color.purple_500 no existe en tu proyecto
 *    → Solución: Agregar el color en colors.xml o dejarlo con fallback
 *
 * ❌ Log: "Invalid startTime format: ..."
 *    → El formato de hora no es "HH:mm" (ej: "09:30")
 *    → Solución: Validar que Task.startTime siempre esté en formato válido
 *
 * ❌ Log: "Exact alarms not permitted on this device"
 *    → Dispositivo con Android 12+ y permisos restringidos
 *    → Solución: Automático - se usa alarma inexacta como fallback
 *    → Nota: La notificación llegará pero con ±1 minuto de variación
 *
 * ❌ Log: "No pending alarm found to cancel for taskId=..."
 *    → La alarma ya fue disparada o no existe
 *    → Normal - no es un error
 *
 * ❌ Log: "SecurityException while posting notification"
 *    → Error de permisos en tiempo de ejecución
 *    → Solución: Verificar que AndroidManifest.xml tiene POST_NOTIFICATIONS
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * CÓMO VER LOS LOGS:
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * En Android Studio:
 * 1. View → Tool Windows → Logcat
 * 2. Filtrar por tag:
 *    - "AnclaTaskAlarmManager"
 *    - "AnclaTaskAlarmReceiver"
 *    - "AnclaNotifications"
 * 3. Nivel: Debug (D) y superiores (W, E)
 *
 * Comando adb:
 *    adb logcat | grep "Ancla"
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * CARACTERÍSTICAS DE SEGURIDAD IMPLEMENTADAS:
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * ✅ Try-catch en todos los puntos críticos
 * ✅ Validación de entrada (titles, messages, taskId)
 * ✅ Permisos checked en runtime (Android 13+)
 * ✅ Fallback a alarmas inexactas si exactas no permitidas
 * ✅ Logging detallado para debugging
 * ✅ Constantes centralizadas (extras, delays, etc.)
 * ✅ Manejo seguro de resources (color, drawable con fallback)
 * ✅ Cancelación de alarmas sin fallar si no existen
 * ✅ Validación de formato de hora con fallback a 08:00
 * ✅ Uso de abs() para garantizar IDs de notificación positivos
 *
 */

