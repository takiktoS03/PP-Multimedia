package com.example.multimedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.multimedia.ui.gallery.GalleryScreen
import com.example.multimedia.ui.gallery.LocationPickerScreen
import com.example.multimedia.ui.gallery.MapWithPhotosScreen
import com.example.multimedia.ui.home.HomeViewModel
import com.example.multimedia.ui.login.LoginViewModel
import com.example.multimedia.ui.pages.AccountScreen
import com.example.multimedia.ui.pages.HomeScreen
import com.example.multimedia.ui.pages.LoginScreen
import com.example.multimedia.ui.pages.ResetPasswordScreen
import com.example.multimedia.ui.pages.VerificationScreen
import com.example.multimedia.ui.theme.MultimediaTheme
import com.example.multimedia.ui.veryfication.VerificationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.delay

@AndroidEntryPoint
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MultimediaTheme {
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                var isVerified by remember { mutableStateOf<Boolean?>(null) }
                val verificationViewModel: VerificationViewModel = viewModel()
                val successTrigger by verificationViewModel.trigger.collectAsState()

                LaunchedEffect(successTrigger) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        try {
                            delay(500) // DODAJ 500ms opóźnienia
                            val snapshot = FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(user.uid)
                                .get()
                                .await()

                            val verified = snapshot.getBoolean("isVerified") ?: false
                            isVerified = if (snapshot.exists()) verified else false

                            if (!snapshot.exists()) {
                                FirebaseAuth.getInstance().signOut()
                                isVerified = false
                                return@LaunchedEffect
                            }

                        } catch (e: Exception) {
                            FirebaseAuth.getInstance().signOut()
                            isVerified = false
                        }
                    } else {
                        isVerified = false
                    }
                }

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                if (isVerified == null) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    // KLUCZOWA ZMIANA: NavHost opakowany w key(isVerified) – teraz odświeży się po zmianie
                    key(isVerified) {
                        NavHost(
                            navController = navController,
                            startDestination = when {
                                FirebaseAuth.getInstance().currentUser == null -> "login"
                                isVerified == false -> "verify/${FirebaseAuth.getInstance().currentUser?.uid}"
                                else -> "home"
                            }
                        ) {
                            composable("login") {
                                val loginViewModel = remember { LoginViewModel() }
                                val state by loginViewModel.state.collectAsState()

                                LoginScreen(
                                    state = state,
                                    navController = navController,
                                    onEmailChange = loginViewModel::onEmailChange,
                                    onPasswordChange = loginViewModel::onPasswordChange,
                                    onConfirmPasswordChange = loginViewModel::onConfirmPasswordChange,
                                    onNameChange = loginViewModel::onNameChange,
                                    onToggleMode = loginViewModel::toggleMode,
                                    onSubmit = {
                                        if (state.isRegistering) {
                                            lifecycleScope.launch {
                                                loginViewModel.register(
                                                    navController = navController,
                                                    onSuccess = { loginViewModel.resetForm() },
                                                    onFailure = loginViewModel::setError
                                                )
                                            }
                                        } else {
                                            lifecycleScope.launch {
                                                loginViewModel.login(
                                                    onSuccess = {
                                                        loginViewModel.resetForm()
                                                        navController.navigate("home") {
                                                            popUpTo("login") { inclusive = true }
                                                        }
                                                    },
                                                    onFailure = loginViewModel::setError
                                                )
                                            }
                                        }
                                    },
                                    onGuestLogin = {
                                        lifecycleScope.launch {
                                            loginViewModel.loginAsGuest(
                                                onSuccess = {
                                                    loginViewModel.resetForm()
                                                    navController.navigate("home") {
                                                        popUpTo("login") { inclusive = true }
                                                    }
                                                },
                                                onFailure = loginViewModel::setError
                                            )
                                        }
                                    }
                                )
                            }

                            composable(
                                "verify/{userId}",
                                arguments = listOf(navArgument("userId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                                VerificationScreen(navController, userId)
                            }

                            composable("home") {
                                HomeWithDrawer(navController, drawerState, scope, currentRoute)
                            }

                            composable("gallery") {
                                GalleryScreen(navController = navController)
                            }


                            composable("account") {
                                HomeWithDrawer(navController, drawerState, scope, currentRoute) {
                                    val loginViewModel: LoginViewModel = viewModel()
                                    AccountScreen(onLogout = {
                                        loginViewModel.resetForm()
                                        FirebaseAuth.getInstance().signOut()
                                        navController.navigate("login") {
                                            popUpTo("home") { inclusive = true }
                                        }
                                    })
                                }
                            }

                            composable("location_picker") {
                                LocationPickerScreen(
                                    onLocationPicked = { latLng ->
                                        // Wróć do dialogu z tą lokalizacją
                                        navController.previousBackStackEntry
                                            ?.savedStateHandle
                                            ?.set("picked_location", latLng)
                                        navController.popBackStack()
                                    },
                                    onCancel = { navController.popBackStack() }
                                )
                            }
                            composable("reset_password") {
                                ResetPasswordScreen(navController)
                            }
                            composable("map_with_photos") {
                                MapWithPhotosScreen(
                                    onBack = { navController.popBackStack() }
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeWithDrawer(
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    currentRoute: String?,
    content: @Composable () -> Unit = {
        val viewModel = remember { HomeViewModel() }
        val title = viewModel.text.collectAsState()
        val isLoading = viewModel.isLoading.collectAsState()
        HomeScreen(title.value, isLoading.value)
    }
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menu", modifier = Modifier.padding(16.dp))
                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = currentRoute == "home",
                    icon = { Icon(Icons.Default.Home, null) },
                    onClick = {
                        navController.navigate("home") {
                            launchSingleTop = true
                            popUpTo("home") { inclusive = true }
                        }
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Gallery") },
                    selected = currentRoute == "gallery",
                    icon = {
                        Icon(painter = painterResource(id = R.drawable.ic_menu_gallery), contentDescription = null)
                    },
                    onClick = {
                        navController.navigate("gallery") {
                            launchSingleTop = true
                            popUpTo("home") { inclusive = false }
                        }
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Maps of Photos") },
                    selected = currentRoute == "map_with_photos",
                    icon = {
                        Icon(painter = painterResource(id = R.drawable.ic_menu_gallery), contentDescription = null)
                    },
                    onClick = {
                        navController.navigate("map_with_photos") {
                            launchSingleTop = true
                            popUpTo("home") { inclusive = false }
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
                            popUpTo("home") { inclusive = false }
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
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                content()
            }
        }
    }
}
