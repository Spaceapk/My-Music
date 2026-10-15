# Verificación de Space Music 2.0.1 beta

APK universal firmado: `Space-Music-2.0.1-beta.apk`. Identificador `org.nexlibre.spacemusic`, versionCode 4.

## Integridad

- SHA-256 del APK: `348e087ae9e023b0e15c4d4ad9b403baee695b85a8db06c8d0d0c6b1bbd1633b`.
- Certificado SHA-256: `5436f4210119058a215c0b0660dead0a9b1bed315f13d6f801645c71c942c5ce`, igual que las versiones oficiales anteriores.
- Verificación de firma, alineación de 16 KiB e identidad Android en la automatización.
- Artefacto compilado original: ejecución [38802903459](https://github.com/nexlibre/Space-Music/actions/runs/38802903459). Compilación, pruebas unitarias y lint aprobados; la primera comprobación de navegación falló por buscar el icono en lugar de su padre seleccionable. El comprobador corregido se ejecuta sobre el mismo APK firmado. No cambió el código de producción.

## Pruebas

- Pruebas unitarias Android: 977 ejecutadas, 0 fallos, 1 omitida. También se ejecutaron las pruebas del módulo compartido.
- Matriz del APK firmado: Android 12 (API 31), Android 13 (API 33), Android 15 (API 35). Ejecución [38806481333](https://github.com/nexlibre/Space-Music/actions/runs/38806481333). Los resultados de trabajos y capturas se adjuntan en el ZIP de verificación.
- Instalación sobre 1.0.2 con la misma firma; conservación de una lista de reproducción con archivos FLAC locales, edición de orden por arrastre, persistencia tras reinicio y reproducción en ese orden.
- 13 pruebas Android de almacenamiento de listas, reproducción sin red, servicio de audio y formatos locales, recuperación de errores, letras embebidas e importadas. Veinte arranques en frío.
- Barra inferior: cuatro apartados en el orden Canciones, Álbumes, Artistas y Listas; estado seleccionado, navegación y conservación al girar la pantalla.
- Apariencia clara, oscura y Liquid Glass; minirreproductor; letras FLAC, importación LRC y persistencia después de reiniciar y reinstalar como actualización; modo ambiente y pausa/segundo plano.

## Límites

Pruebas en emuladores, sin validación en teléfonos físicos. La compatibilidad de instalación mantiene minSdk 26; la matriz de pruebas comienza en Android 12. Una ejecución de Android 15 fue bloqueada por un diálogo de falta de respuesta de Pixel Launcher del emulador y se repitió. El comprobador cierra únicamente ese aviso externo del emulador; nunca oculta un aviso de falta de respuesta de Space Music. Se conservan los diagnósticos.
