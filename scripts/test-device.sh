#!/usr/bin/env bash
set -euo pipefail
apk=${TEST_APP_APK:-$(find built -name '*prod*x86_64*debug.apk' -o -name '*prod*universal*debug.apk' | head -1)}
if [ -z "$apk" ]; then apk=$(find built -path '*/prod/debug/*' -name '*universal*.apk' | head -1); fi
test_apk=${TEST_INSTRUMENTATION_APK:-$(find built -name '*androidTest.apk' | head -1)}
test -n "$apk"
test -n "$test_apk"
timeout 180s adb install "$apk"
timeout 180s adb install "$test_apk"
adb shell svc wifi disable
adb shell svc data disable
timeout 120s adb shell am instrument -w -r -e class com.music.bitchord.DevicePlaylistStoreTest,com.music.bitchord.OfflinePlaylistPlaybackTest,com.music.bitchord.ExampleInstrumentedTest org.nexlibre.starmusic.test/androidx.test.runner.AndroidJUnitRunner | tee device-tests.txt
grep -q 'OK (' device-tests.txt
! grep -q 'FAILURES\|INSTRUMENTATION_FAILED\|shortMsg=' device-tests.txt
timeout 15s adb shell svc wifi enable
timeout 15s adb shell svc data enable
timeout 15s adb logcat -c
timeout 60s adb shell am start -W -n org.nexlibre.starmusic/com.music.bitchord.MainActivity
sleep 8
timeout 15s adb logcat -d -b crash > device-crash-log.txt
! grep -q 'FATAL EXCEPTION' device-crash-log.txt
timeout 15s adb shell pidof org.nexlibre.starmusic
