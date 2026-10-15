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
            LocalLyricsStore.importFile(context, track, Uri.fromFile(document))
            assertEquals("First local line", EmbeddedLyrics.forUri(context, track)!!.first { it.text.isNotBlank() }.text)
            document.delete()
            assertTrue(LocalLyricsStore.read(context, track)!!.contains("Second local line"))
            assertNull(LocalLyricsStore.read(context, track + "other"))
            assertEquals("original audio", music.readText())
            document.writeText("Plain lyrics\nAnother line", Charsets.UTF_16)
            LocalLyricsStore.importFile(context, track, Uri.fromFile(document))
            assertEquals("Plain lyrics\nAnother line", EmbeddedLyrics.forUri(context, track)!!.single().text)
        } finally { music.delete(); document.delete() }
    }
    @Test fun flacLyricsAfterLargeCoverAreReadWithoutLoadingCover() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val audio = File(context.cacheDir, "large-cover-${UUID.randomUUID()}.flac")
        try {
            fun little(n: Int) = byteArrayOf(n.toByte(), (n shr 8).toByte(), (n shr 16).toByte(), (n shr 24).toByte())
            val entry = "LYRICS=[00:01.00]After large cover".toByteArray()
            val comment = little(0) + little(1) + little(entry.size) + entry
            java.io.RandomAccessFile(audio, "rw").use {
                it.write("fLaC".toByteArray())
                it.write(byteArrayOf(6, 0x90.toByte(), 0, 0))
                it.seek(8L + 0x900000)
                it.write(byteArrayOf(0x84.toByte(), 0, 0, comment.size.toByte()))
                it.write(comment)
            }
            assertEquals("After large cover", EmbeddedLyrics.forUri(context, Uri.fromFile(audio).toString())!!.first { it.text.isNotBlank() }.text)
        } finally { audio.delete() }
    }

    @Test fun invalidImportKeepsPreviousLyrics() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val document = File(context.cacheDir, "lyrics-${UUID.randomUUID()}.txt")
        val track = "test:${UUID.randomUUID()}"
        try {
            document.writeText("Valid local text")
            LocalLyricsStore.importFile(context, track, Uri.fromFile(document))
            document.writeBytes(ByteArray(1024 * 1024 + 1) { 65 })
            assertTrue(runCatching { LocalLyricsStore.importFile(context, track, Uri.fromFile(document)) }.isFailure)
            assertEquals("Valid local text", LocalLyricsStore.read(context, track))
            document.writeBytes(byteArrayOf(0, 0, 1))
            assertTrue(runCatching { LocalLyricsStore.importFile(context, track, Uri.fromFile(document)) }.isFailure)
            assertEquals("Valid local text", LocalLyricsStore.read(context, track))
        } finally { document.delete() }
    }
}
