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
            store.create("  My files  ")
            val id = store.playlists.value.single().id
            val a = Song("local:1", "First", "Artist", null, localUri = "content://media/external/audio/media/1", localPath = "/storage/emulated/0/Music/a.flac")
            val b = Song("local:2", "Second", "Artist", null, localUri = "file:///storage/emulated/0/Music/b.flac")
            store.add(id, listOf(a, b, a))
            assertEquals(2, store.playlists.value.single().songs.size)
            store.move(id, 1, 0)
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
    @Test fun onlineTracksAndBlankNamesAreRejected() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val filename = "test-${UUID.randomUUID()}.json"
        try {
            val store = DevicePlaylistStore(context, filename)
            assertTrue(runCatching { store.create(" ") }.isFailure)
            store.create("Local")
            val id = store.playlists.value.single().id
            assertTrue(runCatching { store.add(id, listOf(Song("online", "Online", "Artist", null))) }.isFailure)
            assertTrue(DevicePlaylistStore(context, filename).playlists.value.single().songs.isEmpty())
        } finally { context.deleteFile(filename) }
    }
}
