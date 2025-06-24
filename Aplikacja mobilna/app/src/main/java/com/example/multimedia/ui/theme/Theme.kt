package com.example.multimedia.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val DarkColors = darkColorScheme(
    primary = Color(0xFF5C32F6),
    secondary = Color(0xFF0036FF),
)

private val LightColors = lightColorScheme(
    primary = Color(0xC8B185FF),
    secondary = Color(0xFF7AE0FF),
)

@Composable
fun MultimediaTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val darkThemeFlow = remember { ThemeManager.isDarkTheme(context) }
    val isDark = darkThemeFlow.collectAsState(initial = false).value

    MaterialTheme(
        colorScheme = if (isDark) DarkColors else LightColors,
        typography = Typography(),
        content = content
    )
}
