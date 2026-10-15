package com.music.bitchord.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.bitchord.R
import com.music.bitchord.ui.components.ReorderableSongList
import com.music.bitchord.data.DevicePlaylistStore
import com.music.bitchord.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Local-only management; playback uses the same queue and engine as the device library. */
@Composable
fun DevicePlaylistsTab(songs: List<Song>, onPlay: (List<Song>, Int) -> Unit, padding: PaddingValues) {
    val context = LocalContext.current.applicationContext
    val loaded = remember(context) { runCatching { DevicePlaylistStore.forDevice(context) } }
    val store = loaded.getOrNull()
    if (store == null) {
        Text(stringResource(R.string.star_save_error), Modifier.padding(20.dp))
        return
    }
    val playlists by store.playlists.collectAsStateWithLifecycle()
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val selected = playlists.firstOrNull { it.id == selectedId }
    var editing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var reordering by remember { mutableStateOf(false) }
    var adding by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf(false) }
    var checked by remember { mutableStateOf<Set<String>>(emptySet()) }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    fun change(action: () -> Unit) {
        if (busy) return
        busy = true
        scope.launch {
            try { withContext(Dispatchers.IO) { action() } }
            catch (e: Exception) { error = e.localizedMessage ?: "Could not save playlist" }
            finally { busy = false }
        }
    }
    BackHandler(selected != null) { selectedId = null }
    Column(Modifier.fillMaxSize().padding(bottom = padding.calculateBottomPadding())) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            if (selected != null) IconButton(onClick = { selectedId = null }) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, stringResource(R.string.back))
            }
            Text(selected?.name ?: stringResource(R.string.playlists), Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
            if (selected == null) IconButton(onClick = { name = ""; editing = true }, enabled = !busy) {
                Icon(Icons.Rounded.Add, stringResource(R.string.create_playlist))
            } else {
                IconButton(onClick = { checked = emptySet(); adding = true }, enabled = !busy) { Icon(Icons.Rounded.Add, stringResource(R.string.star_add_songs)) }
                IconButton(onClick = { reordering = true }, enabled = !busy && selected.songs.size > 1) { Icon(Icons.Rounded.DragHandle, stringResource(R.string.reorder_songs)) }
                IconButton(onClick = { name = selected.name; editing = true }, enabled = !busy) { Icon(Icons.Rounded.Edit, stringResource(R.string.rename)) }
                IconButton(onClick = { deleting = true }, enabled = !busy) { Icon(Icons.Rounded.Delete, stringResource(R.string.delete)) }
            }
        }
        if (busy) LinearProgressIndicator(Modifier.fillMaxWidth())
        if (selected == null) {
            if (playlists.isEmpty()) Text(stringResource(R.string.star_playlist_hint), Modifier.padding(20.dp))
            LazyColumn {
                items(playlists, key = { it.id }) { p ->
                    ListItem(headlineContent = { Text(p.name) }, supportingContent = { Text("${p.songs.size}") },
                        leadingContent = { Icon(Icons.Rounded.LibraryMusic, null) },
                        modifier = Modifier.clickable { selectedId = p.id })
                }
            }
        } else {
            Button(onClick = { onPlay(selected.songs.map { it.copy(videoId = it.localUri!!) }, 0) }, enabled = selected.songs.isNotEmpty(), modifier = Modifier.padding(horizontal = 16.dp)) {
                Icon(Icons.Rounded.PlayArrow, null); Text(stringResource(R.string.play))
            }
            if (selected.songs.isEmpty()) Text(stringResource(R.string.star_playlist_hint), Modifier.padding(20.dp))
            LazyColumn {
                itemsIndexed(selected.songs, key = { _, s -> s.localUri ?: s.videoId }) { index, song ->
                    ListItem(headlineContent = { Text(song.title, maxLines = 1) }, supportingContent = { Text(song.artist, maxLines = 1) },
                        modifier = Modifier.clickable { onPlay(selected.songs.map { it.copy(videoId = it.localUri!!) }, index) },
                        trailingContent = {
                            Row {
                                IconButton(onClick = { change { store.move(selected.id, index, index - 1) } }, enabled = !busy && index > 0) { Icon(Icons.Rounded.KeyboardArrowUp, stringResource(R.string.star_move_up)) }
                                IconButton(onClick = { change { store.move(selected.id, index, index + 1) } }, enabled = !busy && index < selected.songs.lastIndex) { Icon(Icons.Rounded.KeyboardArrowDown, stringResource(R.string.star_move_down)) }
                                IconButton(onClick = { change { store.remove(selected.id, index) } }, enabled = !busy) { Icon(Icons.Rounded.Close, stringResource(R.string.remove)) }
                            }
                        })
                }
            }
        }
    }
    if (reordering && selected != null) {
        val playlistId = selected.id
        val order = remember(playlistId) { mutableStateListOf<Song>().apply { addAll(selected.songs) } }
        androidx.compose.ui.window.Dialog(onDismissRequest = { reordering = false }) {
            Surface(shape = MaterialTheme.shapes.large) {
                Column(Modifier.fillMaxWidth().heightIn(max = 560.dp).padding(16.dp)) {
                    Text(stringResource(R.string.reorder_songs), style = MaterialTheme.typography.titleLarge)
                    Box(Modifier.weight(1f, fill = false)) { ReorderableSongList(order, enabled = !busy) }
                    Row(Modifier.align(Alignment.End)) {
                        TextButton(onClick = { reordering = false }) { Text(stringResource(R.string.cancel)) }
                        TextButton(enabled = !busy, onClick = {
                            val uris = order.map { requireNotNull(it.localUri) }
                            change { store.reorder(playlistId, uris) }
                            reordering = false
                        }) { Text(stringResource(R.string.save_order)) }
                    }
                }
            }
        }
    }
    if (editing) AlertDialog(onDismissRequest = { editing = false },
        title = { Text(stringResource(if (selected == null) R.string.create_playlist else R.string.rename)) },
        text = { OutlinedTextField(value = name, onValueChange = { name = it }, singleLine = true, label = { Text(stringResource(R.string.playlist_name)) }) },
        confirmButton = { TextButton(enabled = name.isNotBlank() && !busy, onClick = {
            val id = selected?.id
            val value = name
            change { if (id == null) store.create(value) else store.rename(id, value) }
            editing = false
        }) { Text(stringResource(R.string.save)) } },
        dismissButton = { TextButton(onClick = { editing = false }) { Text(stringResource(R.string.cancel)) } })
    if (deleting && selected != null) AlertDialog(onDismissRequest = { deleting = false },
        title = { Text(stringResource(R.string.delete)) }, text = { Text(stringResource(R.string.star_delete_playlist)) },
        confirmButton = { TextButton(onClick = { val id = selected.id; change { store.delete(id) }; selectedId = null; deleting = false }) { Text(stringResource(R.string.delete)) } },
        dismissButton = { TextButton(onClick = { deleting = false }) { Text(stringResource(R.string.cancel)) } })
    if (adding && selected != null) AlertDialog(onDismissRequest = { adding = false },
        title = { Text(stringResource(R.string.star_add_songs)) },
        text = {
            LazyColumn(Modifier.heightIn(max = 400.dp)) {
                items(songs.filter { it.localUri != null }.distinctBy { it.localUri }, key = { it.localUri!! }) { song ->
                    val uri = song.localUri!!
                    Row(Modifier.fillMaxWidth().clickable { checked = if (uri in checked) checked - uri else checked + uri }, verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = uri in checked, onCheckedChange = { checked = if (it) checked + uri else checked - uri })
                        Column { Text(song.title, maxLines = 1); Text(song.artist, maxLines = 1, style = MaterialTheme.typography.bodySmall) }
                    }
                }
            }
        },
        confirmButton = { TextButton(enabled = checked.isNotEmpty() && !busy, onClick = {
            val id = selected.id; val tracks = songs.filter { it.localUri in checked }
            change { store.add(id, tracks) }; adding = false
        }) { Text(stringResource(R.string.save)) } },
        dismissButton = { TextButton(onClick = { adding = false }) { Text(stringResource(R.string.cancel)) } })
    error?.let { message -> AlertDialog(onDismissRequest = { error = null }, title = { Text(stringResource(R.string.star_save_error)) }, text = { Text(message) }, confirmButton = { TextButton(onClick = { error = null }) { Text(stringResource(R.string.back)) } }) }
}
