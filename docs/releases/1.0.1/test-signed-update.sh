#!/usr/bin/env bash
set -euo pipefail
trap 'timeout 15s adb logcat -d > device-full-log.txt 2>&1 || true; timeout 15s adb logcat -d -b crash > device-crash-log.txt 2>&1 || true' EXIT
# adbd can close its transport while restarting as root; verify readiness.
for attempt in 1 2 3; do
  timeout 30s adb root || true
  timeout 30s adb wait-for-device
  if [ "$(timeout 15s adb shell id -u | tr -d '\r')" = 0 ]; then break; fi
done
test "$(timeout 15s adb shell id -u | tr -d '\r')" = 0
timeout 180s adb install -g --no-incremental previous/Space-Music-1.0.0-beta.apk
python3 - <<'PY_FIXTURE'
import base64
from pathlib import Path
encoded = Path('docs/releases/1.0.1/upgrade-tone.flac.b64').read_bytes()
for suffix in ['A', 'B']:
    Path('SpaceUpgradeTone' + suffix + '.flac').write_bytes(base64.b64decode(encoded))
PY_FIXTURE
adb shell mkdir -p /sdcard/Music
for suffix in A B; do
  adb push "SpaceUpgradeTone$suffix.flac" "/sdcard/Music/SpaceUpgradeTone$suffix.flac"
  adb shell am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d "file:///sdcard/Music/SpaceUpgradeTone$suffix.flac"
done
adb shell content query --uri content://media/external/audio/media --projection _id:title:duration:is_music > device-media-fixture.txt
python3 docs/releases/1.0.1/verify-upgrade-playlist.py seed
TEST_APP_APK=dist/Space-Music-1.0.1-beta.apk TEST_INSTRUMENTATION_APK=dist/test-runner.apk bash scripts/test-device.sh
python3 docs/releases/1.0.1/verify-upgrade-playlist.py verify
adb shell cmd uimode night yes || true
adb shell am start -W -n org.nexlibre.spacemusic/com.music.bitchord.MainActivity
sleep 3
adb exec-out screencap -p > device-dark.png
adb shell cmd uimode night no || true
adb shell am start -W -n org.nexlibre.spacemusic/com.music.bitchord.MainActivity
sleep 3
adb exec-out screencap -p > device-light.png
