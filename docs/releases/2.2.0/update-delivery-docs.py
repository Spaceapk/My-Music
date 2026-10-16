from pathlib import Path
import os

url = os.environ["SIGNED_RUN_URL"]
r = Path(".")
p = r / "README.md"
s = p.read_text().replace("Space Music 2.1.0 estable", "Space Music 2.2.0 estable").replace("/v2.1.0/Space-Music-2.1.0.apk", "/v2.2.0/Space-Music-2.2.0.apk")
a = s.index("Pruebas aprobadas en Android 12, 13 y 15:")
b = s.index("\n\n", a)
s = s[:a] + "Pruebas aprobadas en Android 12, 13 y 15: orden independiente por lista, persistencia, reproducción local, arrastre, veinte arranques en frío y actualización desde 2.1.0 estable sin desinstalar. Conserva firma y datos. [Resultados y límites](docs/verification-2.2.0.md). Pendiente validación en teléfonos físicos." + s[b:]
s = s.replace("Reproducir, Añadir y Opciones comparten una fila.", "Cabecera propia de dos filas, búsqueda al tocar la lupa y controles Reproducir, Añadir, Ordenar y Opciones. Cada lista conserva su orden independiente: manual, título A–Z/Z–A, artista o incorporaciones recientes. El orden de Canciones sigue siendo independiente.")
p.write_text(s)
p = r / "CHANGELOG.md"; p.write_text(p.read_text().replace("## 2.2.0 — en verificación", "## 2.2.0 — estable", 1))
notes = """**[Descargar Space Music 2.2.0 estable — APK completo firmado](https://github.com/nexlibre/Space-Music/releases/download/v2.2.0/Space-Music-2.2.0.apk)**

- Cabecera de listas compacta, con búsqueda al tocar la lupa y ajustes en Opciones.
- Orden independiente guardado por lista: manual, título A–Z/Z–A, artista o añadidas recientemente a esa lista.
- Reproducción y eliminación respetan el orden visible. Ordenar una lista no cambia Canciones ni las otras listas.
- Guardar el orden por arrastre activa el modo manual y conserva el historial de incorporación.
- Migración de listas existentes sin perder canciones. Como antes no se guardaban fechas de incorporación, se usa su orden existente como referencia inicial; las nuevas incorporaciones se registran por separado.

Instala sobre la versión oficial anterior sin desinstalar para conservar tus listas y preferencias. Mantiene el identificador y la firma oficiales. La matriz de pruebas comienza en Android 12; se conserva la compatibilidad de instalación desde Android 8.

[Compilación, regresiones, lint y pruebas Android 12/13/15 del código verificado](https://github.com/nexlibre/Space-Music/actions/runs/38931532375).
""" + "\nVerificación del APK firmado y actualización desde 2.1.0: " + url + "\n"
(r / "docs/space-2.2.0-notes.md").write_text(notes)
(r / "docs/verification-2.2.0.md").write_text("""# Verificación de Space Music 2.2.0

Código verificado: `2d7448ab61dd4858b3be4d3eafab8c3f6981e296`. Aplicación `org.nexlibre.spacemusic`, versión 2.2.0, código 6. Se publica el APK universal optimizado del build aprobado, con la misma firma oficial.

- [Compilación, regresiones, lint y matriz Android](https://github.com/nexlibre/Space-Music/actions/runs/38931532375): aprobados.
- [Instalación firmada sobre 2.1.0 y aceptación del APK publicado](""" + url + """): aprobadas en Android 12, 13 y 15.
- Quince pruebas instrumentadas por recorrido: listas persistentes, migración, independencia de criterios, reproducción local, errores de archivo y letras locales.
- Interfaz real: tres listas con distintos criterios, persistencia tras reinicio, eliminación por identidad desde el orden visible, altura compacta de cabecera, navegación y selección tras recreación.
- Actualización sin desinstalar, listas de la versión previa, arrastre, añadir/quitar canciones, reproducción de la lista en orden y controles multimedia en segundo plano.
- Aviso de novedades una vez por versión y recuperación de notas descargadas sin conexión. Capturas de temas oscuro, claro y Liquid Glass.
- Firma SHA-256: `5436f4210119058a215c0b0660dead0a9b1bed315f13d6f801645c71c942c5ce`. Los hashes del APK se publican en la release.

Límites: pruebas en emuladores; pendiente validación en teléfonos físicos. Las listas anteriores no tenían fechas de incorporación: su orden existente se usa como referencia inicial para recientes. Las nuevas incorporaciones se registran por separado. Se conserva minSdk 26, pero la matriz de esta actualización comienza en Android 12.
""")
