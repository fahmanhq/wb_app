package android.template.feature.weighbridge.ui

import android.template.core.data.model.SortingOption
import android.template.core.ui.Typography
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SortingDialog(
    appliedFilter: RecordListViewModel.FilterParam,
    onDismissRequest: () -> Unit,
    onSortOptionSelected: (SortingOption, Boolean) -> Unit
) {
    var selectedOption by remember {
        mutableStateOf(
            appliedFilter.sortingOption
        )
    }
    var isAscending by remember {
        mutableStateOf(
            appliedFilter.isAscending
        )
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Sort Options") },
        text = {
            Column(
                verticalArrangement = Arrangement.Top
            ) {
                SortingOption.values().forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedOption = option }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = option.label)
                        RadioButton(
                            selected = selectedOption == option,
                            onClick = { selectedOption = option }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Sort Direction")
                        Text(
                            text = (if (isAscending) "Ascending" else "Descending").uppercase(),
                            style = Typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Switch(
                        checked = isAscending,
                        onCheckedChange = {
                            isAscending = it
                        },
                        thumbContent = if (isAscending) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowUp,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                )
                            }
                        } else {
                            {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                )
                            }
                        },
                        colors = SwitchDefaults.colors(
                            uncheckedThumbColor = MaterialTheme.colorScheme.primary,
                            uncheckedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        )
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSortOptionSelected(selectedOption, isAscending) }) {
                Text(text = "Sort")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        }
    )
}

@Preview
@Composable
private fun SortingDialogPreview() {
    SortingDialog(
        appliedFilter = RecordListViewModel.FilterParam(),
        onDismissRequest = {},
        onSortOptionSelected = { _, _ -> }
    )
}

