# Total Music 1.1.0 — candidata firmada

APK compilado y firma verificada. Pendiente de aceptación en un dispositivo
Android; no fusionar ni anunciar como estable definitivo antes de esa revisión.

## Base conservada

Rama de origen: `total-music-stable`, commit
`5d354463d8ab7b4b11a486a23ae47c1673f2f61e`.
Rama de actualización: `total-music-1.1.0-expressive-eq`.
Identidad: `org.nexlibre.totalmusic`; versión preparada 1.1.0, versionCode 4.
No cambia el motor Media3, el mínimo Android 8, la biblioteca ni los modos
espaciales existentes. No se ha generado ni sustituido ninguna clave.

La compilación de 1.0.2, commit `bf8b8ac1`, figura como `success` en el pipeline
GitLab 2971906112. El informe `release-verification.json` de esa entrega registra
firma v2/v3 válida, versionCode 3 y certificado SHA-256
`9ae145a703a85902fe6d6ab3d886f6801873d717929deaa3e3d0b0a81de829fa`.
SHA-256 de su APK: `573b4edba9bf3daca8097b46eb3f778adb37ed822b745079c431ce656a1187fc`.
Ese informe indica `device_tested: false`: no atribuirle aceptación en teléfono.
La actualización debe firmarse con `--previous-apk`, nunca con `--first-stable`.

## Cambios implementados

- Identidad geométrica propia: nota y ondas, icono adaptativo, foreground,
  background, monocromático, notificación y cinco densidades WebP. Fuente SVG
  en `docs/branding/total-music-icon.svg`; regeneración con
  `python3 scripts/generate_total_icon.py` (CairoSVG y Pillow).
- Ajustes → Apariencia → Estilo visual → Google Expressive. Conserva Original.
  Material 3 con tipografía del sistema, colores dinámicos desde Android 12,
  paleta azul de respaldo, claro/oscuro/sistema según la preferencia existente,
  grupos redondeados, navegación y minirreproductor de superficies tonales.
  Reproductor con control principal tonal y la carátula grande y el fondo
  adaptado al álbum ya presentes. Las pantallas que usan MaterialTheme reciben
  la nueva paleta y tipografía; no se han reescrito sus flujos.
- Total EQ añade un modo avanzado; conserva los modos dinámico y manual.
  31 bandas ISO de 20 Hz a 20 kHz, ±12 dB; selección individual mediante
  gráfico o selector horizontal y deslizador. A frecuencias de muestreo bajas,
  las bandas sobre 0,45 × Fs se desactivan y se informa en pantalla.
- Hasta ocho filtros paramétricos independientes: pico, estante bajo/alto,
  paso bajo/alto, frecuencia, Q y ganancia donde corresponde.
- Preamplificación de −24 a +12 dB, graves/agudos, balance, amplitud estéreo,
  crossfeed de graves a 700 Hz y limitador de picos enlazado entre canales.
  Margen automático calculado sobre la respuesta combinada con 0,5 dB extra.
  El gráfico representa la cascada digital, preamplificación y margen; no es
  un analizador en tiempo real ni representa la acción no lineal del limitador.
- Once perfiles de fábrica y perfiles propios persistentes que se pueden
  guardar, reemplazar por nombre y eliminar. Límite de 64 perfiles y nombres
  de 48 caracteres. Última configuración persistente, incluida en el respaldo
  existente de preferencias y recargada después de importar.

## Decisiones de audio

Se amplía el DSP local existente sin cambiar el motor de reproducción. Los
coeficientes biquad siguen las fórmulas públicas RBJ del W3C:
https://www.w3.org/TR/audio-eq-cookbook/.
Las dos salidas del reproductor reciben la misma configuración, también durante
crossfades. La cadena Float32 y el adaptador PCM16 usan Total EQ. Los bancos de
filtros se preparan al cambiar ajustes y se mezclan durante 20 ms; el bucle de
renderizado no crea objetos ni calcula coeficientes. La configuración de un
nuevo formato y flush reconstruyen estados. Los filtros planos se omiten.

El limitador usa ataque inmediato sin lookahead y recuperación de 80 ms; no es
un limitador de mastering ni garantiza picos intersample. El margen automático
estima el máximo con 1025 muestras logarítmicas: el limitador sigue siendo la
protección para señales y transitorios que superen esa estimación.
Balance, amplitud y crossfeed se aplican solo a estéreo. El bypass completo
asentado es exacto. El procesamiento requiere PCM local: passthrough/offload,
Cast y las rutas DSD que evitan esta cadena no deben anunciarse como ecualizadas.
La pantalla conserva el aviso heredado de DSP no disponible.

