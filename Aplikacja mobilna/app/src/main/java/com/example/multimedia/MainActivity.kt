    package com.example.multimedia

    import android.os.Bundle
    import androidx.activity.ComponentActivity
    import androidx.activity.compose.setContent
    import androidx.compose.foundation.layout.padding
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.Home
    import androidx.compose.material.icons.filled.Menu
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.res.stringResource
    import androidx.compose.ui.unit.dp
    import androidx.navigation.compose.*
    import androidx.navigation.compose.rememberNavController
    import androidx.navigation.findNavController
    import androidx.compose.material.icons.filled.Person
    import com.example.multimedia.ui.gallery.GalleryScreen
    import com.example.multimedia.ui.home.HomeViewModel
    import com.example.multimedia.ui.pages.AccountScreen
    import com.example.multimedia.ui.pages.HomeScreen
    import com.example.multimedia.ui.theme.MultimediaTheme
    import dagger.hilt.android.AndroidEntryPoint
    import kotlinx.coroutines.launch
    import com.google.firebase.auth.FirebaseAuth


    @AndroidEntryPoint
    @OptIn(ExperimentalMaterial3Api::class)
    class MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                MultimediaTheme {
                    val firebaseUser = FirebaseAuth.getInstance().currentUser

                    if (firebaseUser == null) {
                        com.example.multimedia.ui.auth.LoginScreen(
                            onLoginSuccess = { recreate() },
                            onContinueAsGuest = { recreate() }
                        )
                    } else {
                        val navController = rememberNavController()
                        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                        val scope = rememberCoroutineScope()
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route

                        ModalNavigationDrawer(
                            drawerState = drawerState,
                            drawerContent = {
                                ModalDrawerSheet {
                                    Text(
                                        "Menu",
                                        modifier = Modifier.padding(16.dp),
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    NavigationDrawerItem(
                                        label = { Text("Home") },
                                        selected = currentRoute == "home",
                                        icon = {
                                            Icon(
                                                Icons.Default.Home,
                                                contentDescription = null
                                            )
                                        },
                                        onClick = {
                                            navController.navigate("home") {
                                                launchSingleTop = true
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                            }
                                            scope.launch { drawerState.close() }
                                        }
                                    )

                                    NavigationDrawerItem(
                                        label = { Text("Gallery") },
                                        selected = currentRoute == "gallery",
                                        icon = {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_menu_gallery),
                                                contentDescription = null
                                            )
                                        },
                                        onClick = {
                                            navController.navigate("gallery") {
                                                launchSingleTop = true
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                            }
                                            scope.launch { drawerState.close() }
                                        }
                                    )

                                    NavigationDrawerItem(
                                        label = { Text("Konto") },
                                        selected = currentRoute == "account",
                                        icon = {
                                            Icon(
                                                Icons.Default.Person,
                                                contentDescription = null
                                            )
                                        },
                                        onClick = {
                                            navController.navigate("account") {
                                                launchSingleTop = true
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                            }
                                            scope.launch { drawerState.close() }
                                        }
                                    )
                                }
                            }
                        ) {
                            Scaffold(
                                topBar = {
                                    TopAppBar(
                                        title = { Text("Moja Aplikacja") },
                                        navigationIcon = {
                                            IconButton(onClick = {
                                                scope.launch { drawerState.open() }
                                            }) {
                                                Icon(
                                                    Icons.Default.Menu,
                                                    contentDescription = "Menu"
                                                )
                                            }
                                        }
                                    )
                                }
                            ) { innerPadding ->
                                NavHost(
                                    navController = navController,
                                    startDestination = "home",
                                    modifier = Modifier.padding(innerPadding)
                                ) {
                                    composable("home") {
                                        val viewModel = remember { HomeViewModel() }
                                        val title = viewModel.text.collectAsState()
                                        val isLoading = viewModel.isLoading.collectAsState()
                                        HomeScreen(title.value, isLoading.value)
                                    }

                                    composable("gallery") {
                                        GalleryScreen()
                                    }

                                    composable("account") {
                                        AccountScreen(onLogout = { recreate() })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
