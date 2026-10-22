# Total Music

Reproductor Android de música local derivado de [Star Music](https://github.com/nexlibre/Star-Music), basado en [BitChord](https://github.com/kushagrasinghx/BitChord). Este repositorio continúa la edición de Audio espacial 3 en 1 publicada en Space Music, basada en el commit `369a1f68e80dae6780d317e82d226bff06d37b01`.

Total Music conserva el reproductor local y las funciones existentes: biblioteca por canciones, artistas y álbumes; listas persistentes; búsqueda y orden; letras locales; carátulas; ecualizador; reproducción en segundo plano; controles multimedia; temas, accesibilidad y preferencias guardadas. El cliente de música no consulta servicios para letras; el cliente de actualizaciones se limita a GitHub Releases.

## Compatibilidad de instalación

Esta integración continúa Total Music con `org.nexlibre.totalmusic`, versión
`1.1.0` y `versionCode 6`. Conserva el certificado permanente de Total Music
1.0.2, cuya huella SHA-256 es
`9ae145a703a85902fe6d6ab3d886f6801873d717929deaa3e3d0b0a81de829fa`.
No debe confundirse con la antigua edición `org.nexlibre.spacemusic.spatial`,
que usa otro identificador y otro certificado. Las ramas antiguas se conservan.

El código 6 supera los candidatos recuperados de GitLab (códigos 4 y 5).
La actualización definitiva requiere recuperar la clave privada original y
verificar el APK anterior con `scripts/release/sign_apk.py`. No crear otra clave.
Los APK de pruebas usan firma debug y **no** pueden actualizar la instalación
con firma permanente. No desinstalar la aplicación existente para probarlos.

El repositorio de integración es [Spaceapk/My-Music](https://github.com/Spaceapk/My-Music).
GitLab se mantiene como respaldo hasta verificar y aprobar la integración.
Consulta [el registro de migración](docs/migration/README.md).

## Interfaz y ecualizador

Se conservan el tema Original y el tema opcional Google Expressive con colores
dinámicos, modo oscuro e ícono adaptable/monocromático recuperados de GitLab.
Total EQ ofrece diez bandas DSP independientes (31, 62, 125, 250, 500 Hz;
1, 2, 4, 8 y 16 kHz), deslizadores verticales, preamplificador, presets y
perfiles persistentes. El modo avanzado de 31 bandas y los filtros paramétricos
siguen disponibles. Las preferencias anteriores no se eliminan.

## Audio espacial 3 en 1

- **Espacial original:** conserva el procesamiento original.
- **Audio 8D:** simula una órbita estéreo con perfiles y controles guardados.
- **Espacial avanzado:** aproximación binaural paramétrica mediante diferencias de nivel, retardos interaurales y filtrado espectral.

El modo avanzado no usa HRTF medidas, seguimiento de cabeza, separación de instrumentos ni posicionamiento independiente de fuentes. Mono y multicanal se reproducen sin espacialización estéreo; el diagrama opcional es estático. Se recomiendan audífonos para percibir los efectos, pero no se han realizado pruebas auditivas o en teléfono físico en este traslado.

## Construcción y verificación

El workflow `total-music-android.yml` ejecuta pruebas compartidas/JVM, pruebas
unitarias Android, lint y compilaciones debug/release optimizada. Los artefactos
incluyen commit, identidad del APK, certificados de prueba, SHA-256 e informes.
La release sale **sin firmar** y no es una entrega definitiva. No se publica
ninguna GitHub Release automáticamente desde esta integración. El registro
`build-verification.json` declara expresamente si se verificó firma permanente
y si hubo pruebas de dispositivo; no se deben asumir pruebas en emulador ni
pruebas físicas por la mera presencia de workflows heredados.

```sh
./gradlew :shared:jvmTest :app:testProdDebugUnitTest :app:lintProdDebug :app:assembleProdRelease -PspaceSpatialEdition=true
```

Consulta [la arquitectura y límites del DSP](docs/spatial/implementation.md), [la licencia GPL-3.0](LICENSE) y los avisos conservados en el código. Los créditos de Star Music, BitChord y los autores originales se mantienen.

## Licencia y créditos

Este proyecto está bajo GPL-3.0. Se conservan la licencia, los avisos y los créditos del código original. Total Music es una modificación de Star Music y BitChord; sus autores originales mantienen sus derechos de autor.
