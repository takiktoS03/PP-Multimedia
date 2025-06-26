package com.example.multimedia.ui.album

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.multimedia.ui.sideBar.DrawerScaffold
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AlbumListScreen(
    vm: AlbumViewModel = hiltViewModel(),
    navController: NavController
) {
    val albums by vm.albums.observeAsState(emptyList())

    val selectedIds = remember { mutableStateListOf<String>() }
    var selectionMode by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Dialog potwierdzenia usunięcia
    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Usuń album(y)") },
            text = { Text("Na pewno usunąć ${selectedIds.size} album(y)?") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirm = false
                    vm.deleteAlbums(
                        selectedIds.toList(),
                        onComplete = {
                            selectedIds.clear()
                            selectionMode = false
                            scope.launch {
                                snackbarHostState.showSnackbar("Usunięto album(y)")
                            }
                        },
                        onError = { e ->
                            scope.launch {
                                snackbarHostState.showSnackbar("Błąd: ${e.localizedMessage}")
                            }
                        }
                    )
                }) {
                    Text("Tak")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) {
                    Text("Anuluj")
                }
            }
        )
    }

    DrawerScaffold(
        navController     = navController,
        currentRoute      = "albums",
        title             = if (selectionMode) "${selectedIds.size} zaznaczone" else "Twoje albumy",
        snackbarHostState = snackbarHostState,
        actions = {
            if (selectionMode) {
                IconButton(onClick = { showConfirm = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Usuń")
                }
            } else {
                IconButton(onClick = { navController.navigate("create_album") }) {
                    Icon(Icons.Default.Add, contentDescription = "Nowy album")
                }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(albums, key = { it.id }) { album ->
                val isSelected by remember { derivedStateOf { album.id in selectedIds } }

                ListItem(
                    headlineContent   = { Text(album.name) },
                    supportingContent = { Text(album.description) },
                    trailingContent   = {
                        if (isSelected) {
                            Icon(
                                imageVector        = Icons.Default.Check,
                                contentDescription = "Zaznaczono",
                                tint               = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {
                                if (selectionMode) {
                                    // toggle zaznaczenie
                                    if (isSelected) selectedIds.remove(album.id)
                                    else               selectedIds.add(album.id)
                                    if (selectedIds.isEmpty()) selectionMode = false
                                } else {
                                    // normalna nawigacja do szczegółów albumu
                                    navController.navigate("gallery?albumId=${album.id}")
                                }
                            },
                            onLongClick = {
                                if (!selectionMode) {
                                    selectionMode = true
                                    selectedIds.add(album.id)
                                }
                            }
                        )
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}
