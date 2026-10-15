"""Exercise the published app UI, retain its playlist through an APK update."""
import re
import subprocess
import sys
import time
import xml.etree.ElementTree as ET
from pathlib import Path

LABEL = 'SpaceUpgradeVerification'
TRACKS = ['SpaceUpgradeToneA', 'SpaceUpgradeToneB']

def adb(*args):
    return subprocess.check_output(['adb', *args], text=True, timeout=30)

def snapshot():
    adb('shell', 'uiautomator', 'dump', '--compressed', '/sdcard/space-upgrade.xml')
    text = adb('exec-out', 'cat', '/sdcard/space-upgrade.xml')
    Path('device-upgrade-ui.xml').write_text(text)
    return ET.fromstring(text)

def find(label=None, class_name=None):
    for attempt in range(12):
        for node in snapshot().iter('node'):
            if ((label is not None and label in (node.get('text'), node.get('content-desc')))
                    or (class_name is not None and node.get('class') == class_name)):
                bounds = [int(v) for v in re.findall(r'\d+', node.get('bounds', ''))]
                if len(bounds) == 4 and bounds[2] > bounds[0] and bounds[3] > bounds[1]:
                    return bounds
        time.sleep(1)
    raise AssertionError(f'UI element absent: {label or class_name}')

def tap(label=None, class_name=None):
    left, top, right, bottom = find(label, class_name)
    adb('shell', 'input', 'tap', str((left+right)//2), str((top+bottom)//2))

def start():
    adb('shell', 'am', 'force-stop', 'org.nexlibre.spacemusic')
    adb('shell', 'am', 'start', '-W', '-n', 'org.nexlibre.spacemusic/com.music.bitchord.MainActivity')
    tap('Playlists')

if sys.argv[1] == 'seed':
    start()
    tap('Create playlist')
    tap(class_name='android.widget.EditText')
    adb('shell', 'input', 'text', LABEL)
    tap('Save')
    tap(LABEL)
    tap('Add songs')
    for track in TRACKS: tap(track)
    tap('Save')
    for track in TRACKS: find(track)
    print('Created playlist with local audio in previous signed APK')
elif sys.argv[1] == 'verify':
    start()
    tap(LABEL)
    for track in TRACKS: find(track)
    tap('Reorder songs')
    root = snapshot()
    handles = [n for n in root.iter('node') if n.get('content-desc') == 'Drag to reorder']
    assert len(handles) == 2, 'Expected two drag handles'
    coordinates = [[int(v) for v in re.findall(r'\d+', n.get('bounds', ''))] for n in handles]
    centers = [((b[0]+b[2])//2, (b[1]+b[3])//2) for b in coordinates]
    adb('shell', 'input', 'swipe', str(centers[0][0]), str(centers[0][1]), str(centers[1][0]), str(centers[1][1]+12), '900')
    tap('Save')
    for attempt in range(12):
        texts = [n.get('text') for n in snapshot().iter('node') if n.get('text') in TRACKS]
        if texts == list(reversed(TRACKS)): break
        time.sleep(1)
    assert texts == list(reversed(TRACKS)), f'Drag order did not update: {texts}'
    start()
    tap(LABEL)
    texts = [n.get('text') for n in snapshot().iter('node') if n.get('text') in TRACKS]
    assert texts == list(reversed(TRACKS)), f'Dragged order did not persist: {texts}'
    print('Playlist membership survived upgrade and dragged order survived cold restart')
    tap('Play')
    def session_state(title, state):
        for attempt in range(15):
            dump = adb('shell', 'dumpsys', 'media_session')
            Path('device-playlist-session.txt').write_text(dump)
            if title in dump and re.search(r'\bstate=(?:[A-Z_]+\()?'+ str(state) + r'\b', dump): return
            time.sleep(1)
        raise AssertionError(f'Media session did not reach {title}, state {state}')
    session_state(TRACKS[1], 3)
    Path('device-miniplayer.png').write_bytes(subprocess.check_output(['adb', 'exec-out', 'screencap', '-p'], timeout=30))
    adb('shell', 'input', 'keyevent', '3')
    time.sleep(2)
    session_state(TRACKS[1], 3)
    adb('shell', 'input', 'keyevent', '127')
    session_state(TRACKS[1], 2)
    adb('shell', 'input', 'keyevent', '126')
    session_state(TRACKS[1], 3)
    adb('shell', 'input', 'keyevent', '87')
    session_state(TRACKS[0], 3)
    print('Reordered playlist played in order, survived backgrounding and responded to media pause/play/next')
else:
    raise SystemExit('Use seed or verify')
