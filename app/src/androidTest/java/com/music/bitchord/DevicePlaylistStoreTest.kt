package com.music.bitchord

import androidx.test.platform.app.InstrumentationRegistry
import com.music.bitchord.data.DevicePlaylistStore
import com.music.bitchord.data.model.Song
import org.junit.Assert.*
import org.junit.Test
import java.util.UUID

class DevicePlaylistStoreTest {
    @Test fun editsSurviveReloadAndKeepOfflineLocations() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val filename = "test-${UUID.randomUUID()}.json"
        try {
            val store = DevicePlaylistStore(context, filename)
            val a = Song("local:1", "First", "Artist", null, localUri = "content://media/external/audio/media/1", localPath = "/storage/emulated/0/Music/a.flac")
            val b = Song("local:2", "Second", "Artist", null, localUri = "file:///storage/emulated/0/Music/b.flac")
            store.createWithSongs("  My files  ", listOf(a, b, a))
            val id = store.playlists.value.single().id
            store.add(id, listOf(a, b, a))
            assertEquals(2, store.playlists.value.single().songs.size)
            store.reorder(id, listOf(b.localUri!!, a.localUri!!))
            assertTrue(runCatching { store.reorder(id, listOf(a.localUri!!, a.localUri!!)) }.isFailure)
            assertTrue(runCatching { store.reorder(id, listOf(a.localUri!!)) }.isFailure)
            store.rename(id, "Offline")
            val reloaded = DevicePlaylistStore(context, filename)
            assertEquals("Offline", reloaded.playlists.value.single().name)
            assertEquals(listOf(b.localUri, a.localUri), reloaded.playlists.value.single().songs.map { it.localUri })
            assertEquals(a.localPath, reloaded.playlists.value.single().songs[1].localPath)
            reloaded.remove(id, 0)
            assertEquals(a.localUri, DevicePlaylistStore(context, filename).playlists.value.single().songs.single().localUri)
            reloaded.move(id, -1, 20)
            assertEquals(1, reloaded.playlists.value.single().songs.size)
            reloaded.delete(id)
            assertTrue(DevicePlaylistStore(context, filename).playlists.value.isEmpty())
        } finally { context.deleteFile(filename) }
    }
    @Test fun interruptedWriteRecoversBackupWithoutDeletingOriginalAudio() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val filename = "test-${UUID.randomUUID()}.json"
        val audio = java.io.File(context.filesDir, "original-${UUID.randomUUID()}.flac")
        val base = java.io.File(context.filesDir, filename)
        val backup = java.io.File(context.filesDir, "$filename.bak")
        try {
            audio.writeText("original bytes must survive playlist edits")
            val song = Song("local-backup", "Original", "Test", null,
                localUri = android.net.Uri.fromFile(audio).toString(), localPath = audio.absolutePath)
            val store = DevicePlaylistStore(context, filename)
            store.createWithSongs("Recoverable", listOf(song))
            val id = store.playlists.value.single().id
            assertTrue(base.renameTo(backup))
            val recovered = DevicePlaylistStore(context, filename)
            assertEquals(song.localUri, recovered.playlists.value.single().songs.single().localUri)
            recovered.remove(id, 0)
            recovered.delete(id)
            assertEquals("original bytes must survive playlist edits", audio.readText())
        } finally {
            context.deleteFile(filename); backup.delete(); audio.delete()
        }
    }
    @Test fun onlineTracksAndBlankNamesAreRejected() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val filename = "test-${UUID.randomUUID()}.json"
        try {
            val store = DevicePlaylistStore(context, filename)
            assertTrue(runCatching { store.create(" ") }.isFailure)
            assertTrue(runCatching { store.createWithSongs("Remote", listOf(Song("online", "Online", "Artist", null))) }.isFailure)
            assertTrue(store.playlists.value.isEmpty())
            store.create("Local")
            val id = store.playlists.value.single().id
            assertTrue(runCatching { store.add(id, listOf(Song("online", "Online", "Artist", null))) }.isFailure)
            assertTrue(DevicePlaylistStore(context, filename).playlists.value.single().songs.isEmpty())
        } finally { context.deleteFile(filename) }
    }
}
