#!/usr/bin/env python3
"""Sign a verified release APK locally; never send private keys to CI."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import subprocess

ROOT = Path(__file__).resolve().parents[2]


def configuration():
    return dict(line.split('=', 1) for line in
                (ROOT / 'total-music-release.properties').read_text().splitlines()
                if line and not line.startswith('#'))


def run(args):
    return subprocess.run([str(a) for a in args], check=True,
                          capture_output=True, text=True).stdout


def identity(aapt, apk):
    text = run([aapt, 'dump', 'badging', apk])
    package = re.search(r"package: name='([^']+)' versionCode='(\d+)' versionName='([^']+)'", text)
    if not package:
        raise ValueError('APK package metadata is missing')
    return {'application_id': package[1], 'version_code': int(package[2]),
            'version_name': package[3], 'debuggable': 'application-debuggable' in text}


def certificate(apksigner, apk):
    verified = run([apksigner, 'verify', '--verbose', '--print-certs', apk])
    fingerprints = re.findall(r'Signer #\d+ certificate SHA-256 digest: ([0-9a-fA-F]+)', verified)
    if len(fingerprints) != 1:
        raise ValueError('Expected exactly one signer')
    return fingerprints[0].lower()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('unsigned_apk', type=Path)
    parser.add_argument('output_directory', type=Path)
    previous = parser.add_mutually_exclusive_group(required=True)
    previous.add_argument('--previous-apk', type=Path)
    previous.add_argument('--first-stable', action='store_true')
    args = parser.parse_args()
    config = configuration()
    tools = Path(os.environ['ANDROID_BUILD_TOOLS'])
    apksigner, aapt = tools / 'apksigner', tools / 'aapt'
    info = identity(aapt, args.unsigned_apk)
    if info != {'application_id': config['applicationId'],
                'version_code': int(config['versionCode']),
                'version_name': config['versionName'], 'debuggable': False}:
        raise ValueError('Wrong package, version, or debuggable APK; refusing release')
    expected = config['certificateSha256'].lower()
    if args.previous_apk:
        old = identity(aapt, args.previous_apk)
        if certificate(apksigner, args.previous_apk) != expected:
            raise ValueError('Previous APK does not use the permanent signing key')
        if old['application_id'] != info['application_id'] or old['version_code'] >= info['version_code']:
            raise ValueError('An update must keep its package and increase versionCode')
    elif config['versionName'] != '1.0.2' or config['versionCode'] != '3':
        raise ValueError('--first-stable is only permitted for the first stable release')
    key = Path(os.environ['TOTAL_MUSIC_KEYSTORE']).resolve()
    password = Path(os.environ['TOTAL_MUSIC_PASSWORD_FILE']).resolve()
    for private in (key, password):
        if private.is_relative_to(ROOT) or not private.is_file():
            raise ValueError('Signing files must exist outside the repository')
    output = args.output_directory.resolve()
    output.mkdir(parents=True, exist_ok=True)
    target = output / f"Total-Music-{config['versionName']}-universal.apk"
    if target.exists():
        raise ValueError('Output APK already exists; refusing to replace it')
    temporary = output / '.signing-in-progress.apk'
    # All values are paths or public identifiers. Password bytes never enter argv or logs.
    run([apksigner, 'sign', '--ks', key, '--ks-key-alias', config['keyAlias'],
         '--ks-pass', f'file:{password}', '--key-pass', f'file:{password}',
         '--v4-signing-enabled', 'false', '--out', temporary, args.unsigned_apk])
    actual = certificate(apksigner, temporary)
    if actual != expected or identity(aapt, temporary) != info:
        temporary.unlink(missing_ok=True)
        raise ValueError('Signed APK verification failed')
    temporary.rename(target)
    digest = hashlib.file_digest(target.open('rb'), 'sha256').hexdigest()
    (output / 'SHA256SUMS.txt').write_text(f'{digest}  {target.name}\n')
    (output / 'release-verification.json').write_text(json.dumps({
        **info, 'certificate_sha256': actual, 'apk_sha256': digest,
        'previous_version_code': old['version_code'] if args.previous_apk else None,
        'first_stable_requires_test_build_replacement': bool(args.first_stable),
    }, indent=2) + '\n')
    print(json.dumps({'apk': str(target), 'signature_verified': True,
                      'version': info['version_name'], 'sha256': digest}))


if __name__ == '__main__':
    main()
