package com.example.multimedia.ui.gallery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilterContent(onApply: (String, List<String>) -> Unit) {
    val sortOptions = listOf("Tytuł A-Z", "Tytuł Z-A", "Data rosnąco", "Data malejąco")
    val selectedSort = remember { mutableStateOf(sortOptions[0]) }
    val selectedTags = remember { mutableStateListOf<String>() }

    Column(Modifier.padding(16.dp)) {
        Text("Sortowanie", style = MaterialTheme.typography.titleMedium)
        sortOptions.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { selectedSort.value = option }
            ) {
                RadioButton(
                    selected = selectedSort.value == option,
                    onClick = { selectedSort.value = option }
                )
                Text(option)
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Tagi", style = MaterialTheme.typography.titleMedium)
        val allTags = listOf("family", "nature", "vacation", "fun", "animals")

        allTags.forEach { tag ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                Checkbox(
                    checked = tag in selectedTags,
                    onCheckedChange = {
                        if (it) selectedTags.add(tag) else selectedTags.remove(tag)
                    }
                )
                Text(tag)
            }
        }

        Spacer(Modifier.height(12.dp))
        Button(onClick = { onApply(selectedSort.value, selectedTags.toList()) }) {
            Text("Zastosuj")
        }
    }
}
