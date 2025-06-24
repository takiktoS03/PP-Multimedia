import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.multimedia.R

@Composable
fun ThemeToggleSwitch(
    isDarkTheme: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val thumbIcon = if (isDarkTheme) R.drawable.baseline_dark_mode_24 else R.drawable.baseline_sunny_24

    Switch(
        checked = isDarkTheme,
        onCheckedChange = onToggle,
        thumbContent = {
            Icon(
                painter = painterResource(id = thumbIcon),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        },
        colors = SwitchDefaults.colors(
            checkedTrackColor = MaterialTheme.colorScheme.primary,
            uncheckedTrackColor = MaterialTheme.colorScheme.secondary,
        )
    )
}

