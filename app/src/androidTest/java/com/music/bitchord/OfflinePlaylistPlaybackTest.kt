package com.music.bitchord

import android.net.Uri
import android.net.ConnectivityManager
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.Player
import androidx.test.platform.app.InstrumentationRegistry
import com.music.bitchord.data.DevicePlaylistStore
import com.music.bitchord.data.model.Song
import com.music.bitchord.playback.toMediaItem
import org.junit.Test
import org.junit.Assert.*
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class OfflinePlaylistPlaybackTest {
    @Test fun restoredLocalPlaylistDecodesWithoutNetwork() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        assertEquals(android.content.pm.PackageManager.PERMISSION_DENIED, context.checkSelfPermission(android.Manifest.permission.INTERNET))
        val audio = File(context.filesDir, "test-${UUID.randomUUID()}.wav")
        val filename = "test-${UUID.randomUUID()}.json"
        // One second of silent PCM audio: a real local container, no network source.
        val dataSize = 44100 * 2
        val buffer = ByteBuffer.allocate(44 + dataSize).order(ByteOrder.LITTLE_ENDIAN)
        buffer.put("RIFF".toByteArray()).putInt(36 + dataSize).put("WAVEfmt ".toByteArray())
            .putInt(16).putShort(1).putShort(1).putInt(44100).putInt(88200).putShort(2).putShort(16)
            .put("data".toByteArray()).putInt(dataSize)
        audio.writeBytes(buffer.array())
        var player: ExoPlayer? = null
        try {
            val store = DevicePlaylistStore(context, filename)
            store.create("Offline")
            store.add(store.playlists.value.single().id, listOf(Song(Uri.fromFile(audio).toString(), "Local audio", "Test", null,
                localUri = Uri.fromFile(audio).toString(), localPath = audio.absolutePath)))
            val restored = DevicePlaylistStore(context, filename).playlists.value.single().songs.single()
            val done = CountDownLatch(1)
            val error = AtomicReference<Throwable?>()
            instrumentation.runOnMainSync {
                val item = restored.toMediaItem()
                assertEquals("file", item.localConfiguration!!.uri.scheme)
                player = ExoPlayer.Builder(context).build().also { p ->
                    p.addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(state: Int) { if (state == Player.STATE_ENDED) done.countDown() }
                        override fun onPlayerError(e: androidx.media3.common.PlaybackException) { error.set(e); done.countDown() }
                    })
                    p.setMediaItem(item); p.prepare(); p.play()
                }
            }
            assertTrue("Local audio did not finish", done.await(20, TimeUnit.SECONDS))
            assertNull(error.get())
        } finally {
            instrumentation.runOnMainSync { player?.release() }
            audio.delete(); context.deleteFile(filename)
        }
    }
}
