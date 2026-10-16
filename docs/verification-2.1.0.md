# Space Music 2.1.0 — verificación de la entrega estable

Paquete `org.nexlibre.spacemusic`, versionCode 5, versionName `2.1.0`, sin sufijo beta. APK universal optimizado con R8. La firma original se conserva; el material privado no se publica.

## Código y compilación

- Fuente compilada: `feee435adfcfa55a285ba605eae31d133b8f05cd`.
- [Compilación, regresiones, lint y matriz Android](https://github.com/nexlibre/Space-Music/actions/runs/38907970488).
- Pruebas JVM: 978 aprobadas en app y 46 en shared; una prueba omitida, cero fallos.
- Lint: cero errores fuera del baseline heredado. El informe mantiene advertencias y 166 incidencias heredadas filtradas; no se afirma haber eliminado esa deuda técnica.
- SHA-256 del APK firmado: `45534d6e7f432de7958d22e5ce71d0f9ad8ac3f40ab91cc3b3c2dc2f5a49e2d4`.
- SHA-256 del certificado: `5436f4210119058a215c0b0660dead0a9b1bed315f13d6f801645c71c942c5ce`.
- Firma APK v2/v3 comprobada con apksigner. La reconstrucción en CI verifica los hashes de los bytes compilados y firmados; sólo se publica metadata pública de ZIP y firma.

## Comprobación con firma de publicación

[Instalación y pruebas de la versión firmada](https://github.com/nexlibre/Space-Music/actions/runs/38910702531).

La actualización firmada aprobó la validación en Android 12 (API 31), 13 (API 33) y 15 (API 35), con pantallas de 360×800 o 412×892 dp. Los scripts verifican:

- Instalación sobre la beta oficial 2.0.1, conservando una lista creada desde su interfaz.
- Persistencia de canciones y orden por arrastre después de reiniciar el proceso.
- Ausencia de flechas arriba/abajo, una sola fila de acciones y menús individuales para las canciones.
- Quitar y volver a añadir canciones, reproducir la lista y mantener la sesión en segundo plano.
- Resumen de novedades al primer inicio y ausencia después de aceptarlo. Una fixture de notas guardadas antes de instalar comprueba su presentación sin conexión.
- Almacenamiento atómico, reproducción local, letras FLAC incrustadas, importación LRC por el selector Android, ambiente, temas y veinte arranques en frío.
- La reproducción se prueba sin Wi-Fi ni datos móviles; el cliente HTTP de música permanece bloqueado.

El aviso de versiones futuras consulta `nexlibre/Space-Music/releases/latest`; sólo se admiten versiones publicadas estables. La descarga usa un cliente independiente limitado a los hosts HTTPS de GitHub y comprueba paquete, incremento de versionCode y certificado antes de ofrecer instalación.

## Incidencias de la automatización

El ensayo de reinicio espera que el diálogo desaparezca y que `seen_version=5` esté escrito antes de forzar el cierre del proceso. La primera ejecución reiniciaba inmediatamente después del toque, antes de que la interfaz terminara de procesarlo.

Durante la validación firmada, Android 13 y 15 aprobaron todas las comprobaciones en el primer intento aceptado. En Android 12 falló una lectura de pantalla por un `NullPointerException` dentro de `com.android.uiautomator.core.AccessibilityNodeInfoDumper.childNafCheck`, en el proceso de UIAutomator. La aplicación tenía un PID distinto y ya había aprobado novedades, listas, arrastre, menús, edición y las trece pruebas instrumentadas. Se repitió únicamente ese trabajo y aprobó la validación completa, manteniendo exactamente el mismo APK firmado; los artefactos conservan el diagnóstico del intento anterior. Los trabajos aceptados del intento final son: API 31 `117010458244`, API 33 `117010460004` y API 35 `117010525819`.

## Límites

La matriz corresponde a emuladores, sin validación en teléfonos físicos. Android 8–11 conserva compatibilidad de instalación declarada, pero no forma parte de la matriz de esta versión. No se simula una publicación futura real para probar de extremo a extremo el anuncio, su descarga y la pantalla del instalador del fabricante; se comprueban comparación de versiones, aislamiento de red, presentación de notas, firma e instalación del APK actual. Android conserva la confirmación de instalación y el permiso de instalar desde esta fuente.
