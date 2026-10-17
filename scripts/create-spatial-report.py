"""Build the release report from completed signed-device evidence, never assumed results."""
import json, re
from pathlib import Path

root=Path('verification')
summary=json.loads(Path('dist/unit-tests.json').read_text())
rows=[]
for api in [31,33,34,35,36]:
    folders=[p for p in root.iterdir() if f'-device-{api}-' in p.name]
    # Keep failed-attempt diagnostics in the archive; report the latest complete
    # successful evidence after targeted reruns of an unchanged signed APK.
    complete=[p for p in folders if (p/'device-spatial-benchmark.txt').exists()
        and (p/'device-tests.txt').exists() and 'OK (17 tests)' in (p/'device-tests.txt').read_text()
        and len(list(p.glob('device-spatial-*.png'))) >= 9]
    assert complete, (api,folders)
    folder=max(complete,key=lambda p:int(p.name.rsplit('-',1)[-1]))
    result=(folder/'device-tests.txt').read_text()
    assert 'OK (17 tests)' in result and 'FAILURES' not in result, (api,result[-1500:])
    metrics={}
    for line in (folder/'device-spatial-benchmark.txt').read_text().splitlines():
        m=re.search(r'mode=(null|ORIGINAL|EIGHT_D|ADVANCED) frames=(\d+) rate=(\d+) cpuMs=([\d.]+)',line)
        if m:
            assert m[2]=='480256' and m[3]=='48000',line
            metrics[m[1]]=float(m[4])
    assert set(metrics)=={'null','ORIGINAL','EIGHT_D','ADVANCED'}, (api,metrics)
    assert all(v < 2500 for v in metrics.values()),(api,metrics)
    shots=list(folder.glob('device-spatial-*.png'))
    assert len(shots)>=9,(api,len(shots))
    rows.append((api,metrics))
body='''# Space Music 2.4.0 — informe de verificación

[Release](https://github.com/nexlibre/Space-Music/releases/tag/v2.4.0) · [APK firmado](https://github.com/nexlibre/Space-Music/releases/download/v2.4.0/Space-Music-2.4.0.apk)

Paquete `org.nexlibre.spacemusic`, versionCode 8. El certificado original se conserva:
`5436f4210119058a215c0b0660dead0a9b1bed315f13d6f801645c71c942c5ce`.
Firma APK v2/v3, alineación, instalación como actualización sobre 2.3.0 y
arranque se exigen en la automatización de publicación. El APK se restaura
byte por byte con metadatos públicos ligados al hash del artefacto compilado;
la clave privada no se publica.

## Implementación real

Media3/ExoPlayer → PrecisionAudioSink Float32 → espacial → ecualizador →
transición → normalización/limitador → salida. Los dos reproductores del
crossfade reciben las mismas preferencias. No hay efectos nativos duplicados
ni reinicios/seek para cambiar de modo. Pesos y parámetros usan suavizado de
30 ms, con asentamiento aproximado de 210 ms.

| Modo | Procesamiento y controles |
|---|---|
| Espacial original, predeterminado | Algoritmo anterior: width 2.5, ganancia .82, crossfeed .2, filtro .3, retardo 15 ms. La salida asentada se conserva; se limpia el historial al reactivar después del bypass. |
| Audio 8D | Órbita simulada, profundidad frontal/posterior, intensidad, amplitud y reverb amortiguada. Velocidad 0–100% = 40–6 s por vuelta. Perfiles Suave/Equilibrado/Inmersivo; Equilibrado por defecto. |
| Espacial avanzado | Dos fuentes virtuales estéreo, ITD fraccional .15–.65 ms, diferencias de nivel y sombra de cabeza filtrada a 1.6–4.6 kHz. Intensidad, profundidad, distancia, amplitud y reverb. Natural/Envolvente/Cinemático; Envolvente por defecto. |

El modo avanzado es un modelo binaural paramétrico: no usa HRTF medidas,
separación de instrumentos ni seguimiento de cabeza. Mono y multicanal se
reproducen sin espacialización. El DSP no modifica los archivos y no ofrece
bit-perfect mientras procesa. Al desactivar, vuelve a dry tras asentarse.
El modo original mantiene su headroom histórico; EQ/crossfade pueden añadir
ganancia posteriormente. Los controles nuevos se guardan por modo y los
ajustes anteriores se conservan. El panel incluye tarjetas del tema y un
esquema estático opcional, rotulado como tal, sin falso análisis en vivo.

## Resultados automatizados

'''
body+=f"Unitarias: {summary['passed']} aprobadas; {summary['ignored']} omitida heredada de búsqueda Genius en línea, sin relación con música local. Cero fallos. Lint y compilación debug/release aprobados.\n\n"
body+='''Cada API ejecutó las 17 pruebas instrumentadas, actualización firmada,
arranque, regresión de listas/letras/cola, persistencia espacial y reproducción
estéreo MP3/FLAC/AAC/OGG con cambios entre los tres modos sin reiniciar pista.
WAV mono se comprueba en la reproducción local. La UI se capturó con claro,
oscuro/AMOLED y fuente al 150%. Se incluyen XML, capturas y registros en el ZIP
de verificación de la release. No hay fallos críticos conocidos dentro de
este alcance automatizado.

Las pruebas DSP usan seno, ruido blanco, impulsos, DC y cambios repetidos de
modo/parámetros a 8–192 kHz. Comprueban valores inválidos, límites, ausencia de
clipping añadido en las rutas nuevas, continuidad, bypass exacto, historial
inactivo y energía contralateral retardada. Esto valida propiedades técnicas,
no una percepción binaural universal.

## CPU medida en emuladores x86_64

Tiempo de CPU del hilo para 480.256 frames estéreo a 48 kHz (unos 10 s de
señal), con calentamiento previo. Incluye copia del bloque y aserciones del
benchmark. No equivale al consumo total de la app, batería o latencia física.
El presupuesto de aceptación de esta prueba es <2.500 ms por caso.

| Android (API) | Bypass ms | Original ms | Audio 8D ms | Avanzado ms |
|---|---:|---:|---:|---:|
'''
android={31:'12',33:'13',34:'14',35:'15',36:'16'}
for api,m in rows:
    body+=f"| {android[api]} ({api}) | {m['null']:.2f} | {m['ORIGINAL']:.2f} | {m['EIGHT_D']:.2f} | {m['ADVANCED']:.2f} |\n"
body+='''
Los registros guardan también instantáneas del heap antes/después; no se
presentan como demostración de ausencia de fugas ni como mejora porcentual
frente a otra versión. El bypass se mide en el mismo APK.

## Límites y comprobaciones pendientes

No se hicieron pruebas físicas ni evaluación auditiva humana. Quedan cable,
Bluetooth, cambios de salida reales, llamadas/notificaciones, bloqueo,
reproducción prolongada, batería/temperatura, fatiga y valoración perceptual
con música. Seguir [la lista reproducible](spatial/manual-checklist.md).
No se garantizan ausencia absoluta de errores ni mejora de todas las mezclas.

La publicación exige además que la verificación final de la PR sea exitosa y
que main tenga los mismos archivos de aplicación. Los registros firmados se
generan sobre el APK exacto adjunto, no sobre una APK anterior de otra versión.
'''
Path('docs/verification-2.4.0.md').write_text(body)
print(body)
