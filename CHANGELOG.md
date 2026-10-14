# Star Music 1.0.0-beta

Base: BitChord, commit fe95bb60aa9cd205cf4ab8bd25a5bec71a595b4f.

- Importación íntegra del código y conservación del historial original.
- Nombre Star Music, icono musical propio e identificador Android independiente. Marca propia en notificaciones, tarjetas para compartir y carpeta de descargas. Se conserva el namespace interno para reducir cambios y evitar regresiones.
- Nueva pestaña de listas de archivos locales, con creación, renombrado, borrado, selección múltiple, eliminación de entradas y orden manual.
- Persistencia atómica de metadatos, URI y ruta local, sin llamadas a servicios en línea.
- Actualizador Android dirigido al repositorio de Star Music.
- Automatización independiente para verificar, compilar y preparar el APK.
- Verificación estática con línea base de hallazgos heredados cotejados contra el código original; los hallazgos nuevos siguen bloqueando la compilación. Consulta docs/LINT-BASELINE.md. Correcciones de recursos de porcentaje literal y plurales necesarias para ejecutar lint.
- Ajustes necesarios para verificar la base importada: SDK 37.0, nombres de chino simplificado/tradicional y etiquetas de región en las pruebas de idiomas. Se retiran seis traducciones huérfanas de opciones que no están presentes en el código, detectadas por lint. La prueba de integración con Genius queda disponible mediante RUN_LIVE_GENIUS_TESTS=true; sus pruebas de análisis local se mantienen obligatorias.

Estado: pendiente de completar y verificar compilación, pruebas Android, firma y publicación.
