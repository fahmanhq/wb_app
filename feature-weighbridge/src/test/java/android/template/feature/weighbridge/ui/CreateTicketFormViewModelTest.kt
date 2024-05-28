/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.template.feature.weighbridge.ui


import android.template.core.data.model.FleetType
import android.template.core.data.model.Resource
import android.template.core.data.model.WeighbridgeRecord
import android.template.core.data.repository.FakeWeighbridgeRecordRepository
import android.template.core.data.repository.WeighbridgeRecordRepository
import android.template.feature.weighbridge.ui.detail.CreateTicketFormViewModel
import android.template.feature.weighbridge.ui.detail.FormMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class CreateTicketFormViewModelTest {

    private lateinit var fakeRepository: WeighbridgeRecordRepository
    private lateinit var viewModel: CreateTicketFormViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        fakeRepository = FakeWeighbridgeRecordRepository()
        viewModel = CreateTicketFormViewModel(fakeRepository)
    }

    @Test
    fun uiState_initialData() = runTest {
        // assert initial value
        assertTrue(
            viewModel.uiState.value.mode == FormMode.CREATE
        )
        assertTrue(
            viewModel.uiState.value.formValues.let {
                it.licenseNumber.isEmpty() &&
                        it.driverName.isEmpty() &&
                        it.tareWeight.isEmpty() &&
                        it.grossWeight.isEmpty() &&
                        it.recordId.isNullOrEmpty()
            }
        )
    }

    @Test
    fun uiState_onFormValueChanged_updateFormValues() = runTest {

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        // trigger value changes
        val newFleetType = FleetType.OUTBOUND
        val newLicenseNumber = "ABC 12345"
        val newDriverName = "John Doe"
        val newTareWeight = "1000"
        val newGrossWeight = "2000"

        viewModel.onPickFleetType(newFleetType)
        viewModel.onLicenseNumberChanged(newLicenseNumber)
        viewModel.onDriverNameChanged(newDriverName)
        viewModel.onTareWeightChanged(newTareWeight)
        viewModel.onGrossWeightChanged(newGrossWeight)

        val formValues = viewModel.uiState.value.formValues
        assertEquals(
            newFleetType,
            formValues.fleetType
        )
        assertEquals(
            newLicenseNumber,
            formValues.licenseNumber
        )
        assertEquals(
            newDriverName,
            formValues.driverName
        )
        assertEquals(
            newTareWeight,
            formValues.tareWeight
        )
        assertEquals(
            newGrossWeight,
            formValues.grossWeight
        )

        collectJob.cancel()
    }

    @Test
    fun uiState_onSaveBtnClicked_saveRecord() = runTest {

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        // trigger value changes
        val newFleetType = FleetType.OUTBOUND
        val newLicenseNumber = "ABC 12345"
        val newDriverName = "John Doe"
        val newTareWeight = "1000"
        val newGrossWeight = "2000"

        viewModel.onPickFleetType(newFleetType)
        viewModel.onLicenseNumberChanged(newLicenseNumber)
        viewModel.onDriverNameChanged(newDriverName)
        viewModel.onTareWeightChanged(newTareWeight)
        viewModel.onGrossWeightChanged(newGrossWeight)

        viewModel.onSaveBtnClicked()

        val saveProgressState = viewModel.uiState.value.saveProgressState
        assertTrue(saveProgressState is Resource.Success)

        val resultData = (saveProgressState as Resource.Success).data
        assertTrue(resultData.recordId.isNotBlank())

        // check if the record is saved in the repository
        assertTrue(
            fakeRepository.getWeighbridgeRecordById(resultData.recordId) != null
        )

        collectJob.cancel()
    }

    @Test
    fun uiState_onEditButtonClicked_updateMode() = runTest {

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.onEditButtonClicked()

        val mode = viewModel.uiState.value.mode
        assertEquals(FormMode.EDIT, mode)

        collectJob.cancel()
    }

    @Test
    fun uiState_onDeleteActionConfirmed_deleteRecord() = runTest {
        val sampleWeighbridgeRecord = WeighbridgeRecord(
            recordId = "reprehendunt",
            fleetType = FleetType.INBOUND,
            licenseNumber = "porro",
            driverName = "Herschel Coffey",
            tareWeight = 4.5,
            grossWeight = 6.7,
            entryDate = Calendar.getInstance().time,
        )
        fakeRepository.insertWeighbridgeRecord(
            sampleWeighbridgeRecord
        )

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.init(recordId = sampleWeighbridgeRecord.recordId, FormMode.EDIT)
        viewModel.onDeleteActionConfirmed()

        val deleteProgressState = viewModel.uiState.value.deleteProgressState
        assertTrue(deleteProgressState is Resource.Success)

        assertNull(fakeRepository.getWeighbridgeRecordById(sampleWeighbridgeRecord.recordId))

        collectJob.cancel()
    }
}

