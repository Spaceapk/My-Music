**[Descargar Space Music 1.0.2 beta — APK firmado](https://github.com/nexlibre/Space-Music/releases/download/v1.0.2-beta/Space-Music-1.0.2-beta.apk)**

- Lectura ampliada de letras locales: FLAC con campos estándar alternativos, MP3 con USLT ID3v2.3/v2.4, texto sin marcas de tiempo y archivos LRC/TXT del mismo nombre cuando Android permite acceder a ellos.
- Las carátulas FLAC grandes y los metadatos M4A situados después del audio ya no impiden encontrar la letra por el límite de lectura inicial.
- Nueva acción **Importar letra (LRC / TXT)** en el menú de la canción. La letra se guarda en los datos privados de la app y se conserva al actualizar; no modifica el archivo musical.
- Nuevo **Modo ambiente** en Ajustes: colores de la carátula con movimiento suave. Se detiene al pausar o quedar en segundo plano y respeta Reducir animaciones.
- La matriz de pruebas comienza en **Android 12 (API 31)**, junto con Android 13 y 15. Se conserva la compatibilidad de instalación desde Android 8.

Mantiene el identificador Android y el certificado originales: instala sobre la versión oficial anterior sin desinstalar. Sin cuentas ni permiso de Internet. Si una canción no contiene letra, importa un archivo local; la app no consulta servicios en línea ni genera la letra a partir del audio.

Verificación: **1.022 pruebas aprobadas**, cero fallos y una prueba heredada de scraping en línea omitida. Lint y compilaciones aprobados; APK optimizado y firmado comprobado en **Android 12, 13 y 15** con trece pruebas instrumentadas y veinte arranques en frío por API. Actualización desde 1.0.1, listas, arrastre, reproducción, letras incrustadas, importación desde el selector de Android y conservación tras reinicio/reinstalación aprobados; el audio original conserva su hash.

[Compilación y pruebas](https://github.com/nexlibre/Space-Music/actions/runs/38772768970) · [Aceptación del APK firmado y capturas](https://github.com/nexlibre/Space-Music/actions/runs/38776684979) · [Informe y límites](https://github.com/nexlibre/Space-Music/blob/main/docs/verification-1.0.2.md).

Las pruebas emuladas no sustituyen las pruebas físicas de llamadas, auriculares y DAC USB. La captura del propietario no permite determinar si su FLAC contiene letra: para archivos sin letra, usa la importación local.
