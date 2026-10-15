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
sdk=$(adb shell getprop ro.build.version.sdk | tr -d '\r')
if [ "$sdk" -ge 33 ]; then
  adb shell pm grant org.nexlibre.spacemusic android.permission.READ_MEDIA_AUDIO
else
  adb shell pm grant org.nexlibre.spacemusic android.permission.READ_EXTERNAL_STORAGE
fi
adb shell dumpsys package org.nexlibre.spacemusic > device-package-fixture.txt
python3 - <<'PY_FIXTURE'
import base64
from pathlib import Path
encoded = Path('docs/releases/1.0.1/upgrade-tone.flac.b64').read_bytes()
for suffix in ['A', 'B']:
    Path('SpaceUpgradeTone' + suffix + '.flac').write_bytes(base64.b64decode(encoded))
PY_FIXTURE
adb shell mkdir -p /storage/emulated/0/Music
for suffix in A B; do
  track="SpaceUpgradeTone$suffix"
  adb push "$track.flac" "/storage/emulated/0/Music/$track.flac"
  adb shell ls -l "/storage/emulated/0/Music/$track.flac"
  adb shell content insert --uri content://media/external/audio/media --bind "_data:s:/storage/emulated/0/Music/$track.flac" --bind "_display_name:s:$track.flac" --bind "title:s:$track" --bind mime_type:s:audio/flac --bind duration:l:31000 --bind is_music:i:1 --bind is_alarm:i:0 --bind is_notification:i:0 --bind is_ringtone:i:0 --bind artist:s:Test --bind album:s:SpaceUpgrade
 done
if [ "$sdk" -ge 29 ]; then
  adb shell content update --uri content://media/external/audio/media --bind is_podcast:i:0
fi
adb shell content query --uri content://media/external/audio/media --projection _id:title:duration:is_music:is_alarm:is_notification:is_ringtone:_data > device-media-fixture.txt
grep -q SpaceUpgradeToneA device-media-fixture.txt
grep -q SpaceUpgradeToneB device-media-fixture.txt
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
