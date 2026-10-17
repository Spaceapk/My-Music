"""Capture actual contextual spatial controls in light/dark themes and large font."""
import re
import subprocess
import time
import xml.etree.ElementTree as ET
from pathlib import Path
PACKAGE = 'org.nexlibre.spacemusic'

def adb(*args):
    return subprocess.check_output(['adb', *args], timeout=40)

def tree():
    adb('shell','uiautomator','dump','--compressed','/sdcard/spatial.xml')
    raw = adb('exec-out','cat','/sdcard/spatial.xml')
    Path('device-spatial-ui.xml').write_bytes(raw)
    return ET.fromstring(raw)

def find(label):
    for _ in range(8):
        for n in tree().iter('node'):
            if any(v == label or v.startswith(label + '\n') for v in (n.get('text',''), n.get('content-desc',''))):
                return n
        time.sleep(.5)
    raise AssertionError('Missing spatial UI: ' + label)

def tap(label):
    n = find(label)
    l,t,r,b = map(int,re.findall(r'\d+', n.get('bounds')))
    adb('shell','input','tap',str((l+r)//2),str((t+b)//2))
    time.sleep(.3)

def expand_sheet():
    handle=find('Drag handle')
    l,t,r,b=map(int,re.findall(r'\d+',handle.get('bounds')))
    y=(t+b)//2
    if y > 80:
        adb('shell','input','swipe',str((l+r)//2),str(y),str((l+r)//2),'40','450')
        time.sleep(.4)

def assert_selected(mode):
    root=tree(); parents={c:p for p in root.iter() for c in p}
    for n in root.iter('node'):
        if n.get('text') == mode:
            while n in parents:
                if n.get('checked') == 'true' or n.get('selected') == 'true':
                    return
                n=parents[n]
    raise AssertionError('Spatial mode did not become selected: '+mode)

def capture(name):
    Path('device-spatial-' + name + '.png').write_bytes(adb('exec-out','screencap','-p'))

size = list(map(int,re.findall(rb'(\d+)x(\d+)',adb('shell','wm','size'))[-1]))
for theme, font in [('DARK', '1.0'), ('LIGHT', '1.0'), ('DARK', '1.5')]:
    adb('shell','am','force-stop',PACKAGE)
    path = '/data/user/0/' + PACKAGE + '/shared_prefs/bitchord_settings.xml'
    preferences = ET.fromstring(adb('exec-out','cat',path))
    for tag,name,value in [('string','theme_mode',theme), ('boolean','spatial_audio','true')]:
        entry = next((e for e in preferences if e.get('name') == name), None)
        if entry is None: entry = ET.SubElement(preferences,tag,{'name':name})
        if tag == 'string': entry.text=value
        else: entry.set('value',value)
    subprocess.run(['adb','shell','tee',path],input=ET.tostring(preferences),stdout=subprocess.DEVNULL,check=True,timeout=30)
    owner=adb('shell','stat','-c','%u:%g','/data/user/0/'+PACKAGE).decode().strip()
    assert re.fullmatch(r'\d+:\d+',owner)
    adb('shell','chown',owner,path); adb('shell','rm','-f',path+'.bak')
    adb('shell','settings','put','system','font_scale',font)
    adb('shell','am','start','-W','-n',PACKAGE+'/com.music.bitchord.MainActivity')
    tap('Settings')
    # Use the actual EditText exported by Android, not its unexported hint.
    # Never start a scroll on the search field: that can open the IME and
    # turn subsequent swipes into gesture typing.
    for _ in range(20):
        root = tree()
        editors = [n for n in root.iter('node') if n.get('class') == 'android.widget.EditText']
        if editors:
            break
        adb('shell','input','swipe',str(size[0]//2),str(int(size[1]*.55)),str(size[0]//2),str(int(size[1]*.85)),'180')
    else:
        capture('missing-search-editor')
        raise AssertionError('Settings search editor is not reachable')
    if any(n.get('content-desc') == 'Clear search' for n in root.iter('node')):
        tap('Clear search')
    editor = next(n for n in tree().iter('node') if n.get('class') == 'android.widget.EditText')
    l,t,r,b = map(int,re.findall(r'\d+', editor.get('bounds')))
    adb('shell','input','tap',str((l+r)//2),str((t+b)//2))
    adb('shell','input','text','Spatial')
    ime = adb('shell','dumpsys','input_method').decode(errors='replace')
    if re.search(r'(?:mInputShown|mIsInputViewShown|isInputViewShown)=true', ime):
        adb('shell','input','keyevent','4')
    tap('Spatial audio')
    # Expand through the native handle, without dragging a card or slider.
    expand_sheet()
    label=theme.lower()+'-'+font
    for mode in ['Espacial original','Audio 8D','Espacial avanzado']:
        expand_sheet()
        tap(mode)
        expand_sheet()
        assert_selected(mode)
        text='\n'.join(n.get('text','') for n in tree().iter('node'))
        assert 'Conserva el sonido espacial actual.' in text
        if mode == 'Espacial original':
            assert 'Velocidad' not in text and 'Distancia virtual' not in text
        capture(label+'-'+mode.replace(' ','-'))
    adb('shell','input','keyevent','4')
adb('shell','settings','put','system','font_scale','1.0')
print('PASS: three real selectable spatial modes in dark/light and large font; screenshots retained')
