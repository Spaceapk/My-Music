package com.music.bitchord

import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import com.music.bitchord.data.lyrics.EmbeddedLyrics
import com.music.bitchord.data.lyrics.LocalLyricsStore
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.util.UUID

class LocalLyricsStoreTest {
    @Test fun importsPersistPerTrackWithoutChangingAudio() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val music = File(context.cacheDir, "lyrics-${UUID.randomUUID()}.flac").apply { writeText("original audio") }
        val document = File(context.cacheDir, "lyrics-${UUID.randomUUID()}.lrc")
        try {
            document.writeText("[00:01.00]First local line\n[00:04.00]Second local line")
            val track = Uri.fromFile(music).toString()
            LocalLyricsStore.import(context, track, Uri.fromFile(document))
            assertEquals("First local line", EmbeddedLyrics.forUri(context, track)!!.first { it.text.isNotBlank() }.text)
            document.delete()
            assertTrue(LocalLyricsStore.read(context, track)!!.contains("Second local line"))
            assertNull(LocalLyricsStore.read(context, track + "other"))
            assertEquals("original audio", music.readText())
            document.writeText("Plain lyrics\nAnother line", Charsets.UTF_16)
            LocalLyricsStore.import(context, track, Uri.fromFile(document))
            assertEquals("Plain lyrics\nAnother line", EmbeddedLyrics.forUri(context, track)!!.single().text)
        } finally { music.delete(); document.delete() }
    }
    @Test fun invalidImportKeepsPreviousLyrics() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val document = File(context.cacheDir, "lyrics-${UUID.randomUUID()}.txt")
        val track = "test:${UUID.randomUUID()}"
        try {
            document.writeText("Valid local text")
            LocalLyricsStore.import(context, track, Uri.fromFile(document))
            document.writeBytes(ByteArray(1024 * 1024 + 1) { 65 })
            assertTrue(runCatching { LocalLyricsStore.import(context, track, Uri.fromFile(document)) }.isFailure)
            assertEquals("Valid local text", LocalLyricsStore.read(context, track))
            document.writeBytes(byteArrayOf(0, 0, 1))
            assertTrue(runCatching { LocalLyricsStore.import(context, track, Uri.fromFile(document)) }.isFailure)
            assertEquals("Valid local text", LocalLyricsStore.read(context, track))
        } finally { document.delete() }
    }
}
