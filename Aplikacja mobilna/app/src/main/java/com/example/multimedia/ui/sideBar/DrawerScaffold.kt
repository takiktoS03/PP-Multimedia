package com.example.multimedia.ui.sideBar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.multimedia.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerScaffold(
    navController: NavController,
    currentRoute: String,
    title: String,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // 🔹 Tło obrazkowe
                    Image(
                        painter = painterResource(id = R.drawable.drawer_background), // <- Upewnij się że plik jest w drawable
                        contentDescription = "Tło paska bocznego",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // 🔹 Zawartość paska bocznego
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text("Menu", style = MaterialTheme.typography.titleLarge)

                        Spacer(modifier = Modifier.height(8.dp))

                        NavigationDrawerItem(
                            label = { Text("Home") },
                            selected = currentRoute == "home",
                            icon = { Icon(Icons.Default.Home, contentDescription = null) },
                            onClick = {
                                navController.navigate("home") {
                                    launchSingleTop = true
                                    popUpTo(0)
                                }
                                scope.launch { drawerState.close() }
                            }
                        )
                        NavigationDrawerItem(
                            label = { Text("Gallery") },
                            selected = currentRoute == "gallery",
                            icon = { Icon(painterResource(id = R.drawable.ic_menu_gallery), null) },
                            onClick = {
                                navController.navigate("gallery") {
                                    launchSingleTop = true
                                    popUpTo(0)
                                }
                                scope.launch { drawerState.close() }
                            }
                        )
                        NavigationDrawerItem(
                            label = { Text("Maps of Photos") },
                            selected = currentRoute == "map_with_photos",
                            icon = { Icon(painterResource(id = R.drawable.baseline_map_24), null) },
                            onClick = {
                                navController.navigate("map_with_photos") {
                                    launchSingleTop = true
                                    popUpTo(0)
                                }
                                scope.launch { drawerState.close() }
                            }
                        )
                        NavigationDrawerItem(
                            label = { Text("Konto") },
                            selected = currentRoute == "account",
                            icon = { Icon(Icons.Default.Person, null) },
                            onClick = {
                                navController.navigate("account") {
                                    launchSingleTop = true
                                    popUpTo(0)
                                }
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            content = content
        )
    }
}
