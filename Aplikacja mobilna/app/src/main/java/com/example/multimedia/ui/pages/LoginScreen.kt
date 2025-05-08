package com.example.multimedia.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.multimedia.R
import com.example.multimedia.ui.login.LoginState

@Composable
fun LoginScreen(
    state: LoginState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onToggleMode: () -> Unit,
    onSubmit: () -> Unit,
    onGuestLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Tło
        Image(
            painter = painterResource(id = R.drawable.login_registartion),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.3f)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.1f))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (state.isRegistering) "Rejestracja" else "Logowanie",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (state.isRegistering) {
            StyledTextField(
                value = state.name,
                onValueChange = onNameChange,
                placeholder = "Wpisz imię",
                label = "Imię",
                isError = state.nameError != null
            )
            state.nameError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(start = 8.dp, top = 4.dp)
                        .fillMaxWidth()
                )
            }
            Spacer(Modifier.height(12.dp))
        }

        StyledTextField(
            value = state.email,
            onValueChange = onEmailChange,
            placeholder = "Wpisz e-mail",
            label = "Adress e-mail",
            isError = state.emailError != null
        )
        state.emailError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(start = 8.dp, top = 4.dp)
                    .fillMaxWidth()
            )
        }
        Spacer(Modifier.height(12.dp))

        StyledTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            placeholder = "Wpisz hasło",
            label = "Hasło",
            isError = state.passwordError != null,
            isPassword = true
        )
        state.passwordError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(start = 8.dp, top = 4.dp)
                    .fillMaxWidth()
            )
        }

        if (state.isRegistering) {
            Spacer(Modifier.height(12.dp))
            StyledTextField(
                value = state.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                placeholder = "Powtórz hasło",
                label = "Hasło",
                isError = state.confirmPasswordError != null,
                isPassword = true
            )
            state.confirmPasswordError?.let{
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(start = 8.dp, top = 4.dp)
                        .fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            enabled = true
        )
             {
            Text(
                text = if (state.isRegistering) "Zarejestruj się" else "Zaloguj się",
                color = Color.White,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (state.isRegistering) "Posiadasz już konto ? " else "Nie posiadasz konta ?",
            fontSize = 14.sp
        )
        Text(
            text = if (state.isRegistering) "Logowanie" else "Rejestracja",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6200EE),
            modifier = Modifier
                .clickable { onToggleMode() }
                .padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        TextButton(onClick = onGuestLogin) {
            Text("Kontynuuj jako gość",
            color = Color(0xFF6200EE)
            )
        }

        state.error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    label: String,
    isError: Boolean = false,
    isPassword: Boolean = false
)

{
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label       = { Text(label) },
        placeholder = { Text(placeholder) },
        singleLine  = true,
        isError     = isError,
        shape       = RoundedCornerShape(50),
        visualTransformation =
            if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        textStyle   = LocalTextStyle.current.copy(color = Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    )
}
