# Space Music — cambios

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
