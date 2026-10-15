package com.music.bitchord.data.lyrics

import android.content.Context
import android.net.Uri
import android.util.AtomicFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest

/** Private per-track imports: never rewrite or delete the original music file. */
object LocalLyricsStore {
    val revision = MutableStateFlow(0L)
    private const val MAX_BYTES = 1024 * 1024
    private fun file(context: Context, track: String): AtomicFile {
        val key = MessageDigest.getInstance("SHA-256").digest(track.toByteArray())
            .joinToString("") { "%02x".format(it) }
        val directory = File(context.filesDir, "local-lyrics").apply { mkdirs() }
        return AtomicFile(File(directory, "$key.lrc"))
    }
    @Synchronized
    fun read(context: Context, track: String): String? = runCatching {
        file(context, track).openRead().use { it.readBytes().toString(Charsets.UTF_8) }
    }.getOrNull()

    suspend fun importFile(context: Context, track: String, document: Uri) = withContext(Dispatchers.IO) {
        val bytes = requireNotNull(context.contentResolver.openInputStream(document)).use {
            val output = java.io.ByteArrayOutputStream()
            val buffer = ByteArray(8192)
            while (true) {
                val count = it.read(buffer)
                if (count < 0) break
                require(output.size() + count <= MAX_BYTES) { "Lyrics file exceeds 1 MB" }
                output.write(buffer, 0, count)
            }
            output.toByteArray()
        }
        val text = when {
            bytes.size >= 2 && bytes[0] == 0xff.toByte() && bytes[1] == 0xfe.toByte() -> bytes.toString(Charsets.UTF_16)
            bytes.size >= 2 && bytes[0] == 0xfe.toByte() && bytes[1] == 0xff.toByte() -> bytes.toString(Charsets.UTF_16)
            else -> bytes.toString(Charsets.UTF_8)
        }.trimStart('\uFEFF').trim()
        require(text.isNotEmpty() && '\u0000' !in text && '\uFFFD' !in text) { "Choose a UTF-8 or UTF-16 LRC/TXT file" }
        synchronized(LocalLyricsStore) {
            val target = file(context, track)
            val stream = target.startWrite()
            try { stream.write(text.toByteArray(Charsets.UTF_8)); target.finishWrite(stream) }
            catch (error: Throwable) { target.failWrite(stream); throw error }
            revision.value += 1
        }
    }
}
