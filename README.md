# Space Music

Reproductor Android de música local derivado de [Star Music](https://github.com/nexlibre/Star-Music), basado en [BitChord](https://github.com/kushagrasinghx/BitChord).

Conserva el motor de audio, temas, Liquid Glass, biblioteca por canciones/artistas/álbumes, listas locales persistentes, ecualizador y ajustes de reproducción. La pantalla inicial muestra archivos del dispositivo y búsqueda local. No requiere cuentas. La reproducción y las letras son locales; el cliente de música bloquea las solicitudes de red. Un cliente separado consulta y descarga únicamente actualizaciones de GitHub.

Identificador independiente: `org.nexlibre.spacemusic`. Puede instalarse junto a Star Music.

## Descarga e instalación

**[Descargar Space Music 2.1.0 estable — APK completo firmado](https://github.com/nexlibre/Space-Music/releases/download/v2.1.0/Space-Music-2.1.0.apk)**

APK universal optimizado para Android 8.0 o posterior, con firma propia de publicación. Incluye arm64-v8a, armeabi-v7a y x86_64.

Pruebas aprobadas en Android 12, 13 y 15: reproducción local, listas, arrastre, veinte arranques en frío y actualización desde 2.0.1 beta. Conserva firma y datos: instala sobre la versión oficial anterior sin desinstalar. [Resultados y límites](docs/verification-2.1.0.md). Pendiente validación en teléfonos físicos.

Si tienes instalada la versión de prueba con firma de desarrollo, debes desinstalarla antes de instalar esta. La desinstalación borra sus datos locales.

## Biblioteca y navegación

Barra inferior flotante Liquid Glass con **Canciones, Álbumes, Artistas y Listas**, sin pestañas superiores duplicadas. El minirreproductor aparece encima de la barra. Mantiene búsqueda, ordenación y vistas de biblioteca. Liquid Glass es el valor predeterminado cuando no hay una preferencia de apariencia guardada.

## Listas compactas y actualizaciones

Reproducir, Añadir y Opciones comparten una fila. En Opciones están el orden por arrastre, renombrar y eliminar la lista. El menú de cada canción permite quitarla; se han eliminado las flechas de subir y bajar.

Desde esta versión, un aviso informa de nuevas versiones estables y permite descargar e instalar el APK. Android solicita permiso para instalar desde esta fuente cuando sea necesario. El actualizador comprueba el paquete, una versión superior y la firma antes de ofrecer la instalación. Las novedades de la versión instalada aparecen una vez; las notas de una actualización descargada se conservan para mostrarlas sin conexión.

## Letras locales y ambiente

Lee letras incrustadas compatibles, también texto sin sincronización. En el menú de la canción puedes importar un archivo LRC/TXT sin modificar el audio. El modo ambiente se activa en Ajustes y usa colores de la carátula; respeta pausa, segundo plano y reducción de animaciones. No se consultan servidores para obtener letras.

La matriz de pruebas de esta versión comienza en Android 12; se conserva minSdk 26 para instalación.

## Firma y actualizaciones

La firma de Space Music es independiente de Star Music. Su respaldo cifrado se conserva en .signing/space-release-key.cms; el material de recuperación permanece privado. Las futuras versiones deben reutilizar el mismo almacén y alias space-music, mediante SPACE_KEYSTORE_B64 y SPACE_KEYSTORE_PASSWORD. La automatización rechaza crear una nueva identidad si ya existe la clave respaldada.

## Licencia y créditos

GPL-3.0: se conserva LICENSE y el historial original. Los nombres de paquetes internos se mantienen para conservar compatibilidad del código. Funciones remotas heredadas permanecen en el código compartido, pero sus solicitudes de red siguen bloqueadas y no tienen accesos desde la navegación de Space Music. La conexión del actualizador es independiente.
