# Verificación de Space Music 2.3.0

Resultados de compilación y aceptación del APK firmado: **aprobados**.

Código compilado: `876da7cbf45eca7999aeae8f574c9a86a095a21c`. Paquete `org.nexlibre.spacemusic`, versión 2.3.0, código 7.

- [Compilación, regresiones, lint y pruebas Android](https://github.com/nexlibre/Space-Music/actions/runs/38999125631).
- [Aceptación del APK firmado y actualización sobre 2.2.0](https://github.com/nexlibre/Space-Music/actions/runs/39000880443).
- APK universal optimizado: firma v2/v3, alineación de bibliotecas nativas, paquete, versión y hash comprobados.
- Matriz de emuladores Android 12, 13 y 15: reproducción local sin conexión, quince pruebas instrumentadas, veinte arranques en frío, navegación, orden independiente de listas, persistencia, eliminación según el orden visible y licencias disponibles sin conexión.
- Instalación del APK firmado sobre 2.2.0 sin desinstalar; conservación de una lista anterior, reordenación por arrastre, añadir/quitar canciones y reproducción en el orden guardado.
- Aviso de novedades una vez por versión y notas descargadas disponibles sin conexión. Capturas de temas oscuro, claro y Liquid Glass.

Firma SHA-256 del certificado: `5436f4210119058a215c0b0660dead0a9b1bed315f13d6f801645c71c942c5ce`.

SHA-256 del APK: `991682c919a4f48bacc02c1e53fd03a4247a31fec5c66f7bffa079fecb6a023d`.

La clave privada no se incorpora al repositorio. Los metadatos públicos de firma permiten reconstruir exactamente el APK firmado a partir del artefacto compilado verificado. El flujo de publicación conserva capturas, resultados instrumentados y registros en el ZIP de verificación de la release.

Límites: emuladores Android 12/13/15; pendiente validación en teléfonos físicos. Se conserva minSdk 26. El arnés espera a que termine la sustitución del paquete antes de abrir la actualización, pues Android 12 puede entregar sus avisos de reemplazo después de que `adb install` devuelva éxito.
