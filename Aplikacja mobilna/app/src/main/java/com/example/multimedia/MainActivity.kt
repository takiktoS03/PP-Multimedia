package com.example.multimedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.multimedia.ui.gallery.GalleryScreen
import com.example.multimedia.ui.home.HomeViewModel
import com.example.multimedia.ui.login.LoginViewModel
import com.example.multimedia.ui.pages.AccountScreen
import com.example.multimedia.ui.pages.HomeScreen
import com.example.multimedia.ui.pages.LoginScreen
import com.example.multimedia.ui.theme.MultimediaTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MultimediaTheme {
                val firebaseUser = FirebaseAuth.getInstance().currentUser

                if (firebaseUser == null) {
                    val loginViewModel = remember { LoginViewModel() }
                    val state by loginViewModel.state.collectAsState()

                    LoginScreen(
                        state = state,
                        onEmailChange = loginViewModel::onEmailChange,
                        onPasswordChange = loginViewModel::onPasswordChange,
                        onConfirmPasswordChange = loginViewModel::onConfirmPasswordChange,
                        onNameChange = loginViewModel::onNameChange,
                        onToggleMode = loginViewModel::toggleMode,
                        onSubmit = {
                            if (state.isRegistering) {
                                lifecycleScope.launch {
                                    loginViewModel.register(
                                        onSuccess = { loginViewModel.resetForm()
                                            recreate() },
                                        onFailure = loginViewModel::setError
                                    )
                                }
                            } else {
                                lifecycleScope.launch {
                                    loginViewModel.login(
                                        onSuccess = { loginViewModel.resetForm()
                                            recreate() },
                                        onFailure = loginViewModel::setError
                                    )
                                }
                            }
                        },
                        onGuestLogin = {
                            lifecycleScope.launch {
                                loginViewModel.loginAsGuest(
                                    onSuccess = { loginViewModel.resetForm()
                                        recreate() },
                                    onFailure = loginViewModel::setError
                                )
                            }
                        }
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
                                    icon = { Icon(Icons.Default.Home, null) },
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
                                    icon = { Icon(Icons.Default.Person, null) },
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
                                    title = { Text("MultiMediaApp") },
                                    navigationIcon = {
                                        IconButton(onClick = {
                                            scope.launch { drawerState.open() }
                                        }) {
                                            Icon(Icons.Default.Menu, contentDescription = "Menu")
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
                                    val loginViewModel: LoginViewModel = viewModel()

                                    AccountScreen(onLogout = {
                                        loginViewModel.resetForm()
                                        recreate()
                                        FirebaseAuth.getInstance().signOut()
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
