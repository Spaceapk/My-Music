# Space Music — cambios

## 1.0.2 beta — letras locales y modo ambiente

- Letras FLAC con campos alternativos, MP3 USLT UTF-8/UTF-16/Latin-1 y texto sin sincronización.
- Lectura de etiquetas después de carátulas FLAC grandes y del audio M4A.
- Importación LRC/TXT por canción, persistencia atómica y archivo musical original intacto.
- Modo ambiente opcional con colores de carátula; respeta pausa, segundo plano y reducción de animaciones.
- Traducciones de los textos nuevos y matriz activa Android 12/13/15, manteniendo compatibilidad de instalación anterior.
- [Resultados y límites de validación](docs/verification-1.0.2.md).

## 1.0.1 beta — estabilidad y listas locales

- Restauración del reproductor en su hilo principal y errores locales recuperables.
- Listas locales desde menús y selección múltiple, arrastre, deduplicación y recuperación atómica.
- WAV visible en biblioteca; contraste adaptativo y navegación inferior simplificada.
- Actualización con firma original y datos conservados.
- 1.017 pruebas aprobadas y matriz firmada Android 8/13/15. [Informe](docs/verification-1.0.1.md).

## 1.0.0 — release optimizada con firma propia

- Derivación de Star Music y BitChord; historial y licencia GPL-3.0 conservados.
- Nombre independiente y paquete Android org.nexlibre.spacemusic.
- Biblioteca inicial del dispositivo, búsqueda local, canciones, artistas, álbumes y listas persistentes.
- Temas, Liquid Glass, reproductor, ecualizador, reproducción en segundo plano, widgets y ajustes locales heredados.
- Eliminados del inicio y ajustes: cuentas, streaming, servidores, integraciones, descargas y actualizaciones en línea.
- Android Auto muestra música local; búsquedas y solicitudes de reproducción usan archivos locales.
- Sin permiso de Internet ni de lectura del estado de red en el APK final.
- Carátulas y letras incrustadas se leen del archivo. No se consultan proveedores remotos.
- GitHub Actions comprueba regresiones, compilación, permisos, persistencia de listas, decodificación sin conexión y arranque en Android.

Verificación completada: regresiones, lint, compilación, instalación, arranque, permisos sin red, persistencia y reproducción local en Android 13 (API 33). Publicada la release optimizada con firma propia, respaldo privado y verificación adicional del APK firmado en Android 13. Pendientes pruebas manuales en teléfonos físicos. El APK de publicación es el release optimizado firmado con la clave independiente de Space Music. Los APK debug son exclusivamente para pruebas.

La lógica remota heredada se conserva parcialmente como código sin acceso desde la interfaz local. La eliminación del permiso de Internet impide conexiones de red de toda la aplicación, incluidas sus dependencias.
