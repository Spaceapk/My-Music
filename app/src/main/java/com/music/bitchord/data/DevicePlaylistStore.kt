package com.music.bitchord.data

import android.content.Context
import android.util.AtomicFile
import com.music.bitchord.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

data class DevicePlaylist(val id: String, val name: String, val songs: List<Song>)

/** Star Music device-only playlists. Atomic writes preserve the previous file on failure. */
class DevicePlaylistStore(context: Context, filename: String = "star-device-playlists.json") {
    companion object {
        private var deviceStore: DevicePlaylistStore? = null
        /** One writer and one observable state across local-tab navigation. */
        fun forDevice(context: Context): DevicePlaylistStore = synchronized(this) {
            deviceStore ?: DevicePlaylistStore(context.applicationContext).also { deviceStore = it }
        }
    }
    private val file = AtomicFile(File(context.filesDir, filename))
    private val state = MutableStateFlow(read())
    val playlists = state.asStateFlow()

    private fun read(): List<DevicePlaylist> {
        if (!file.baseFile.exists()) return emptyList()
        val array = JSONArray(file.openRead().use { it.readBytes().toString(Charsets.UTF_8) })
        return (0 until array.length()).map { i ->
            val p = array.getJSONObject(i)
            val tracks = p.getJSONArray("songs")
            DevicePlaylist(p.getString("id"), p.getString("name"), (0 until tracks.length()).map { j ->
                val s = tracks.getJSONObject(j)
                fun optional(key: String) = if (s.isNull(key)) null else s.optString(key).takeIf { it.isNotBlank() }
                Song(videoId = s.getString("id"), title = s.getString("title"), artist = s.getString("artist"),
                    thumbnailUrl = optional("art"), durationText = optional("duration"), albumName = optional("album"),
                    localUri = s.getString("uri"), localPath = optional("path"), downloadFormat = optional("format"))
            })
        }
    }

    @Synchronized
    private fun update(transform: (List<DevicePlaylist>) -> List<DevicePlaylist>) {
        val next = transform(state.value)
        val array = JSONArray()
        next.forEach { p ->
            val tracks = JSONArray()
            p.songs.forEach { s ->
                tracks.put(JSONObject().put("id", s.videoId).put("title", s.title).put("artist", s.artist)
                    .put("art", s.thumbnailUrl).put("duration", s.durationText).put("album", s.albumName)
                    .put("uri", s.localUri).put("path", s.localPath).put("format", s.downloadFormat))
            }
            array.put(JSONObject().put("id", p.id).put("name", p.name).put("songs", tracks))
        }
        val output = file.startWrite()
        try {
            output.write(array.toString().toByteArray(Charsets.UTF_8))
            file.finishWrite(output)
        } catch (error: Throwable) {
            file.failWrite(output)
            throw error
        }
        state.value = next
    }

    fun create(name: String) {
        require(name.trim().isNotEmpty())
        update { it + DevicePlaylist(UUID.randomUUID().toString(), name.trim(), emptyList()) }
    }
    fun rename(id: String, name: String) {
        require(name.trim().isNotEmpty())
        update { list -> list.map { if (it.id == id) it.copy(name = name.trim()) else it } }
    }
    fun delete(id: String) = update { list -> list.filterNot { it.id == id } }
    fun add(id: String, songs: List<Song>) {
        require(songs.all { it.localUri?.let { uri -> uri.startsWith("content://") || uri.startsWith("file://") } == true })
        update { list -> list.map { if (it.id == id) it.copy(songs = (it.songs + songs).distinctBy { song -> song.localUri }) else it } }
    }
    fun remove(id: String, index: Int) = update { list -> list.map { p ->
        if (p.id == id) p.copy(songs = p.songs.filterIndexed { i, _ -> i != index }) else p
    } }
    fun move(id: String, from: Int, to: Int) = update { list -> list.map { p ->
        if (p.id == id && from in p.songs.indices && to in p.songs.indices) {
            val tracks = p.songs.toMutableList()
            tracks.add(to, tracks.removeAt(from))
            p.copy(songs = tracks)
        } else p
    } }
}
