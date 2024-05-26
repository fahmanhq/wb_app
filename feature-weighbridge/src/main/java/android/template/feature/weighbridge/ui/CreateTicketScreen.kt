package android.template.feature.weighbridge.ui

import android.template.core.data.model.FleetType
import android.template.core.ui.MyApplicationTheme
import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import java.text.DateFormat

@Composable
fun TicketFormScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateTicketFormViewModel = hiltViewModel(),
    onRecordSaved: () -> Unit
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val uiState by produceState(
        initialValue = CreateTicketFormUiState(),
        key1 = lifecycle,
        key2 = viewModel
    ) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.uiState.collect { value = it }
        }
    }

    val context = LocalContext.current
    val saveProgressState = uiState.saveProgressState
    LaunchedEffect(saveProgressState) {
        when (saveProgressState) {
            is Resource.Error -> {
                saveProgressState.error.consumeOnce {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
            is Resource.Success -> {
                Toast.makeText(context, "Successfully created the ticket", Toast.LENGTH_SHORT).show()
                onRecordSaved()
            }
            else -> {}
        }
    }

    TicketFormScreen(
        modifier = modifier,
        uiState = uiState,
        onPickFleetType = {
            viewModel.onPickFleetType(it)
        },
        onLicenseNumberChanged = {
            viewModel.onLicenseNumberChanged(it)
        },
        onDriverNameChanged = {
            viewModel.onDriverNameChanged(it)
        },
        onTareWeightChanged = {
            viewModel.onTareWeightChanged(it)
        },
        onGrossWeightChanged = {
            viewModel.onGrossWeightChanged(it)
        },
        onSaveBtnClicked = {
            viewModel.onSaveBtnClicked()
        }
    )
}

@Composable
private fun TicketFormScreen(
    modifier: Modifier = Modifier,
    uiState: CreateTicketFormUiState,
    onPickFleetType: (type: FleetType) -> Unit,
    onLicenseNumberChanged: (String) -> Unit,
    onDriverNameChanged: (String) -> Unit,
    onTareWeightChanged: (String) -> Unit,
    onGrossWeightChanged: (String) -> Unit,
    onSaveBtnClicked: () -> Unit
) {
    val formValues = uiState.formValues
    val entryDate = formValues.entryDate
    val fleetType = formValues.fleetType
    val licenseNumber = formValues.licenseNumber
    val driverName = formValues.driverName
    val tareWeight = formValues.tareWeight
    val grossWeight = formValues.grossWeight

    val netWeight = (grossWeight.toDoubleOrNull() ?: 0.0) - (tareWeight.toDoubleOrNull() ?: 0.0)

    val saveBtnEnabled =
            licenseNumber.isNotBlank() &&
                    driverName.isNotBlank() &&
                    tareWeight.isNotBlank() &&
                    grossWeight.isNotBlank() &&
                    netWeight > 0.0

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Entry Time : ${
                DateFormat.getDateTimeInstance().format(entryDate)
            }",
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
                                onPickFleetType(type)
                            },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (type == fleetType),
                        onClick = null
                    )
                    Text(
                        text = type.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        OutlinedTextField(
            value = licenseNumber,
            onValueChange = onLicenseNumberChanged,
            label = { Text("License Number") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )

        OutlinedTextField(
            value = driverName,
            onValueChange = onDriverNameChanged,
            label = { Text("Driver Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )

        OutlinedTextField(
            value = tareWeight,
            onValueChange = onTareWeightChanged,
            label = { Text("Tare Weight") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            suffix = { Text("kg") },
            singleLine = true,
        )

        OutlinedTextField(
            value = grossWeight,
            onValueChange = onGrossWeightChanged,
            label = { Text("Gross Weight") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            suffix = { Text("kg") },
            singleLine = true,
        )

        OutlinedTextField(
            value = WeightFormatter().format(netWeight),
            onValueChange = {},
            label = { Text("Net Weight") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        )

        Button(
            enabled = saveBtnEnabled,
            onClick = onSaveBtnClicked,
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
        TicketFormScreen(
            uiState = CreateTicketFormUiState(
                formValues = CreateTicketFormUiState.FormValues()
            ),
            onPickFleetType = {},
            onLicenseNumberChanged = {},
            onDriverNameChanged = {},
            onTareWeightChanged = {},
            onGrossWeightChanged = {},
            onSaveBtnClicked = {}
        )
    }
}