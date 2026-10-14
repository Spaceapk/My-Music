# Space Music

Reproductor Android de música local derivado de [Star Music](https://github.com/nexlibre/Star-Music), basado en [BitChord](https://github.com/kushagrasinghx/BitChord).

Conserva el motor de audio, temas, Liquid Glass, biblioteca por canciones/artistas/álbumes, listas locales persistentes, ecualizador y ajustes de reproducción. La pantalla inicial muestra archivos del dispositivo y búsqueda local. No requiere cuentas y el manifiesto elimina el permiso de Internet, también de las dependencias.

Identificador independiente: `org.nexlibre.spacemusic`. Puede instalarse junto a Star Music.

## Estado

**[Compilación verificada y APK de prueba](https://github.com/nexlibre/Space-Music/actions/runs/38640621164)**

Pasaron las pruebas de regresión, lint, compilación, instalación y arranque en Android 13 (API 33), persistencia de listas y decodificación de audio local sin conexión. En los artefactos de la ejecución, `space-android-build` contiene los APK.

El APK debug es una compilación de prueba con firma de desarrollo. La publicación definitiva con una clave estable y las pruebas manuales en teléfonos físicos siguen pendientes. No se reutilizan las claves ni secretos de Star Music.

## Licencia y créditos

GPL-3.0: se conserva LICENSE y el historial original. Los nombres de paquetes internos se mantienen para conservar compatibilidad del código. Funciones remotas heredadas permanecen en el código compartido, pero Space Music no dispone de permiso de red ni de accesos de inicio a sus pantallas.
