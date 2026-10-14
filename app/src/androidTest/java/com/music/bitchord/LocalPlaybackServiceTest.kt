package com.music.bitchord

import android.content.ComponentName
import android.net.Uri
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.music.bitchord.data.model.Song
import com.music.bitchord.playback.PlaybackService
import com.music.bitchord.playback.playSongs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class LocalPlaybackServiceTest {
    @Test fun largeLocalQueueSurvivesPlaybackAndActivityRecreation() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val audio = File(context.filesDir, "service-${UUID.randomUUID()}.wav")
        val dataSize = 44100 * 2 * 30
        val buffer = ByteBuffer.allocate(44 + dataSize).order(ByteOrder.LITTLE_ENDIAN)
        buffer.put("RIFF".toByteArray()).putInt(36 + dataSize).put("WAVEfmt ".toByteArray())
            .putInt(16).putShort(1).putShort(1).putInt(44100).putInt(88200).putShort(2).putShort(16)
            .put("data".toByteArray()).putInt(dataSize)
        audio.writeBytes(buffer.array())
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        var controller: MediaController? = null
        try {
            var future: com.google.common.util.concurrent.ListenableFuture<MediaController>? = null
            instrumentation.runOnMainSync {
                future = MediaController.Builder(context,
                    SessionToken(context, ComponentName(context, PlaybackService::class.java))).buildAsync()
            }
            val player = future!!.get(20, TimeUnit.SECONDS)
            controller = player
            val started = CountDownLatch(1)
            val error = AtomicReference<PlaybackException?>()
            instrumentation.runOnMainSync {
                player.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) { if (isPlaying) started.countDown() }
                    override fun onPlayerError(e: PlaybackException) { error.set(e); started.countDown() }
                })
            }
            val songs = (0 until 731).map { index ->
                Song("local-service-$index", "Local $index", "Test", null,
                    localUri = Uri.fromFile(audio).toString(), localPath = audio.absolutePath)
            }
            runBlocking { withContext(Dispatchers.Main) { player.playSongs(songs, 365) } }
            assertTrue("Service did not start local audio", started.await(20, TimeUnit.SECONDS))
            assertNull("Local service playback failed", error.get())
            scenario.recreate()
            instrumentation.runOnMainSync {
                assertEquals(731, player.mediaItemCount)
                assertNotNull(player.currentMediaItem)
                player.pause()
                player.seekToNextMediaItem()
                player.play()
            }
            scenario.recreate()
            instrumentation.runOnMainSync {
                assertEquals(731, player.mediaItemCount)
                assertNull(player.playerError)
            }
        } finally {
            instrumentation.runOnMainSync { controller?.stop(); controller?.clearMediaItems(); controller?.release() }
            scenario.close()
            audio.delete()
        }
    }
}
