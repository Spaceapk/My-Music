# Total Music

Reproductor Android de música local derivado de [Star Music](https://github.com/nexlibre/Star-Music), basado en [BitChord](https://github.com/kushagrasinghx/BitChord). Este repositorio continúa la edición de Audio espacial 3 en 1 publicada en Space Music, basada en el commit `369a1f68e80dae6780d317e82d226bff06d37b01`.

Total Music conserva el reproductor local y las funciones existentes: biblioteca por canciones, artistas y álbumes; listas persistentes; búsqueda y orden; letras locales; carátulas; ecualizador; reproducción en segundo plano; controles multimedia; temas, accesibilidad y preferencias guardadas. El cliente de música no consulta servicios para letras; el cliente de actualizaciones se limita a GitHub Releases.

## Compatibilidad de instalación

Total Music mantiene el identificador Android `org.nexlibre.spacemusic.spatial`, el mismo certificado de firma que Space Music Spatial 1.0.0 y una versión superior (`versionCode 2`, `versionName 1.0.1`). Por ello está preparado para actualizar esa edición sin desinstalarla y conservar sus datos. No actualiza ni reemplaza la aplicación Space Music original, que tiene otro identificador y permanece separada.

La publicación firmada requiere disponer del PKCS#12 original y su contraseña en los secretos de GitHub Actions `SPACE_SPATIAL_KEYSTORE_B64` y `SPACE_SPATIAL_KEYSTORE_PASSWORD`. Estos secretos no se transfieren al importar el repositorio. No se debe crear una clave sustituta: el flujo valida la huella SHA-256 del certificado existente antes de publicar.

## Audio espacial 3 en 1

- **Espacial original:** conserva el procesamiento original.
- **Audio 8D:** simula una órbita estéreo con perfiles y controles guardados.
- **Espacial avanzado:** aproximación binaural paramétrica mediante diferencias de nivel, retardos interaurales y filtrado espectral.

El modo avanzado no usa HRTF medidas, seguimiento de cabeza, separación de instrumentos ni posicionamiento independiente de fuentes. Mono y multicanal se reproducen sin espacialización estéreo; el diagrama opcional es estático. Se recomiendan audífonos para percibir los efectos, pero no se han realizado pruebas auditivas o en teléfono físico en este traslado.

## Construcción y verificación

El workflow de GitHub Actions ejecuta pruebas JVM, lint, compilación optimizada y pruebas en emuladores Android 12–16. Antes de publicar, también verifica en Android 13 la instalación, el arranque y la reproducción del APK firmado con el certificado persistente, además de comprobar paquete, versión, alineación y SHA-256. Las pruebas de emulador no sustituyen pruebas físicas, Bluetooth ni escucha perceptual.

Consulta [la arquitectura y límites del DSP](docs/spatial/implementation.md), [la licencia GPL-3.0](LICENSE) y los avisos conservados en el código. Los créditos de Star Music, BitChord y los autores originales se mantienen.

## Licencia y créditos

Este proyecto está bajo GPL-3.0. Se conservan la licencia, los avisos y los créditos del código original. Total Music es una modificación de Star Music y BitChord; sus autores originales mantienen sus derechos de autor.
