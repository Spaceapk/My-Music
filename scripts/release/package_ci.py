#!/usr/bin/env python3
"""Package verifiable TEST and UNSIGNED APKs; never claim a signed release."""
import hashlib
import json
import os
from pathlib import Path
import shutil
import subprocess
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(Path(__file__).parent))
from sign_apk import configuration, identity, certificate


def main():
    config = configuration()
    tools = Path(os.environ['ANDROID_HOME']) / 'build-tools' / '36.0.0'
    output = ROOT / 'release-artifacts'
    output.mkdir(exist_ok=True)
    commit = subprocess.check_output(['git', 'rev-parse', 'HEAD'], cwd=ROOT, text=True).strip()
    apks = []
    for kind, pattern in [('test-only-debug-signed', '*universal*debug.apk'), ('unsigned-release-NOT-INSTALLABLE', '*universal*release-unsigned.apk')]:
        matches = list((ROOT / 'app/build/outputs/apk').rglob(pattern))
        if len(matches) != 1:
            raise ValueError(f'Expected exactly one {kind} universal APK, found {len(matches)}')
        source = matches[0]
        info = identity(tools / 'aapt', source)
        expected = dict(application_id=config['applicationId'], version_code=int(config['versionCode']),
                        version_name=config['versionName'], debuggable=kind.startswith('test'))
        if info != expected:
            raise ValueError(f'APK identity does not match release configuration: {info}')
        fingerprint = certificate(tools / 'apksigner', source) if kind.startswith('test') else None
        if kind.startswith('test') and fingerprint == config['certificateSha256']:
            raise ValueError('Debug APK must not use the permanent release key')
        target = output / f"Total-Music-{config['versionName']}-{kind}.apk"
        shutil.copy2(source, target)
        apks.append(dict(file=target.name, kind=kind, **info, certificate_sha256=fingerprint,
                         sha256=hashlib.file_digest(target.open('rb'), 'sha256').hexdigest()))
    suites = []
    for base in ('app/build/test-results', 'shared/build/test-results'):
        for report in (ROOT / base).rglob('TEST-*.xml'):
            root = ET.parse(report).getroot()
            suites.append(dict(report=str(report.relative_to(ROOT)), **{key: int(root.get(key, 0)) for key in ('tests', 'failures', 'errors', 'skipped')}))
    if not suites or any(s['failures'] or s['errors'] for s in suites):
        raise ValueError('Missing or failed regression tests')
    verification = dict(source_commit=commit, version=config['versionName'], artifacts=apks,
        expected_permanent_certificate_sha256=config['certificateSha256'], release_signature_verified=False,
        release_blocker='Original permanent keystore and previous permanent-key APK required for final signing',
        device_tested=False, test_suites=suites)
    (output / 'build-verification.json').write_text(json.dumps(verification, indent=2) + '\n')
    shutil.copy2(ROOT / 'total-music-release.properties', output)
    (output / 'source-commit.txt').write_text(commit + '\n')
    (output / 'SHA256SUMS.txt').write_text(''.join(f"{a['sha256']}  {a['file']}\n" for a in apks))
    (output / 'README.txt').write_text('TEST ONLY: debug APK cannot update the permanent-key installation. Do not uninstall the existing app.\nUnsigned release is NOT a final APK. Permanent release signature and update compatibility still need verification.\n')
    print(json.dumps(dict(source_commit=commit, artifacts=len(apks), release_signature_verified=False)))


if __name__ == '__main__':
    main()
