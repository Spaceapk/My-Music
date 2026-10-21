#!/usr/bin/env python3
"""Check the public release identity and ensure no private key is tracked."""
import hashlib
from pathlib import Path
import re
import subprocess

root = Path(__file__).resolve().parents[2]
config = dict(line.split('=', 1) for line in
              (root / 'total-music-release.properties').read_text().splitlines()
              if line and not line.startswith('#'))
assert config['applicationId'] == 'org.nexlibre.totalmusic', 'Application identity changed'
assert int(config['versionCode']) >= 3
assert re.fullmatch(r'\d+\.\d+\.\d+', config['versionName'])
der = subprocess.run(['openssl', 'x509', '-in',
                      str(root / 'docs/total-music-signing-certificate.pem'),
                      '-outform', 'DER'], check=True, capture_output=True).stdout
assert hashlib.sha256(der).hexdigest() == config['certificateSha256'], 'Certificate fingerprint mismatch'
tracked = subprocess.run(['git', 'ls-files', '-z'], cwd=root, check=True,
                         capture_output=True).stdout.decode().split('\0')
for name in tracked:
    assert not name.lower().endswith(('.p12', '.pfx', '.jks', '.keystore')), 'Private signing file is tracked'
    assert 'signing-password' not in name.lower(), 'Signing password file is tracked'
print('Public identity and repository signing isolation verified')
