package com.example.multimedia.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterContent(
    availableTags: List<String>,
    selectedTags: List<String>,
    onTagToggle: (String) -> Unit,
    onApply: (String?, List<String>) -> Unit,
    onClear: () -> Unit
) {
    var selectedSort by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Sortuj wg:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        val sortOptions = listOf("Tytuł A-Z", "Tytuł Z-A", "Data rosnąco", "Data malejąco")
        sortOptions.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = selectedSort == option,
                    onClick = { selectedSort = option }
                )
                Text(option)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Filtruj po tagach", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
//            mainAxisSpacing = 8.dp,
//            crossAxisSpacing = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            availableTags.forEach { tag ->
                FilterChip(
                    selected = selectedTags.contains(tag),
                    onClick = { onTagToggle(tag) },
                    label = { Text(tag) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { onApply(selectedSort, selectedTags) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Zastosuj")
            }

            OutlinedButton(
                onClick = {
                    selectedSort = null
                    onClear()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Wyczyść")
            }
        }
    }
}
