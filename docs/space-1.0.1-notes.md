**[Descargar Space Music 1.0.1 beta — APK firmado](https://github.com/nexlibre/Space-Music/releases/download/v1.0.1-beta/Space-Music-1.0.1-beta.apk)**

Actualización correctiva de Space Music, reproductor de archivos locales para Android 8 o posterior. Mantiene `org.nexlibre.spacemusic`, el certificado de publicación original y los datos locales. No requiere cuentas ni permiso de Internet.

Cambios incluidos:
- Conserva las correcciones de acceso al reproductor desde su hilo principal, tratamiento de archivos inaccesibles y bloqueo seguro de solicitudes remotas heredadas.
- Contraste mejorado en controles y texto del tema oscuro/AMOLED; botón Ajustes adaptativo.
- Elimina la navegación inferior redundante y conserva el minirreproductor con espacio para los gestos del sistema.
- Incluye música WAV en el escaneo de biblioteca, manteniendo el filtro de carpetas de grabadoras.
- El menú de canciones y la selección múltiple de la biblioteca usan las mismas listas locales que la pestaña Listas.
- Creación de listas con canciones en una sola escritura atómica, eliminación de duplicados y reordenación mediante arrastre o flechas.
- Recuperación del respaldo de listas después de una escritura interrumpida. Quitar canciones o borrar listas conserva los archivos musicales.
- Reglas de optimización corregidas para las interfaces usadas por las pruebas del APK release. Configuración del emulador Android 8 corregida para evitar un fallo de permisos del comando de Wi-Fi.

Verificación completada como requisito de publicación: pruebas unitarias y de regresión, lint, compilaciones debug y release, instalación y reproducción sin red en Android 8/13/15, reproducción de MP3/FLAC/M4A/WAV, recuperación tras archivo inexistente, listas persistentes, veinte arranques en frío, continuidad del certificado y actualización desde el APK publicado. Pruebas unitarias/regresión: 1.017 aprobadas, cero fallos y una prueba heredada de scraping en línea omitida. [Compilación y matriz Android del código publicado](https://github.com/nexlibre/Space-Music/actions/runs/38730753578). La verificación firmada y sus artefactos se enlazan al final de estas notas.

La verificación emulada no sustituye las pruebas en teléfonos físicos. Si persiste un cierre en un dispositivo concreto, hace falta su traza de Android para atribuir la causa; los fallos del APK de pruebas no se presentan como prueba de ese cierre.
