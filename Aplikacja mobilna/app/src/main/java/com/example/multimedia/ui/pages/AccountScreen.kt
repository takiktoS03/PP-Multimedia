package com.example.multimedia.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.multimedia.R
import com.example.multimedia.ui.sideBar.DrawerScaffold
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    navController: NavHostController,
    onLogout: () -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    val isAnonymous = user?.isAnonymous ?: true
    val email = user?.email ?: "Użytkownik gość"

    DrawerScaffold(
        navController = navController,
        currentRoute = "account",
        title = "Konto"
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background image
            Image(
                painter = painterResource(id = R.drawable.zxc),
                contentDescription = "Tło ekranu konta",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text("Konto", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Zalogowano jako:")
                Text(text = email, style = MaterialTheme.typography.bodyLarge)

                if (isAnonymous) {
                    Text("Tryb gościa", color = MaterialTheme.colorScheme.secondary)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = {
                    FirebaseAuth.getInstance().signOut()
                    onLogout()
                }) {
                    Text("Wyloguj się")
                }
            }
        }
    }
}
