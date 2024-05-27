package android.template.feature.weighbridge.ui

import android.template.core.data.model.FleetType
import android.template.core.ui.MyApplicationTheme
import android.template.core.ui.utils.RecordIdFormatter
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import java.text.DateFormat
import java.util.UUID

enum class FormMode {
    CREATE,
    EDIT,
    VIEW
}

@Composable
fun TicketFormScreen(
    navController: NavHostController,
    viewModel: CreateTicketFormViewModel = hiltViewModel(),
    recordId: String?,
    mode: FormMode,
    onBackButtonClicked: () -> Unit,
    onRecordSaved: () -> Unit,
    onRecordDeleted: () -> Unit
) {
    LaunchedEffect(recordId) {
        viewModel.init(recordId, mode)
    }
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
                    Toast.makeText(context, "Save Failed", Toast.LENGTH_SHORT).show()
                }
            }

            is Resource.Success -> {
                val successMessage = when (mode) {
                    FormMode.EDIT -> "Ticket Updated"
                    else -> "Ticket Created"
                }
                Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
                onRecordSaved()
            }

            else -> {}
        }
    }

    val deleteProgressState = uiState.deleteProgressState
    LaunchedEffect(deleteProgressState) {
        when (deleteProgressState) {
            is Resource.Error -> {
                deleteProgressState.error.consumeOnce {
                    Toast.makeText(context, "Delete Failed", Toast.LENGTH_SHORT).show()
                }
            }

            is Resource.Success -> {
                Toast.makeText(context, "Ticket Deleted", Toast.LENGTH_SHORT).show()
                onRecordDeleted()
            }

            else -> {}
        }
    }

    TicketFormScreen(
        uiState = uiState,
        onBackButtonClicked = onBackButtonClicked,
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
        },
        onEditButtonClicked = {
            viewModel.onEditButtonClicked()
        },
        onDeleteActionConfirmed = {
            viewModel.onDeleteActionConfirmed()
        }
    )
}

@Composable
private fun TicketFormScreen(
    uiState: CreateTicketFormUiState,
    onBackButtonClicked: () -> Unit,
    onPickFleetType: (type: FleetType) -> Unit,
    onLicenseNumberChanged: (String) -> Unit,
    onDriverNameChanged: (String) -> Unit,
    onTareWeightChanged: (String) -> Unit,
    onGrossWeightChanged: (String) -> Unit,
    onSaveBtnClicked: () -> Unit,
    onEditButtonClicked: () -> Unit,
    onDeleteActionConfirmed: () -> Unit = {}
) {
    val formMode = uiState.mode
    val formValues = uiState.formValues
    val entryDate = formValues.entryDate
    val fleetType = formValues.fleetType
    val licenseNumber = formValues.licenseNumber
    val driverName = formValues.driverName
    val tareWeight = formValues.tareWeight
    val grossWeight = formValues.grossWeight
    val isEditableMode = formMode != FormMode.VIEW
    val recordId = formValues.recordId
    val readableId = recordId?.let { RecordIdFormatter.format(it) }.orEmpty()

    var isDeletePopUpShown by remember { mutableStateOf(false)}

    val netWeight = (grossWeight.toDoubleOrNull() ?: 0.0) - (tareWeight.toDoubleOrNull() ?: 0.0)

    val saveBtnEnabled =
        licenseNumber.isNotBlank() &&
                driverName.isNotBlank() &&
                tareWeight.isNotBlank() &&
                grossWeight.isNotBlank() &&
                netWeight > 0.0

    Scaffold(
        topBar = {
            AppBarWithBackButtonAndTitle(
                title = when (formMode) {
                    FormMode.CREATE -> "Create Ticket"
                    FormMode.EDIT -> "Edit Ticket $readableId"
                    FormMode.VIEW -> "Ticket $readableId"
                },
                formMode = formMode,
                onBackButtonClick = onBackButtonClicked,
                onEditButtonClick = onEditButtonClicked,
                onDeleteButtonClick = {
                    isDeletePopUpShown = true
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
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
                                role = Role.RadioButton,
                                enabled = isEditableMode
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (type == fleetType),
                            onClick = null,
                            enabled = isEditableMode
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
                singleLine = true,
                readOnly = !isEditableMode
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
                singleLine = true,
                readOnly = !isEditableMode
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
                readOnly = !isEditableMode
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
                readOnly = !isEditableMode
            )

            OutlinedTextField(
                value = WeightFormatter().format(netWeight),
                onValueChange = {},
                label = { Text("Net Weight") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )

            AnimatedVisibility(
                visible = isEditableMode,
                modifier = Modifier.align(Alignment.End)
            ) {
                Button(
                    enabled = saveBtnEnabled,
                    onClick = onSaveBtnClicked
                ) {
                    Text("Save")
                }
            }
        }
    }

    if (isDeletePopUpShown) {
        ConfirmationDialog(
            title = "Confirm Delete",
            message = "Are you sure you want to delete this ticket?",
            onDismiss = { isDeletePopUpShown = false },
            onConfirm = {
                onDeleteActionConfirmed()
                isDeletePopUpShown = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarWithBackButtonAndTitle(
    title: String,
    formMode: FormMode,
    onBackButtonClick: () -> Unit,
    onEditButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            if (formMode == FormMode.VIEW) {
                IconButton(onClick = onEditButtonClick) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Record"
                    )
                }
            } else if (formMode == FormMode.EDIT) {
                IconButton(onClick = onDeleteButtonClick) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Record"
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun TicketFormScreenPreview() {
    MyApplicationTheme {
        TicketFormScreen(
            uiState = CreateTicketFormUiState(
                formValues = CreateTicketFormUiState.FormValues()
            ),
            onBackButtonClicked = {},
            onPickFleetType = {},
            onLicenseNumberChanged = {},
            onDriverNameChanged = {},
            onTareWeightChanged = {},
            onGrossWeightChanged = {},
            onSaveBtnClicked = {},
            onEditButtonClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TicketFormScreenOnViewModePreview() {
    MyApplicationTheme {
        TicketFormScreen(
            uiState = CreateTicketFormUiState(
                formValues = CreateTicketFormUiState.FormValues(
                    recordId = UUID.randomUUID().toString()
                ),
                mode = FormMode.VIEW
            ),
            onBackButtonClicked = {},
            onPickFleetType = {},
            onLicenseNumberChanged = {},
            onDriverNameChanged = {},
            onTareWeightChanged = {},
            onGrossWeightChanged = {},
            onSaveBtnClicked = {},
            onEditButtonClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TicketFormScreenOnEditModePreview() {
    MyApplicationTheme {
        TicketFormScreen(
            uiState = CreateTicketFormUiState(
                formValues = CreateTicketFormUiState.FormValues(
                    recordId = UUID.randomUUID().toString(),
                    fleetType = FleetType.INBOUND,
                    licenseNumber = "F 1208 BER",
                    driverName = "Thomas Bowman",
                    tareWeight = "2000",
                    grossWeight = "5510",
                ),
                mode = FormMode.EDIT
            ),
            onBackButtonClicked = {},
            onPickFleetType = {},
            onLicenseNumberChanged = {},
            onDriverNameChanged = {},
            onTareWeightChanged = {},
            onGrossWeightChanged = {},
            onSaveBtnClicked = {},
            onEditButtonClicked = {}
        )
    }
}