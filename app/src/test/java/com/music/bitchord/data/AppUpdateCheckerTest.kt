package com.music.bitchord.data

import org.junit.Assert.*
import org.junit.Test

class AppUpdateCheckerTest {
    @Test fun stableSupersedesBetaAndNumericVersionsCompareCorrectly() {
        assertTrue(AppUpdateChecker.isNewer("2.1.0", "2.0.1-beta"))
        assertTrue(AppUpdateChecker.isNewer("2.1.0", "2.1.0-beta"))
        assertTrue(AppUpdateChecker.isNewer("2.10.0", "2.9.0"))
        assertFalse(AppUpdateChecker.isNewer("2.1.0", "2.1.0"))
        assertFalse(AppUpdateChecker.isNewer("2.0.1", "2.1.0"))
        assertFalse(AppUpdateChecker.isNewer("2.1.0-beta", "2.1.0"))
    }
    @Test fun noticesOnlyQuerySpaceMusic() {
        assertEquals("https://api.github.com/repos/nexlibre/Space-Music/releases/latest", AppUpdateChecker.LATEST_RELEASE_URL)
    }
}
