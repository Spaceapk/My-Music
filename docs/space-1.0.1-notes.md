Actualización para reproducir exclusivamente archivos locales.

- Corregido el cierre causado por la precarga heredada que buscaba canciones locales en línea.
- Las peticiones HTTP accidentales se rechazan antes de abrir conexiones y devuelven un error controlado.
- Los archivos eliminados conservan su referencia local; un archivo inaccesible muestra un aviso y permite seleccionar otra canción.
- Corregido el acceso al reproductor desde un hilo incorrecto al reanudar.
- Evitados los escaneos simultáneos de la biblioteca.
- Mejorado el contraste de los controles, incluido el botón de ajustes.
- Eliminado el botón inferior redundante «Biblioteca». Se conserva el minirreproductor.
- APK universal optimizado con el mismo paquete y certificado que 1.0.0 beta: puede actualizarse conservando datos.

Verificación: regresiones y lint; nueve pruebas en Android, incluyendo MP3, FLAC, M4A y WAV, selección desde una cola de 731 elementos, reproductor completo, recreación de la pantalla, error de archivo inexistente y recuperación. La publicación requiere instalación y pruebas del APK firmado en Android 8, 13 y 15.
