# Total Music 1.1.0 — candidata en desarrollo

No es todavía una entrega instalable verificada. No fusionar ni publicar como
estable hasta completar los controles Android y la firma final.

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

## Validación registrada

Se compilaron y ejecutaron por separado con Kotlin/JVM las dos clases nuevas
de motor/configuración y sus pruebas. **11 pruebas JUnit aprobadas**:
respuesta medida de cada banda a 32/44,1/48/96 kHz; cinco tipos paramétricos;
correspondencia gráfica/digital; bypass y desactivación; limitador bajo
refuerzo extremo; balance/mono/crossfeed; saneamiento y cambios con Q alta;
persistencia de apariencia/filtros; creación/reemplazo/borrado de perfiles;
JSON importado dañado; y perfiles de fábrica finitos de 31 bandas.

También pasan `git diff --check` y `scripts/release/check_release.py`.
Estas comprobaciones no equivalen a compilar la aplicación Android completa.

## Pendiente antes de entregar

1. Confirmar que Codemagic inicia `total-music-release` sobre el SHA de esta
   rama. El YAML habilita push solo para esta rama; requiere el webhook ya
   configurado en el servicio. Si no aparece pipeline, no asumir un build.
2. Pasar las pruebas heredadas, las nuevas, lint y ensamblado release con el
   flujo existente. Revisar cualquier fallo antes de firmar.
3. Instalar y validar en Android: actualización desde 1.0.2, reproducción local,
   cambio de pista, fondo, auriculares/Bluetooth, paso entre temas, preferencias,
   restauración y efecto audible. Revisar visualmente todas las pantallas y
   medir CPU/memoria/batería con 31 bandas y ocho filtros activos.
4. Recuperar la clave original por el proceso privado existente; comprobar el
   commit y checksums del ZIP sin firmar; firmar con el APK anterior permanente;
   verificar paquete, versión y certificado. No incluir secretos en CI.
5. Guardar APK/informe/checksums y respaldo ZIP en la carpeta de Drive existente.
   Adjuntar enlaces reales a la entrega y completar el flujo de revisión.

El panel de Codemagic mostró una página en blanco al abrir el proyecto y
también su lista de aplicaciones, incluso tras recargar. No apareció un
formulario de acceso ni un error que permita atribuirlo a las credenciales.
No se ha declarado una compilación ni una firma 1.1.0 exitosa.

### Estado de cierre de esta sesión

El código quedó guardado en GitLab en el commit `06dd6cbc` y el MR en borrador
https://gitlab.com/nexlibre-group/total-music/-/merge_requests/4.
La última consulta a pipelines de esta rama devolvió cero resultados; tampoco
había estados de compilación en el commit. El disparo por push no constituye
evidencia de que Codemagic haya iniciado una compilación.

Se pudo preparar Gradle 8.11.1, Java 21 y descargar componentes del SDK local.
La comprobación Android completa se detuvo durante la configuración por la
ausencia del NDK 27.0.12077973 y de licencias aceptadas. Una comprobación
diagnóstica de Kotlin sin compilar bibliotecas nativas también se detuvo por
los componentes/licencias SDK requeridos. No se aceptaron licencias ni se
alteró el proyecto para eludir ese bloqueo. No se llegaron a compilar las
pantallas Android ni a ejecutar lint o las regresiones heredadas.

La integración de GitLab disponible permite guardar el código, pero no expone
la administración de webhooks. Al abrir esa configuración en el navegador,
GitLab solicitó iniciar sesión; el acceso seguro no se completó. La sesión
conectada para el código y la del navegador son independientes. No atribuir
el fallo de carga de Codemagic a credenciales incorrectas: no hay evidencia
para ese diagnóstico.

No hay APK 1.1.0 nuevo, informe de firma nuevo ni respaldo de esa versión en
Drive. La clave permanente y sus respaldos anteriores no se modificaron.
