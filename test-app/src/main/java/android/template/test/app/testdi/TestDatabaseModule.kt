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

package android.template.test.app.testdi

import android.template.core.data.di.DataModule
import android.template.core.data.model.SortingOption
import android.template.core.data.model.WeighbridgeRecord
import android.template.core.data.repository.WeighbridgeRecordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class]
)
interface FakeDataModule {

    @Binds
    fun bindsWeighbridgeRecordRepository(
        weighbridgeRecordRepository: FakeWeighbridgeRecordRepository
    ): WeighbridgeRecordRepository
}

class FakeWeighbridgeRecordRepository @Inject constructor() : WeighbridgeRecordRepository {

    private val records = mutableListOf<WeighbridgeRecord>()

    override suspend fun insertWeighbridgeRecord(record: WeighbridgeRecord) {
        records.add(record)
    }

    override fun getAllWeighbridgeRecords(): Flow<List<WeighbridgeRecord>> {
        return flowOf(records)
    }

    override fun getAllWeighbridgeRecordsSortedBy(
        sortingOption: SortingOption,
        isAscending: Boolean
    ): Flow<List<WeighbridgeRecord>> {
        return flowOf(
            records.sortedWith(
                if (isAscending) {
                    when (sortingOption) {
                        SortingOption.DATE -> compareBy { it.entryDate }
                        SortingOption.NET_WEIGHT -> compareBy { it.netWeight }
                        SortingOption.DRIVER_NAME -> compareBy { it.driverName }
                        SortingOption.LICENSE_NUMBER -> compareBy { it.licenseNumber }
                    }
                } else {
                    when (sortingOption) {
                        SortingOption.DATE -> compareByDescending { it.entryDate }
                        SortingOption.NET_WEIGHT -> compareByDescending { it.netWeight }
                        SortingOption.DRIVER_NAME -> compareByDescending { it.driverName }
                        SortingOption.LICENSE_NUMBER -> compareByDescending { it.licenseNumber }
                    }
                }
            )
        )
    }

    override suspend fun getWeighbridgeRecordById(recordId: String): WeighbridgeRecord? {
        return records.find { it.recordId == recordId }
    }

    override suspend fun deleteWeighbridgeRecordById(recordId: String) {
        records.remove(
            records.find { it.recordId == recordId }
        )
    }

    override suspend fun deleteAllWeighbridgeRecords() {
        records.clear()
    }
}
