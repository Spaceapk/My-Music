#!/usr/bin/env python3
"""Increase Total Music's release version and Android versionCode together."""
import re
from pathlib import Path
import sys

root = Path(__file__).resolve().parents[2]
path = root / 'total-music-release.properties'
text = path.read_text()
properties = dict(line.split('=', 1) for line in text.splitlines()
                  if line and not line.startswith('#'))
requested = sys.argv[1] if len(sys.argv) == 2 else ''
if not re.fullmatch(r'(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)', requested):
    raise SystemExit('Use a stable version such as 1.0.3')
if tuple(map(int, requested.split('.'))) <= tuple(map(int, properties['versionName'].split('.'))):
    raise SystemExit('The new version must be greater than the current version')
text = text.replace('versionName=' + properties['versionName'], 'versionName=' + requested)
text = text.replace('versionCode=' + properties['versionCode'],
                    'versionCode=' + str(int(properties['versionCode']) + 1))
temporary = path.with_suffix('.tmp')
temporary.write_text(text)
temporary.replace(path)
print(f"Prepared {requested}, versionCode {int(properties['versionCode']) + 1}")
