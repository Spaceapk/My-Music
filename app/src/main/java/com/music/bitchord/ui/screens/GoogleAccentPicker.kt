package com.music.bitchord.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.bitchord.R
import com.music.bitchord.data.settings.TotalMusicSettings

@Composable
internal fun GoogleAccentPicker() {
    val saved by TotalMusicSettings.accent.collectAsStateWithLifecycle()
    var showPicker by remember { mutableStateOf(false) }
    var draft by remember { mutableStateOf(saved) }
    SettingsRow(icon = androidx.compose.material.icons.Icons.Rounded.Palette,
        title = stringResource(R.string.total_accent),
        trailing = { Box(Modifier.size(28.dp).background(Color(saved), CircleShape)) },
        onClick = { draft = saved; showPicker = true })
    if (showPicker) AlertDialog(onDismissRequest = { showPicker = false },
        title = { Text(stringResource(R.string.total_accent)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.fillMaxWidth().height(64.dp).background(Color(draft), MaterialTheme.shapes.large))
                Text("#%06X".format(draft and 0xFFFFFF))
                listOf("R" to 16, "G" to 8, "B" to 0).forEach { (label, shift) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(label, Modifier.width(24.dp))
                        Slider(value = ((draft ushr shift) and 255).toFloat(), valueRange = 0f..255f,
                            onValueChange = { value -> draft = (draft and (255 shl shift).inv()) or (value.toInt() shl shift) },
                            modifier = Modifier.weight(1f))
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { TotalMusicSettings.setAccent(draft); showPicker = false }) { Text(stringResource(android.R.string.ok)) } },
        dismissButton = { TextButton(onClick = { showPicker = false }) { Text(stringResource(android.R.string.cancel)) } })
}
