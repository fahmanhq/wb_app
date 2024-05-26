package android.template.feature.weighbridge.ui

import android.template.core.data.model.FleetType
import android.template.core.ui.MyApplicationTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.DateFormat
import java.util.Calendar

@Composable
fun TicketFormScreen(modifier: Modifier = Modifier) {
    var licenseNumber by remember { mutableStateOf("") }
    var driverName by remember { mutableStateOf("") }
    var fleetType by remember { mutableStateOf(FleetType.INBOUND) }
    var tareWeight by remember { mutableStateOf("") }
    var grossWeight by remember { mutableStateOf("") }
    val currentDateAndTime = remember { mutableStateOf(Calendar.getInstance().time) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Entry Time : ${DateFormat.getDateTimeInstance().format(currentDateAndTime.value)}",
            style = MaterialTheme.typography.bodySmall
        )

        Divider(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        )

        Text("Fleet Type")
        Row {
            FleetType.values().forEach { type ->
                Row(
                    Modifier
                        .weight(1f)
                        .height(56.dp)
                        .selectable(
                            selected = (type == fleetType),
                            onClick = {
                                fleetType = type
                            },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (type == fleetType),
                        onClick = null // null recommended for accessibility with screenreaders
                    )
                    Text(
                        text = type.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        // License number field
        OutlinedTextField(
            value = licenseNumber,
            onValueChange = { licenseNumber = it },
            label = { Text("License Number") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )

        // Driver name field
        OutlinedTextField(
            value = driverName,
            onValueChange = { driverName = it },
            label = { Text("Driver Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )

        // Tare weight field
        OutlinedTextField(
            value = tareWeight,
            onValueChange = { tareWeight = it.takeWhile { it.isDigit() } },
            label = { Text("Tare Weight") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            suffix = { Text("kg") },
            singleLine = true,
        )

        // Gross weight field
        OutlinedTextField(
            value = grossWeight,
            onValueChange = { grossWeight = it.takeWhile { it.isDigit() } },
            label = { Text("Gross Weight") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            suffix = { Text("kg") },
            singleLine = true,
        )

        val netWeight = (grossWeight.toDoubleOrNull() ?: 0.0) - (tareWeight.toDoubleOrNull() ?: 0.0)

        // Net weight field
        OutlinedTextField(
            value = WeightFormatter().format(netWeight),
            onValueChange = {}, // Non-editable field
            label = { Text("Net Weight") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        )

        // Save button
        Button(
            onClick = { /* Save the form data */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TicketFormScreenPreview() {
    MyApplicationTheme {
        TicketFormScreen()
    }
}