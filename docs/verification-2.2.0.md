# Verificación de Space Music 2.2.0

Código verificado: `2d7448ab61dd4858b3be4d3eafab8c3f6981e296`. Aplicación `org.nexlibre.spacemusic`, versión 2.2.0, código 6. Se publica el APK universal optimizado del build aprobado, con la misma firma oficial.

- [Compilación, regresiones, lint y matriz Android](https://github.com/nexlibre/Space-Music/actions/runs/38931532375): aprobados.
- [Instalación firmada sobre 2.1.0 y aceptación del APK publicado](https://github.com/nexlibre/Space-Music/actions/runs/38937468797): aprobadas en Android 12, 13 y 15.
- Quince pruebas instrumentadas por recorrido: listas persistentes, migración, independencia de criterios, reproducción local, errores de archivo y letras locales.
- Interfaz real: tres listas con distintos criterios, persistencia tras reinicio, eliminación por identidad desde el orden visible, altura compacta de cabecera, navegación y selección tras recreación.
- Actualización sin desinstalar, listas de la versión previa, arrastre, añadir/quitar canciones, reproducción de la lista en orden y controles multimedia en segundo plano.
- Aviso de novedades una vez por versión y recuperación de notas descargadas sin conexión. Capturas de temas oscuro, claro y Liquid Glass.
- Firma SHA-256: `5436f4210119058a215c0b0660dead0a9b1bed315f13d6f801645c71c942c5ce`. Los hashes del APK se publican en la release.

Límites: pruebas en emuladores; pendiente validación en teléfonos físicos. Las listas anteriores no tenían fechas de incorporación: su orden existente se usa como referencia inicial para recientes. Las nuevas incorporaciones se registran por separado. Se conserva minSdk 26, pero la matriz de esta actualización comienza en Android 12.
