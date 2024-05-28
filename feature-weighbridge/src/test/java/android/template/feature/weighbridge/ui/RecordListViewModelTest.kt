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
import android.template.core.data.model.SortingOption
import android.template.core.data.model.WeighbridgeRecord
import android.template.core.data.repository.FakeWeighbridgeRecordRepository
import android.template.core.data.repository.WeighbridgeRecordRepository
import android.template.feature.weighbridge.ui.list.RecordListUiState
import android.template.feature.weighbridge.ui.list.RecordListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Calendar

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RecordListViewModelTest {

    private lateinit var fakeRepository: WeighbridgeRecordRepository
    private lateinit var viewModel: RecordListViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        fakeRepository = FakeWeighbridgeRecordRepository()
        viewModel = RecordListViewModel(fakeRepository)
    }

    @Test
    fun uiState_initialData_isDisplayed() = runTest {
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

        // assert initial value
        assertTrue(
            viewModel.uiState.value is RecordListUiState.Loading
        )
        // assert default filter param value
        assertTrue(
            viewModel.filterParam.value.run {
                sortingOption == SortingOption.DATE && !isAscending
            }
        )

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        // assert first retrieved value
        val uiState = viewModel.uiState.value
        assertTrue(
            uiState is RecordListUiState.Success
        )
        assertEquals(
            sampleWeighbridgeRecord,
            (uiState as RecordListUiState.Success).data[0]
        )

        collectJob.cancel()
    }

    @Test
    fun uiState_onSortOptionSelected_changeParamValue() = runTest {
        val sampleWeighbridgeRecord = WeighbridgeRecord(
            recordId = "reprehendunt",
            fleetType = FleetType.INBOUND,
            licenseNumber = "porro",
            driverName = "Herschel Coffey",
            tareWeight = 4.5,
            grossWeight = 6.7,
            entryDate = Calendar.getInstance().time,
        )
        val sampleWeighbridgeRecord2 = WeighbridgeRecord(
            entryDate = Calendar.getInstance().time,
            recordId = "volutpat",
            fleetType = FleetType.INBOUND,
            licenseNumber = "efficiantur",
            driverName = "Joanna Martinez",
            tareWeight = 12.13,
            grossWeight = 14.15,
        )
        fakeRepository.insertWeighbridgeRecord(
            sampleWeighbridgeRecord
        )
        fakeRepository.insertWeighbridgeRecord(
            sampleWeighbridgeRecord2
        )

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.onSortOptionSelected(SortingOption.DRIVER_NAME, isAscending = true)

        // assert changed filter param value
        assertTrue(
            viewModel.filterParam.value.run {
                sortingOption == SortingOption.DRIVER_NAME && isAscending
            }
        )

        // assert updated value
        val uiState = viewModel.uiState.value
        assertTrue(
            uiState is RecordListUiState.Success
        )
        assertTrue(
            (uiState as RecordListUiState.Success).data.let {
                it[0] == sampleWeighbridgeRecord && it[1] == sampleWeighbridgeRecord2
            }
        )

        collectJob.cancel()
    }
}

