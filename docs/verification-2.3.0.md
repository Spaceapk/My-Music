# Verificación de Space Music 2.3.0

Código compilado: `876da7cbf45eca7999aeae8f574c9a86a095a21c`. Paquete `org.nexlibre.spacemusic`, versión 2.3.0, código 7.

La publicación exige regresiones, lint, compilación debug y release optimizada, pruebas de ejecución y comprobaciones del APK firmado en emuladores Android 12, 13 y 15. El flujo de publicación instala sobre 2.2.0 sin desinstalar, verifica la conservación de listas y los avisos de novedades, y repite las pruebas locales de reproducción, listas, letras, interfaz compacta y licencias sin conexión.

Firma SHA-256: `5436f4210119058a215c0b0660dead0a9b1bed315f13d6f801645c71c942c5ce`. Se comprueban firma, alineación, versión, paquete y hash del APK. La clave privada no se incorpora al repositorio. Los metadatos públicos de firma permiten reconstruir exactamente el APK firmado a partir del artefacto compilado verificado.

Resultados de compilación: https://github.com/nexlibre/Space-Music/actions/runs/38999125631

El flujo de publicación conserva capturas, resultados instrumentados y registros en el ZIP de verificación de la release. Límites: emuladores Android 12/13/15; pendiente validación en teléfonos físicos. Se conserva minSdk 26.
