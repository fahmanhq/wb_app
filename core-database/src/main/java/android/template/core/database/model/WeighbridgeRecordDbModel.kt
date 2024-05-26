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

package android.template.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = WeighbridgeRecordDbModel.TABLE_NAME)
data class WeighbridgeRecordDbModel(
    @PrimaryKey
    val recordId: String,

    val licenseNumber: String,
    val driverName: String,
    val type: String,
    val tareWeight: Double,
    val grossWeight: Double,
    val entryDate: String,
) {

    companion object {
        const val TABLE_NAME = "weighbridge_record"
    }
}

