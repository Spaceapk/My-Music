# Verificación de Space Music 1.0.1 beta

APK universal: org.nexlibre.spacemusic, versionCode 2, Android 8 o posterior. Actualización compatible con la release oficial 1.0.0; conserva certificado y datos. Sin permiso de Internet.

SHA-256 del APK: `a3e71c2f2404a08acbde34e99d8914931b7f81607aa86fe30d1cd5c171f690fb`.

Certificado SHA-256: `5436f4210119058a215c0b0660dead0a9b1bed315f13d6f801645c71c942c5ce`.

## Resultados verificables

| Comprobación | Resultado |
| --- | --- |
| Unitarias/regresión, app y shared | 1.017 aprobadas, cero fallos, una prueba heredada de scraping en línea omitida |
| Lint, debug y release optimizada | Aprobados |
| APK firmado en API 26, 33 y 35 | Diez pruebas instrumentadas y veinte arranques en frío por API aprobados |
| Reproducción local sin red | MP3, FLAC, M4A y WAV; recuperación tras archivo inexistente |
| Actualización de APK oficial 1.0.0 | Instalación sobre versión anterior, conservación de lista y canciones |
| Listas y orden | Creación, persistencia, eliminación sin borrar audio, arrastre real, reinicio y reproducción en orden |
| Segundo plano y sesión multimedia | Continúa al volver al inicio; pausa, reanudación y siguiente mediante teclas multimedia |
| Interfaz | Capturas de biblioteca y ajustes: 320×640, 360×800 y 412×892; claro y oscuro, Liquid Glass donde está disponible |

[Compilación y matriz de compatibilidad](https://github.com/nexlibre/Space-Music/actions/runs/38735241735).

[Pruebas del APK firmado, actualización y listas](https://github.com/nexlibre/Space-Music/actions/runs/38738002276).

[Capturas finales de biblioteca y ajustes](https://github.com/nexlibre/Space-Music/actions/runs/38740288829).

## Correcciones

Acceso al reproductor desde el hilo principal durante restauración; archivos inaccesibles tratados como errores recuperables locales; rutas remotas heredadas bloqueadas. Listas locales compartidas por menús y selección múltiple, creación atómica con canciones, deduplicación, reordenación validada y recuperación de AtomicFile. Inclusión de WAV en biblioteca. Contraste adaptativo, ajustes visibles, barra inferior redundante retirada y encabezados adaptados a pantallas estrechas.

Se corrigieron además fallos de infraestructura y del APK de pruebas: permisos de shell en Android 8, interfaces eliminadas por R8 y formatos de dumpsys distintos entre versiones. No se atribuyen estos fallos al cierre observado en un teléfono sin su traza original.

## Límites

Pruebas ejecutadas en emuladores x86_64, no en teléfonos físicos. No se dispone de la traza del dispositivo que presentó los cierres originales. Llamadas reales, auriculares cableados/Bluetooth y DAC USB requieren validación física. Media3 conserva el manejo de audio focus y desconexión; no se afirma salida bit-perfect ni estabilidad universal. Publicación beta por estas limitaciones.

Las capturas definitivas de ajustes están en los directorios space-visual-device; se excluyeron del paquete de verificación capturas anteriores tomadas durante la transición de pantalla. En Android 8 el teclado puede aparecer al abrir ajustes; los controles siguen disponibles al cerrarlo.
