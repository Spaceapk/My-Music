# Revisión estática de la base original

La incorporación inicial de lint completo detectó 198 hallazgos de nivel Error en la base importada. Ninguno afecta a los nuevos archivos DevicePlaylistStore.kt o DevicePlaylistsTab.kt.

Se repararon por separado los recursos de idioma que bloqueaban el APK: traducciones sin clave base, cadenas con porcentaje literal y cantidades plurales ausentes.

La línea base registra únicamente hallazgos de estructura existentes en BitChord en el commit fe95bb60aa9cd205cf4ab8bd25a5bec71a595b4f. Cada fragmento se cotejó contra ese código original antes de incluirlo. No se incluyen hallazgos del código nuevo de listas locales. El informe original se conserva en los artefactos de la ejecución 38583015546.

Distribución: 92 anotaciones de aceptación de APIs de Media3, 65 lecturas de recursos mediante LocalContext, 10 lecturas directas de StateFlow en composición, una construcción de Flow dentro de composición y una anotación de nivel API en el código de efectos gráficos importado.

Lint sigue siendo obligatorio: cualquier hallazgo nuevo que no coincida con la línea base bloquea la compilación. La compilación release mantiene además su verificación vital. Las pruebas unitarias y Android son obligatorias y no utilizan esta línea base.

La línea base permite conservar el alcance de esta beta —identidad y listas locales— y deja identificada la deuda técnica heredada para una revisión posterior. No significa que esos hallazgos estén corregidos ni que se hayan comprobado todas las funciones en todos los teléfonos.
