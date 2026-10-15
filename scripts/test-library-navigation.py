"""Check actual rendered bottom tabs, content routing and saved selection."""
import re
import subprocess
import time
import xml.etree.ElementTree as ET
from pathlib import Path


def adb(*args):
    return subprocess.check_output(['adb', *args], timeout=30)


def snapshot():
    adb('shell', 'uiautomator', 'dump', '--compressed', '/sdcard/navigation.xml')
    data = adb('exec-out', 'cat', '/sdcard/navigation.xml')
    Path('device-navigation.xml').write_bytes(data)
    return ET.fromstring(data)


def tab(label, selected=None):
    for _ in range(10):
        root = snapshot()
        nodes = [n for n in root.iter('node') if label in
                 (n.get('text'), n.get('content-desc')) and n.get('clickable') == 'true']
        if len(nodes) == 1 and (selected is None or nodes[0].get('selected') == str(selected).lower()):
            return nodes[0]
        time.sleep(1)
    raise AssertionError(f'Expected one reachable tab {label}, selected={selected}; found {nodes}')


def bounds(node):
    return list(map(int, re.findall(r'\d+', node.get('bounds'))))


def tap(label):
    l, t, r, b = bounds(tab(label))
    adb('shell', 'input', 'tap', str((l+r)//2), str((t+b)//2))
    tab(label, True)


# Tests run after local playback regressions, with permissions already granted.
adb('shell', 'am', 'force-stop', 'org.nexlibre.spacemusic')
adb('shell', 'am', 'start', '-W', '-n', 'org.nexlibre.spacemusic/com.music.bitchord.MainActivity')
labels = ['Songs', 'Albums', 'Artists', 'Playlists']
rects = [bounds(tab(label)) for label in labels]
assert all(a[2] <= b[0] for a, b in zip(rects, rects[1:])), rects
assert max(r[1] for r in rects) - min(r[1] for r in rects) < 12, rects
height = int(re.findall(rb'\d+x(\d+)', adb('shell', 'wm', 'size'))[-1])
assert all(r[1] > height * .65 and r[3] <= height for r in rects), (height, rects)
for label in labels:
    tap(label)
    Path('device-navigation-' + label.lower() + '.png').write_bytes(adb('exec-out', 'screencap', '-p'))
root = snapshot()
assert any(n.get('text') == 'Create playlist' for n in root.iter('node')), 'Playlists did not open'
# A configuration change must preserve selection in the hoisted saveable state.
adb('shell', 'settings', 'put', 'system', 'accelerometer_rotation', '0')
adb('shell', 'settings', 'put', 'system', 'user_rotation', '1')
time.sleep(2)
tab('Playlists', True)
adb('shell', 'settings', 'put', 'system', 'user_rotation', '0')
tap('Songs')
print('PASS: bottom order, single tabs, routing, selected accessibility state and recreation')
