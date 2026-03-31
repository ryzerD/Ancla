# Implementación: "Tu Mapa de Calma" - Screening Tool

## Overview
Se ha implementado una herramienta completa de screening de 10 preguntas que permite a los usuarios explorar su perfil sensorial y cognitivo. Los resultados se guardan en la base de datos local y se integran con la UI.

## Archivos Creados

### 1. Entidades y DAO (Room Database)
**Archivos:**
- `/co/ryzer/ancla/data/local/assessment/UserAssessmentEntity.kt` - Entidad Room para guardar resultados
- `/co/ryzer/ancla/data/local/assessment/UserAssessmentDao.kt` - DAO para operaciones CRUD

**Características:**
- Almacenamiento de puntuación total (0-40)
- Almacenamiento del rasgo primario detectado
- Timestamp de finalización
- Datos detallados de respuestas (JSON)

### 2. Modelo de Dominio
**Archivo actualizado:**
- `/co/ryzer/ancla/data/Models.kt`

**Nuevos:**
- `UserAssessmentResult` - Modelo de dominio para assessment
- `ToolIds.CALM_MAP` - Nueva herramienta en el catálogo

### 3. Repositorio
**Archivos:**
- `/co/ryzer/ancla/data/repository/UserAssessmentRepository.kt` - Interfaz
- `/co/ryzer/ancla/data/repository/RoomUserAssessmentRepository.kt` - Implementación

### 4. ViewModel
**Archivo:**
- `/co/ryzer/ancla/ui/screening/ScreeningViewModel.kt`

**Estado:**
```kotlin
data class ScreeningUiState(
    val currentQuestion: Int = 0,
    val answers: List<Int> = List(10) { 0 },
    val totalScore: Int = 0,
    val primaryTrait: String = "",
    val hasSubmitted: Boolean = false,
    val previousAssessment: UserAssessmentResult? = null
)
```

**Funcionalidades:**
- Navegar entre preguntas
- Registrar respuestas (0-4 escala)
- Calcular puntuación total y rasgo primario
- Persistir resultados en base de datos

### 5. Pantalla de Screening
**Archivo:**
- `/co/ryzer/ancla/ui/screening/ScreeningPagerScreen.kt`

**Componentes:**
1. **ScreeningQuestionScreen** - Mostrar preguntas una a una
   - Barra de progreso
   - Animación entre preguntas
   - 5 opciones de respuesta (Nada → Extremadamente)
   - Botones Anterior/Siguiente

2. **ScreeningResultsScreen** - Mostrar resultados
   - Rasgo primario detectado
   - Puntuación total
   - Opción de retomar el cuestionario

### 6. Preguntas del Screening (10 preguntas)
```
1. ¿Con qué frecuencia las luces brillantes te resultan incómodas?
2. ¿Cuán sensible eres a los sonidos fuertes o inesperados?
3. ¿Las texturas ásperas en la ropa te causan molestia?
4. ¿Cuán fácilmente te distraen los estímulos del ambiente?
5. ¿Con qué intensidad sientes los cambios de temperatura?
6. ¿Cuán abrumador es estar en lugares muy concurridos?
7. ¿Necesitas más tiempo para procesar información nueva?
8. ¿Con qué frecuencia experimentas cansancio emocional?
9. ¿Cuán importante es la rutina y la predicibilidad para ti?
10. ¿Cuán profundamente procesas experiencias emocionales?
```

### 7. Actualización de ToolsScreen
**Archivo actualizado:**
- `/co/ryzer/ancla/ui/screens/ToolsScreen.kt`

**Cambios:**
- Nuevo parámetro `onNavigateToCalmMap`
- Nuevo parámetro `hasCompletedAssessment`
- Tarjeta "Tu Mapa de Calma" con:
  - Color: Lavanda (#E6E6FA)
  - Icono: Favorite (corazón)
  - Descripción: "Explora tu perfil sensorial y cognitivo"

### 8. Actualización de ProfileViewModel
**Archivo actualizado:**
- `/co/ryzer/ancla/ui/profile/ProfileViewModel.kt`

**Cambios:**
- Inyección de `UserAssessmentRepository`
- Observación de assessment results en StateFlow
- Combinación con perfil sensorial

### 9. Actualización de ProfileUiState
**Archivo actualizado:**
- `/co/ryzer/ancla/ui/profile/ProfileUiState.kt`

**Nuevo:**
- `hasCompletedAssessment: Boolean`

### 10. Actualización de MainScreen
**Archivo actualizado:**
- `/co/ryzer/ancla/ui/screens/MainScreen.kt`

**Cambios:**
- Nueva constante: `ROUTE_CALM_MAP = "calm_map"`
- Nuevo parámetro en `ToolsScreen`
- Nueva composición en NavHost para `ScreeningPagerScreen`
- Actualización de `hidesNavigationChrome` para incluir ROUTE_CALM_MAP

### 11. Actualización de Strings
**Archivo actualizado:**
- `/app/src/main/res/values/strings.xml`

**Nuevos strings:**
```xml
<string name="tool_calm_map_title">Tu Mapa de Calma</string>
<string name="tool_calm_map_subtitle">Explora tu perfil sensorial y cognitivo</string>
```

### 12. Base de Datos
**Archivo actualizado:**
- `/co/ryzer/ancla/data/local/AnclaDatabase.kt`
  - Version incrementado a 5
  - Agregado UserAssessmentEntity
  - Agregado UserAssessmentDao

**Migración:**
- `/co/ryzer/ancla/data/local/DatabaseMigrations.kt`
  - `MIGRATION_4_5` - Crea tabla `user_assessment`

### 13. Inyección de Dependencias
**Archivos actualizados:**
- `/co/ryzer/ancla/di/DatabaseModule.kt`
  - Agregado provider para UserAssessmentDao
  - Agregada MIGRATION_4_5

- `/co/ryzer/ancla/di/RepositoryModule.kt`
  - Agregado binding para UserAssessmentRepository

## Flujo de Uso

1. Usuario toca la tarjeta "Tu Mapa de Calma" en ToolsScreen
2. Navega a ScreeningPagerScreen
3. Responde 10 preguntas (una por pantalla)
4. Al finalizar, se calcula:
   - Puntuación total (suma de todas las respuestas 0-40)
   - Rasgo primario basado en rango:
     - 0-5: Baja Sensibilidad
     - 6-10: Sensibilidad Moderada
     - 11-15: Sensibilidad Elevada
     - 16-20: Alta Sensibilidad
     - 21+: Hipersensibilidad
5. Resultados se guardan en Room Database
6. Resultados se muestran en pantalla
7. Próxima vez que ingrese a ToolsScreen, el botón dirá "Ver mis resultados"

## Características de Diseño

- **Animaciones suaves**: Transiciones entre preguntas con slideInHorizontally
- **Accesibilidad**: Diseño limpio y simple, apropiado para usuarios con autismo
- **Responsive**: Se adapta a diferentes tamaños de pantalla
- **Progress tracking**: Barra de progreso visible durante el screening
- **No validación invasiva**: Permite cambiar respuestas libremente

## Próximas Mejoras Opcionales

1. Agregar más detalles al perfil sensorial
2. Guardar historial de assessments anteriores
3. Mostrar cambios en el perfil a lo largo del tiempo
4. Integrar resultados con recomendaciones de herramientas

## Notas Técnicas

- Todas las operaciones de BD son asincrónicas con Coroutines
- StateFlow para observación reactiva
- MVVM pattern seguido en toda la implementación
- DI con Hilt para inyección de dependencias

