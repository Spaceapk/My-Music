"""Exercise local lyrics and ambient appearance through the actual player UI."""
import base64
import hashlib
import re
import subprocess
import time
import xml.etree.ElementTree as ET
from pathlib import Path

PACKAGE = 'org.nexlibre.spacemusic'
TITLE = 'SpaceLyricsFixture'

def adb(*args):
    return subprocess.check_output(['adb', *args], text=True, timeout=40)

def tree():
    adb('shell', 'uiautomator', 'dump', '--compressed', '/sdcard/space-player.xml')
    xml = adb('exec-out', 'cat', '/sdcard/space-player.xml')
    Path('device-player-ui.xml').write_text(xml)
    return ET.fromstring(xml)

def find(label):
    for _ in range(12):
        for node in tree().iter('node'):
            if label in (node.get('text'), node.get('content-desc')):
                bounds = [int(x) for x in re.findall(r'\d+', node.get('bounds',''))]
                if len(bounds) == 4 and bounds[2] > bounds[0] and bounds[3] > bounds[1]: return bounds
        time.sleep(1)
    raise AssertionError('UI element absent: ' + label)

def tap(label):
    x1,y1,x2,y2 = find(label)
    adb('shell','input','tap',str((x1+x2)//2),str((y1+y2)//2))

def screenshot(name):
    Path('device-'+name+'.png').write_bytes(subprocess.check_output(['adb','exec-out','screencap','-p'],timeout=30))

def preferences(mode):
    adb('shell','am','force-stop',PACKAGE)
    path='/data/user/0/'+PACKAGE+'/shared_prefs/bitchord_settings.xml'
    raw=adb('exec-out','cat',path)
    root=ET.fromstring(raw)
    for tag,name,value in [('boolean','space_ambient_mode','true'),('boolean','synced_lyrics','true'),('boolean','liquid_glass','true' if mode=='GLASS' else 'false'),('string','theme_mode','DARK' if mode=='GLASS' else mode),('string','last_player_screen','MAIN')]:
        e=next((e for e in root if e.get('name')==name),None)
        if e is None: e=ET.SubElement(root,tag,{'name':name})
        if tag=='string': e.text=value
        else: e.set('value',value)
    subprocess.run(['adb','shell','tee',path],input=ET.tostring(root,encoding='utf-8',xml_declaration=True),stdout=subprocess.DEVNULL,check=True,timeout=30)
    owner=adb('shell','stat','-c','%u:%g','/data/user/0/'+PACKAGE).strip()
    assert re.fullmatch(r'\d+:\d+',owner)
    adb('shell','chown',owner,path)
    adb('shell','chmod','600',path)
    adb('shell','rm','-f',path+'.bak')

def open_lyrics():
    nodes=list(tree().iter('node'))
    descriptions={n.get('content-desc') for n in nodes}
    texts={n.get('text') for n in nodes}
    if ('Close lyrics' in descriptions or
        {'Imported local lyric survives restart','Another imported line'} <= texts or
        {'Primera linea local de prueba','Segunda linea local de prueba'} <= texts):
        print('Lyrics panel already open, including restored player state')
        return
    tap('Lyrics')

def start_song():
    adb('shell','am','start','-W','-n',PACKAGE+'/com.music.bitchord.MainActivity')
    tap('Songs')
    tap(TITLE)
    adb('shell','am','start','-W','-n',PACKAGE+'/com.music.bitchord.MainActivity','--ez','bitchord.openPlayer','true')
    find('More')

# Publish a real FLAC containing timed Vorbis lyrics and a deterministic cover.
fixture=base64.b64decode(Path('docs/releases/1.0.2/lyrics-tone.flac.b64').read_bytes())
Path(TITLE+'.flac').write_bytes(fixture)
adb('push',TITLE+'.flac','/storage/emulated/0/Music/'+TITLE+'.flac')
collection='content://media/external_primary/audio/media'
args=['content','insert','--uri',collection]
for value in ['_data:s:/storage/emulated/0/Music/'+TITLE+'.flac','_display_name:s:'+TITLE+'.flac','title:s:'+TITLE,'mime_type:s:audio/flac','duration:l:180000','is_music:i:1','is_podcast:i:0','is_alarm:i:0','is_notification:i:0','is_ringtone:i:0','is_pending:i:0','is_trashed:i:0','artist:s:Test','album:s:LocalLyrics']:
    args+=['--bind',value]
insert=adb('shell',*args)
assert 'Exception' not in insert,insert
for mode in ['DARK','LIGHT','GLASS']:
    preferences(mode)
    start_song()
    screenshot('ambient-'+mode.lower())
    time.sleep(2)
    screenshot('ambient-'+mode.lower()+'-motion')
    open_lyrics()
    find('Primera linea local de prueba')
    screenshot('lyrics-'+mode.lower())
    print('Embedded timed FLAC lyrics visible in '+mode)
# Choose an LRC through the actual Android document picker.
adb('shell','input','keyevent','4')
find('Lyrics')
Path('space-import-test.lrc').write_text('[00:00.00]Imported local lyric survives restart\n[00:50.00]Another imported line')
adb('shell','mkdir','-p','/storage/emulated/0/Download')
adb('push','space-import-test.lrc','/storage/emulated/0/Download/space-import-test.lrc')
tap('More')
tap('Import lyrics (LRC / TXT)')
tap('Show roots')
tap('Downloads')
tap('space-import-test.lrc')
time.sleep(1)
adb('shell','input','keyevent','4')
open_lyrics()
find('Imported local lyric survives restart')
screenshot('lyrics-imported')
adb('shell','am','force-stop',PACKAGE)
start_song()
open_lyrics()
find('Imported local lyric survives restart')
screenshot('lyrics-imported-restarted')
adb('install','-r','--no-incremental','dist/Space-Music-2.0.1-beta.apk')
start_song()
open_lyrics()
find('Imported local lyric survives restart')
screenshot('lyrics-imported-reinstalled')
actual=adb('shell','sha256sum','/storage/emulated/0/Music/'+TITLE+'.flac').split()[0]
assert actual==hashlib.sha256(fixture).hexdigest(), 'Import changed original music file'
print('Document-picker import visible, survives process restart, and original FLAC unchanged')
adb('shell','input','keyevent','4')
find('Lyrics')
adb('shell','input','keyevent','127')
screenshot('ambient-paused')
adb('shell','input','keyevent','3')
time.sleep(1)
session=adb('shell','dumpsys','media_session')
Path('device-lyrics-session.txt').write_text(session)
assert re.search(r'\bstate=(?:[A-Z_]+\()?2\b',session),session
print('Ambient and local lyric player captures completed; pause and background checked')
