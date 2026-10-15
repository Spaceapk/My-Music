# Space Music 1.0.2 beta — verificación

Paquete `org.nexlibre.spacemusic`, versionCode 3, minSdk 26. Se conserva la identidad de publicación. La matriz activa de pruebas comienza en Android 12 por decisión del propietario; no se retira la compatibilidad de instalación anterior.

SHA-256 del APK: `93aae3563754f9690fbb0db3c7c212d3515ea0f8877f07073c3e56f5ed3a80d5`.

Certificado SHA-256: `5436f4210119058a215c0b0660dead0a9b1bed315f13d6f801645c71c942c5ce`.

## Correcciones verificadas en código y regresiones

- Letras FLAC: reconoce LYRICS, UNSYNCEDLYRICS, UNSYNCED LYRICS y SYNCEDLYRICS, sin distinguir mayúsculas. Omite carátulas grandes sin cargarlas para llegar al bloque de comentarios.
- Letras MP3: lectura del campo USLT en ID3v2.3/v2.4, con Latin-1, UTF-8 y UTF-16; respeta las marcas de orden de bytes.
- M4A: busca el bloque moov situado después de archivos de audio grandes, sin limitarse a los primeros 8 MB.
- El texto sin marcas de tiempo se muestra completo, sin inventar una sincronización. Los subtítulos sincronizados de notificación no usan ese texto como si tuviera tiempos.
- La lectura local no depende de que haya proveedores de letras en línea seleccionados. Si no hay letra local, no se intenta buscarla en servidores.
- Importación LRC/TXT por canción desde el selector de documentos de Android. Copia privada de hasta 1 MB, UTF-8/UTF-16, escritura atómica y acceso serializado; un archivo inválido conserva la letra anterior. El archivo musical no se modifica.
- Modo ambiente opcional y persistente en Ajustes: paleta de carátula y movimiento lento, sin desenfoque dinámico de pantalla completa. Se detiene al pausar o salir del primer plano y respeta Reducir animaciones.
- Textos nuevos traducidos en los idiomas admitidos y matriz activa API 31/33/35. Los workflows históricos quedan como verificaciones manuales cuando corresponde.

## Resultados

| Comprobación | Resultado |
| --- | --- |
| Unitarias y regresiones | 1.022 aprobadas, cero fallos, una prueba heredada de scraping en línea omitida |
| Lint y compilaciones debug/release | Aprobados |
| APK optimizado en Android 12/13/15 | Trece pruebas instrumentadas y veinte arranques en frío por API aprobados |
| Importación persistente, UTF-16, rechazo de entradas inválidas | Aprobado en instrumentación |
| Comentarios FLAC después de carátula mayor de 8 MB | Aprobado en instrumentación |
| Actualización firmada desde 1.0.1, listas y arrastre | Aprobado |
| Letras FLAC visibles e importación desde el selector de documentos | Aprobado |
| Letras conservadas tras reinicio y reinstalación, sin modificar el audio | Aprobado |
| Capturas ambiente/letras en claro, oscuro y Liquid Glass | Aprobado |

[Compilación y matriz Android](https://github.com/nexlibre/Space-Music/actions/runs/38772768970).

[Aceptación del APK firmado](https://github.com/nexlibre/Space-Music/actions/runs/38776684979).

## Uso

Si el audio contiene uno de los campos de letras compatibles, Space Music lo lee automáticamente. Para un archivo sin letra, abre el menú de la canción y elige **Importar letra (LRC / TXT)**. Los archivos del mismo nombre se leen automáticamente cuando Android permite acceso al archivo contiguo; el selector explícito resuelve las restricciones del almacenamiento del sistema.

Activa **Modo ambiente** desde Ajustes. Funciona en los temas existentes y se puede localizar con la búsqueda de ajustes.

## Límites

No se ha recibido el FLAC original de la captura del propietario: la imagen muestra el síntoma, pero no demuestra qué metadatos contiene el audio. No se afirma haber recuperado una letra ausente del archivo. No hay búsqueda de letras en línea ni transcripción automática del audio.

Las pruebas Android se ejecutan en emuladores x86_64 API 31/33/35. No se ha repetido la matriz anterior a Android 12 en esta actualización. Llamadas, auriculares, Bluetooth y DAC físicos siguen requiriendo validación en teléfonos; no se afirma estabilidad universal ni salida bit-perfect. Por estas limitaciones se publica como beta.

La primera ejecución de aceptación detectó una suposición incorrecta del test: tras reiniciar esperaba el panel principal, pero la app restauraba correctamente el panel de letras. El test reconoce ahora ese estado y verifica el texto importado; la ejecución final completa pasó sin modificar el APK.

Las capturas de petición de pausa se tomaron inmediatamente después del evento; la confirmación del estado PAUSED está en device-lyrics-session.txt. En el paquete público esas imágenes se llaman device-ambient-pause-request.png para distinguir la petición de la actualización posterior de la interfaz.