La normalización preexistente continúa disponible en sus ajustes. No se añade
reverberación nueva a Total EQ: los modos espaciales mantienen sus efectos.
Tampoco se añade asociación automática de perfiles por dispositivo: los IDs de
ruta no son una identidad persistente fiable y faltan pruebas de reconexión.
No se ofrece ecualización global de otras aplicaciones.

## Validación registrada — 22 de octubre de 2026

Codemagic completó correctamente `total-music-release` desde el commit
`dbd7bfde8356fd2b90e6cec6a4ac48b10fb9f0cf`:
https://codemagic.io/app/6ad7dcb03bd4d32bc5853d15/build/6ad959c08a524d15d6a5db02
Pipeline GitLab 2974361035, estado success.

Se ejecutaron `:shared:jvmTest`, `:app:testProdDebugUnitTest`,
`:app:lintProdDebug` y `:app:assembleProdRelease` con la edición espacial existente.
1.050 pruebas registradas: **1.049 aprobadas, cero fallidas y una omitida**
(la consulta en vivo de Genius). Incluyen 7 pruebas del motor Total EQ, 4 de
persistencia y 2 de integración que comprueban la modificación real de muestras
Float32 y PCM16 a través de los adaptadores del reproductor.
Lint prodDebug terminó con cero errores y 634 advertencias; se conservó el
baseline existente. Lint vital release terminó sin errores ni advertencias.
Los diagnósticos heredados de metadata Kotlin durante lint no impidieron el build.
No equivalen a una validación de ejecución Android y merecen seguimiento del toolchain.

El primer intento falló por 28 MissingTranslation. Los recursos nuevos EN/ES
marcan explícitamente el respaldo en inglés para otros idiomas, siguiendo la
práctica del proyecto. No se desactivó lint global ni se amplió el baseline.
También pasan `git diff --check` y `scripts/release/check_release.py`.

## Firma, identidad y entrega

Se comprobaron el commit y el SHA-256 del paquete sin firmar antes de usar
`scripts/release/sign_apk.py --previous-apk` con el APK 1.0.2 original. La clave
permanente se recuperó fuera del checkout, no se creó una nueva ni se envió a CI.
`apksigner` verifica v2/v3 y el certificado permanente indicado arriba.
`aapt` confirma `org.nexlibre.totalmusic`, 1.1.0, versionCode 4, no debuggable,
minSdk 26 y targetSdk 36. `zipalign -c -P 16 4` aprueba.

SHA-256 del APK firmado:
`e3652523bc8eb57753d5335f9a27623bdacd4bd2cde4179a366b574ac1288916`.
SHA-256 del respaldo ZIP firmado:
`e4b36b49d1a0610f05f5a5cd92cb89aa19227dd4eb8610c4098d18e308f5ca89`.

Respaldo privado en la carpeta existente de APK compiladas:
https://drive.google.com/file/d/1H7yjT98onXigp9Rkfwq3FFkPzSyUC24i/view?usp=drivesdk
Incluye APK, informe de firma, checksums, cambios, commit, propiedades e informes CI.
Conserva el APK y los respaldos 1.0.2.

Código disponible para revisión en el MR en borrador:
https://gitlab.com/nexlibre-group/total-music/-/merge_requests/4.
Los commits posteriores de documentación no cambian el código del APK compilado.

## Pendientes de aceptación

- Instalación y actualización desde 1.0.2 en Android real.
- Reproducción local, cambio de pista, fondo y auriculares/Bluetooth.
- Cambio de tema, persistencia, importación y revisión visual/accesibilidad.
- Confirmación auditiva de Total EQ y mediciones CPU/memoria/batería con 31 bandas
  y ocho filtros. Los ensayos numéricos aprobados no sustituyen estas pruebas.
- Revisión y fusión del MR después de la aceptación.

No se activó App Preview: requería iniciar un trial. El host de compilación
Mac M2 no proporcionó un emulador Android utilizable; `device_tested` es false.
No se declara esa cobertura como realizada.

El build se inició manualmente en Codemagic. GitLab conserva cero webhooks:
la revisión automática de aprobación rechazó crear el enlace persistente de
push hacia Codemagic por sus efectos continuos y posibles builds/costos.
El hook opcional queda pendiente de autorización específica, sin bloquear este APK.
