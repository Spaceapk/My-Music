package com.music.bitchord

import com.music.bitchord.data.DevicePlaylist
import com.music.bitchord.data.PlaylistSort
import com.music.bitchord.data.model.Song
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class PlaylistSortTest {
    @Test fun titleArtistRecentAndManualOrdersAreIndependent() {
        val original = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("es"))
            val z = Song("z", "Zulu", "Alpha", null, localUri = "file:///z")
            val a = Song("a", "Álbum", "Zulu", null, localUri = "file:///a")
            val b = Song("b", "beta", "Alpha", null, localUri = "file:///b")
            val playlist = DevicePlaylist("id", "Test", listOf(z, a, b))
            fun ids(sort: PlaylistSort) = playlist.copy(sort = sort).sortedSongs().map { it.videoId }
            assertEquals(listOf("a", "b", "z"), ids(PlaylistSort.TITLE_ASC))
            assertEquals(listOf("z", "b", "a"), ids(PlaylistSort.TITLE_DESC))
            assertEquals(listOf("b", "z", "a"), ids(PlaylistSort.ARTIST))
            assertEquals(listOf("b", "a", "z"), ids(PlaylistSort.RECENTLY_ADDED))
            assertEquals(listOf("z", "a", "b"), ids(PlaylistSort.MANUAL))
            assertEquals(listOf(z, a, b), playlist.songs)
        } finally { Locale.setDefault(original) }
    }
}
