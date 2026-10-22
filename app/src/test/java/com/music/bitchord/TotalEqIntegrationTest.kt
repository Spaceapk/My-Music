package com.music.bitchord

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import com.music.bitchord.playback.EqualizerProcessor
import com.music.bitchord.playback.TotalEqConfig
import com.music.bitchord.playback.TotalFilter
import com.music.bitchord.playback.audio.AudioBlock
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.log10
import kotlin.math.roundToInt
import kotlin.math.sin
import org.junit.Assert.assertEquals
import org.junit.Test

/** Verify the actual two player-facing adapters, rather than only the DSP model. */
class TotalEqIntegrationTest {
    private val boost = TotalEqConfig(filters = listOf(TotalFilter(gain = 6f)),
        autoHeadroom = false, limiter = false)

    @Test fun totalEqChangesTheFloatSinkSamples() {
        val processor = EqualizerProcessor()
        processor.setTotalConfig(boost)
        processor.configure(48000, 2)
        val block = AudioBlock(channelCount = 2, capacityFrames = 256)
        block.setFrameCount(256)
        var inputEnergy = 0.0; var outputEnergy = 0.0
        repeat(128) { n ->
            for (i in 0 until 256) {
                val v = (0.01 * sin(2 * PI * 1000 * (n * 256 + i) / 48000)).toFloat()
                block.samples[i * 2] = v; block.samples[i * 2 + 1] = v
                if (n > 64) inputEnergy += 2 * v.toDouble() * v
            }
            processor.process(block)
            if (n > 64) for (v in block.samples) outputEnergy += v.toDouble() * v
        }
        assertEquals(6.0, 10 * log10(outputEnergy / inputEnergy), 0.03)
    }

    @Suppress("DEPRECATION")
    @Test fun totalEqChangesTheMedia3Pcm16Samples() {
        val processor = EqualizerProcessor()
        processor.setTotalConfig(boost)
        processor.configure(AudioProcessor.AudioFormat(48000, 1, C.ENCODING_PCM_16BIT))
        processor.flush()
        var inputEnergy = 0.0; var outputEnergy = 0.0
        repeat(128) { n ->
            val input = ByteBuffer.allocateDirect(512).order(ByteOrder.nativeOrder())
            for (i in 0 until 256) {
                val v = (1000 * sin(2 * PI * 1000 * (n * 256 + i) / 48000)).roundToInt().toShort()
                input.putShort(v)
                if (n > 64) inputEnergy += v.toDouble() * v
            }
            input.flip()
            processor.queueInput(input)
            val output = processor.output.order(ByteOrder.nativeOrder())
            while (output.hasRemaining()) {
                val v = output.short
                if (n > 64) outputEnergy += v.toDouble() * v
            }
        }
        assertEquals(6.0, 10 * log10(outputEnergy / inputEnergy), 0.03)
    }
}
