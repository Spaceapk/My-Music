package com.music.bitchord

import android.content.SharedPreferences
import com.music.bitchord.data.settings.TotalMusicSettings
import com.music.bitchord.playback.*
import java.lang.reflect.Proxy
import org.junit.Assert.*
import org.junit.Test

class TotalMusicSettingsTest {
    private fun preferences(data: MutableMap<String, Any> = mutableMapOf()): SharedPreferences {
        val editor = Proxy.newProxyInstance(SharedPreferences.Editor::class.java.classLoader,
            arrayOf(SharedPreferences.Editor::class.java)) { proxy, method, args ->
            when {
                method.name.startsWith("put") -> { data[args!![0] as String] = args[1]; proxy }
                method.name == "apply" -> null
                method.name == "commit" -> true
                method.name == "clear" -> { data.clear(); proxy }
                method.name == "remove" -> { data.remove(args!![0]); proxy }
                else -> null
            }
        }
        return Proxy.newProxyInstance(SharedPreferences::class.java.classLoader,
            arrayOf(SharedPreferences::class.java)) { _, method, args ->
            when (method.name) {
                "edit" -> editor
                "getAll" -> data.toMap()
                "contains" -> data.containsKey(args!![0])
                else -> if (method.name.startsWith("get")) data[args!![0]] ?: args[1] else null
            }
        } as SharedPreferences
    }

    @Test fun themeAndAdvancedAudioSurviveReload() {
        val prefs = preferences(); TotalMusicSettings.init(prefs)
        val config = TotalEqConfig(bands = List(31) { it / 3f - 5f }, preamp = -4f, bass = 3f,
            filters = listOf(TotalFilter(TotalFilterType.HIGH_PASS, 55f, 0f, 0.8f)), width = 1.2f, crossfeed = 0.2f)
        TotalMusicSettings.update(config); TotalMusicSettings.setExpressive(true)
        TotalMusicSettings.init(prefs)
        assertEquals(config, TotalMusicSettings.config.value)
        assertTrue(TotalMusicSettings.expressive.value)
    }

    @Test fun namedProfilesCanBeCreatedReplacedAndDeleted() {
        val prefs = preferences(); TotalMusicSettings.init(prefs)
        TotalMusicSettings.update(TotalEqConfig(bass = 4f)); TotalMusicSettings.saveProfile("Headphones")
        TotalMusicSettings.update(TotalEqConfig(bass = 8f)); TotalMusicSettings.saveProfile("Headphones")
        TotalMusicSettings.init(prefs)
        assertEquals(1, TotalMusicSettings.profiles.value.size)
        assertEquals(8f, TotalMusicSettings.profiles.value.getValue("Headphones").bass, 0f)
        TotalMusicSettings.deleteProfile("Headphones"); TotalMusicSettings.init(prefs)
        assertTrue(TotalMusicSettings.profiles.value.isEmpty())
    }

    @Test fun brokenImportedJsonFallsBackWithoutLosingLegacyPreferences() {
        val data = mutableMapOf<String, Any>("total_eq" to "broken", "total_eq_profiles" to "[", "equalizer_bands" to "1,2,3")
        TotalMusicSettings.init(preferences(data))
        assertEquals(TotalEqConfig(), TotalMusicSettings.config.value)
        assertTrue(TotalMusicSettings.profiles.value.isEmpty())
        assertEquals("1,2,3", data["equalizer_bands"])
    }

    @Test fun factoryPresetsHave31FiniteBoundedBands() {
        assertEquals(11, TotalMusicSettings.factoryProfiles.size)
        TotalMusicSettings.factoryProfiles.values.forEach { c ->
            assertEquals(31, c.bands.size)
            assertTrue(c.bands.all { it.isFinite() && it in -12f..12f })
        }
    }
}
