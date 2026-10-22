#!/usr/bin/env python3
"""Generate Android vector assets and density fallbacks from one geometric identity.
Requires CairoSVG and Pillow only for regenerating the legacy WebP files.
"""
from pathlib import Path
import cairosvg
from PIL import Image, ImageDraw
import io

root = Path(__file__).resolve().parents[1]
res = root / 'app/src/main/res'
note = 'M51,35 Q51,32 54,31 L67,28 Q71,27 71,31 L71,38 L57,42 L57,64 C57,73 40,77 38,68 C36,59 46,55 51,58 Z'
wave1 = 'M66,49 C72,53 72,62 66,66'
wave2 = 'M74,44 C85,52 85,65 74,73'
svg = f'''<svg xmlns="http://www.w3.org/2000/svg" width="108" height="108" viewBox="0 0 108 108">
<defs><linearGradient id="b" x2="1" y2="1"><stop stop-color="#165DDB"/><stop offset="1" stop-color="#073C9B"/></linearGradient></defs>
<rect width="108" height="108" fill="url(#b)"/>
<path d="{note}" fill="white"/>
<path d="{wave1}" fill="none" stroke="#80E3AE" stroke-width="5" stroke-linecap="round"/>
<path d="{wave2}" fill="none" stroke="#FFCF65" stroke-width="5" stroke-linecap="round"/>
<circle cx="39" cy="39" r="4" fill="#FF8177"/></svg>'''
assets = root / 'docs/branding'
assets.mkdir(parents=True, exist_ok=True)
(assets / 'total-music-icon.svg').write_text(svg)
vector_start = '<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="108dp" android:height="108dp" android:viewportWidth="108" android:viewportHeight="108">'
fg = f'{vector_start}<path android:fillColor="#FFFFFF" android:pathData="{note}"/>'
for path, color in [(wave1, '#80E3AE'), (wave2, '#FFCF65')]:
    fg += f'<path android:fillColor="@android:color/transparent" android:strokeColor="{color}" android:strokeWidth="5" android:strokeLineCap="round" android:pathData="{path}"/>'
fg += '<path android:fillColor="#FF8177" android:pathData="M35,39a4,4 0,1 0,8 0a4,4 0,1 0,-8 0"/></vector>'
(res / 'drawable/ic_launcher_foreground.xml').write_text(fg + '\n')
mono = fg.replace('#80E3AE', '#FFFFFF').replace('#FFCF65', '#FFFFFF').replace('#FF8177', '#FFFFFF')
(res / 'drawable/ic_launcher_monochrome.xml').write_text(mono + '\n')
(res / 'drawable/ic_launcher_background.xml').write_text('''<vector xmlns:android="http://schemas.android.com/apk/res/android" xmlns:aapt="http://schemas.android.com/aapt" android:width="108dp" android:height="108dp" android:viewportWidth="108" android:viewportHeight="108">
<path android:pathData="M0,0h108v108h-108z"><aapt:attr name="android:fillColor"><gradient android:startX="0" android:startY="0" android:endX="108" android:endY="108" android:startColor="#165DDB" android:endColor="#073C9B" android:type="linear"/></aapt:attr></path></vector>\n''')
for name in ['ic_launcher', 'ic_launcher_round']:
    (res / f'mipmap-anydpi/{name}.xml').write_text('''<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
    <monochrome android:drawable="@drawable/ic_launcher_monochrome"/>
</adaptive-icon>\n''')
notification = mono.replace('108dp', '24dp').replace('viewportWidth="108"', 'viewportWidth="64"').replace('viewportHeight="108"', 'viewportHeight="64"')
notification = notification.replace('><path', '><group android:translateX="-26" android:translateY="-22"><path', 1).replace('</vector>', '</group></vector>')
(res / 'drawable/ic_notification_logo.xml').write_text(notification + '\n')
png = cairosvg.svg2png(bytestring=svg.encode(), output_width=1024, output_height=1024)
image = Image.open(io.BytesIO(png)).convert('RGBA')
for density, size in [('mdpi',48),('hdpi',72),('xhdpi',96),('xxhdpi',144),('xxxhdpi',192)]:
    for rounded in [False, True]:
        icon = image.copy()
        if rounded:
            mask = Image.new('L', icon.size)
            ImageDraw.Draw(mask).ellipse((0,0,1023,1023), fill=255)
            icon.putalpha(mask)
        icon = icon.resize((size,size), Image.Resampling.LANCZOS)
        icon.save(res / f'mipmap-{density}/ic_launcher{"_round" if rounded else ""}.webp', lossless=True)
image.save(root / 'app/src/main/ic_launcher-playstore.png')
print('Generated adaptive, monochrome, notification and five density pairs')
