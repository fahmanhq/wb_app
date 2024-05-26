package android.template.feature.weighbridge.ui

import android.template.core.data.model.FleetType
import android.template.core.data.model.WeighbridgeRecord
import android.template.core.data.repository.WeighbridgeRecordRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class CreateTicketFormViewModel @Inject constructor(
    private val recordRepository: WeighbridgeRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTicketFormUiState())
    val uiState: StateFlow<CreateTicketFormUiState> = _uiState.asStateFlow()

    fun init(recordId: String?, mode: FormMode) {
        when (mode) {
            FormMode.EDIT,
            FormMode.VIEW -> {
                viewModelScope.launch {
                    val existingRecord = recordRepository.getWeighbridgeRecordById(recordId!!)
                    _uiState.value = _uiState.value.copy(
                        mode = mode,
                        formValues = existingRecord?.let {
                            CreateTicketFormUiState.FormValues(
                                entryDate = it.entryDate,
                                fleetType = it.fleetType,
                                licenseNumber = it.licenseNumber,
                                driverName = it.driverName,
                                tareWeight = it.tareWeight.toString(),
                                grossWeight = it.grossWeight.toString(),
                                recordId = it.recordId
                            )
                        } ?: CreateTicketFormUiState.FormValues()
                    )
                }
            }
            else -> {}
        }
    }

    fun onPickFleetType(type: FleetType) {
        _uiState.value = _uiState.value.copy(
            formValues = _uiState.value.formValues.copy(
                fleetType = type
            )
        )
    }

    fun onLicenseNumberChanged(licenseNumber: String) {
        _uiState.value = _uiState.value.copy(
            formValues = _uiState.value.formValues.copy(
                licenseNumber = licenseNumber
            )
        )
    }

    fun onDriverNameChanged(driverName: String) {
        _uiState.value = _uiState.value.copy(
            formValues = _uiState.value.formValues.copy(
                driverName = driverName
            )
        )

    }

    fun onTareWeightChanged(tareWeight: String) {
        _uiState.value = _uiState.value.copy(
            formValues = _uiState.value.formValues.copy(
                tareWeight = tareWeight
            )
        )
    }

    fun onGrossWeightChanged(grossWeight: String) {
        _uiState.value = _uiState.value.copy(
            formValues = _uiState.value.formValues.copy(
                grossWeight = grossWeight
            )
        )
    }

    fun onSaveBtnClicked() {
        viewModelScope.launch {
            val recordToSave = _uiState.value.formValues.let {
                WeighbridgeRecord(
                    entryDate = it.entryDate,
                    fleetType = it.fleetType,
                    licenseNumber = it.licenseNumber,
                    driverName = it.driverName,
                    tareWeight = it.tareWeight.toDouble(),
                    grossWeight = it.grossWeight.toDouble(),
                    recordId = when (_uiState.value.mode) {
                        FormMode.EDIT -> it.recordId!!
                        else -> UUID.randomUUID().toString()
                    }
                )
            }

            runCatching {
                recordRepository.insertWeighbridgeRecord(
                    record = recordToSave
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    saveProgressState = Resource.Error(it)
                )
            }.onSuccess {
                _uiState.value = _uiState.value.copy(
                    saveProgressState = Resource.Success(recordToSave)
                )
            }
        }
    }

    fun onEditButtonClicked() {
        _uiState.value = _uiState.value.copy(
            mode = FormMode.EDIT
        )
    }

    fun onDeleteActionConfirmed() {
        viewModelScope.launch {
            runCatching {
                recordRepository.deleteWeighbridgeRecordById(
                    recordId = _uiState.value.formValues.recordId!!
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    deleteProgressState = Resource.Error(it)
                )
            }.onSuccess {
                _uiState.value = _uiState.value.copy(
                    deleteProgressState = Resource.Success(Unit)
                )
            }
        }
    }

}

data class CreateTicketFormUiState(
    val formValues: FormValues = FormValues(),
    val mode: FormMode = FormMode.CREATE,
    val saveProgressState: Resource<WeighbridgeRecord>? = null,
    val deleteProgressState: Resource<Unit>? = null
) {
    data class FormValues(
        val entryDate: Date = Calendar.getInstance().time,
        val fleetType: FleetType = FleetType.INBOUND,
        val licenseNumber: String = "",
        val driverName: String = "",
        val tareWeight: String = "",
        val grossWeight: String = "",
        val recordId: String? = null
    )
}

sealed class Resource<T> {
    class Loading<T> : Resource<T>()
    class Success<T>(val data: T) : Resource<T>()
    class Error<T>(_error: Throwable) : Resource<T>() {
        val error: Event<Throwable> = Event(_error)
    }
}

class Event<T>(private val data: T) {
    private var consumed = AtomicBoolean(false)
    fun consumeOnce(consumer: (T) -> Unit) {
        if (consumed.compareAndSet(false, true)) {
            consumer(data)
        }
    }

    fun peek(): T? = if (consumed.get()) null else data

    fun dispose() {
        consumed.compareAndSet(false, true)
    }
}
