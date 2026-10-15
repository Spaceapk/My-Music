# Space Music

Reproductor Android de música local derivado de [Star Music](https://github.com/nexlibre/Star-Music), basado en [BitChord](https://github.com/kushagrasinghx/BitChord).

Conserva el motor de audio, temas, Liquid Glass, biblioteca por canciones/artistas/álbumes, listas locales persistentes, ecualizador y ajustes de reproducción. La pantalla inicial muestra archivos del dispositivo y búsqueda local. No requiere cuentas y el manifiesto elimina el permiso de Internet, también de las dependencias.

Identificador independiente: `org.nexlibre.spacemusic`. Puede instalarse junto a Star Music.

## Descarga e instalación

**[Descargar Space Music Beta 1.0.1 — APK completo firmado](https://github.com/nexlibre/Space-Music/releases/download/v1.0.1-beta/Space-Music-1.0.1-beta.apk)**

APK universal optimizado para Android 8.0 o posterior, con firma propia de publicación. Incluye arm64-v8a, armeabi-v7a y x86_64.

Pruebas aprobadas en Android 8, 13 y 15: reproducción local, listas, arrastre, veinte arranques en frío y actualización desde 1.0.0. Conserva firma y datos: instala sobre la versión oficial anterior sin desinstalar. [Resultados y límites](docs/verification-1.0.1.md). Pendiente validación en teléfonos físicos.

Si tienes instalada la versión de prueba con firma de desarrollo, debes desinstalarla antes de instalar esta. La desinstalación borra sus datos locales.

## Firma y actualizaciones

La firma de Space Music es independiente de Star Music. Su respaldo cifrado se conserva en .signing/space-release-key.cms; el material de recuperación permanece privado. Las futuras versiones deben reutilizar el mismo almacén y alias space-music, mediante SPACE_KEYSTORE_B64 y SPACE_KEYSTORE_PASSWORD. La automatización rechaza crear una nueva identidad si ya existe la clave respaldada.

## Licencia y créditos

GPL-3.0: se conserva LICENSE y el historial original. Los nombres de paquetes internos se mantienen para conservar compatibilidad del código. Funciones remotas heredadas permanecen en el código compartido, pero Space Music no dispone de permiso de red ni de accesos de inicio a sus pantallas.
