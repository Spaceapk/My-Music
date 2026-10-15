#!/usr/bin/env bash
set -euo pipefail
timeout 30s adb root
timeout 30s adb wait-for-device
timeout 180s adb install -g --no-incremental previous/Space-Music-1.0.0-beta.apk
python3 - <<'PY_FIXTURE'
import wave
for suffix in ['A', 'B']:
    with wave.open('SpaceUpgradeTone' + suffix + '.wav', 'wb') as audio:
        audio.setnchannels(1); audio.setsampwidth(2); audio.setframerate(44100)
        audio.writeframes(b'\x00\x00' * 44100)
PY_FIXTURE
adb shell mkdir -p /sdcard/Music
for suffix in A B; do
  adb push "SpaceUpgradeTone$suffix.wav" "/sdcard/Music/SpaceUpgradeTone$suffix.wav"
  adb shell am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d "file:///sdcard/Music/SpaceUpgradeTone$suffix.wav"
done
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
