package android.template.core.data.repository

import android.template.core.data.model.FleetType
import android.template.core.data.model.SortingOption
import android.template.core.data.model.WeighbridgeRecord
import android.template.core.database.dao.WeighbridgeRecordDao
import android.template.core.database.model.WeighbridgeRecordDbModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class DefaultWeighbridgeRecordRepository @Inject constructor(
    private val weighbridgeRecordDao: WeighbridgeRecordDao
) : WeighbridgeRecordRepository {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)

    override suspend fun insertWeighbridgeRecord(record: WeighbridgeRecord) {
        weighbridgeRecordDao.insertWeighbridgeRecord(
            WeighbridgeRecordDbModel(
                recordId = record.recordId,
                type = record.fleetType.name,
                licenseNumber = record.licenseNumber,
                driverName = record.driverName,
                tareWeight = record.tareWeight,
                grossWeight = record.grossWeight,
                entryDate = dateFormatter.format(record.entryDate)
            )
        )
    }

    override fun getAllWeighbridgeRecords(): Flow<List<WeighbridgeRecord>> {
        return weighbridgeRecordDao.getAllWeighbridgeRecords()
            .map {
                it.map {
                    WeighbridgeRecord(
                        recordId = it.recordId,
                        fleetType = FleetType.valueOf(it.type),
                        licenseNumber = it.licenseNumber,
                        driverName = it.driverName,
                        tareWeight = it.tareWeight,
                        grossWeight = it.grossWeight,
                        entryDate = dateFormatter.parse(it.entryDate)!!
                    )
                }
            }
    }

    override fun getAllWeighbridgeRecordsSortedBy(
        sortingOption: SortingOption,
        isAscending: Boolean
    ): Flow<List<WeighbridgeRecord>> {
        val fieldNames = when (sortingOption) {
            SortingOption.DATE -> WeighbridgeRecordDbModel.COLUMN_ENTRY_DATE
            SortingOption.NET_WEIGHT -> WeighbridgeRecordDbModel.COLUMN_GROSS_WEIGHT
            SortingOption.DRIVER_NAME -> WeighbridgeRecordDbModel.COLUMN_DRIVER_NAME
            SortingOption.LICENSE_NUMBER -> WeighbridgeRecordDbModel.COLUMN_LICENSE_NUMBER
        }
        return weighbridgeRecordDao.getAllWeighbridgeRecordsSortedBy(fieldNames, isAscending)
            .map {
                it.map {
                    WeighbridgeRecord(
                        recordId = it.recordId,
                        fleetType = FleetType.valueOf(it.type),
                        licenseNumber = it.licenseNumber,
                        driverName = it.driverName,
                        tareWeight = it.tareWeight,
                        grossWeight = it.grossWeight,
                        entryDate = dateFormatter.parse(it.entryDate)!!
                    )
                }
            }
    }

    override suspend fun getWeighbridgeRecordById(recordId: String): WeighbridgeRecord? {
        return weighbridgeRecordDao.getWeighbridgeRecordById(recordId)
            ?.let {
                WeighbridgeRecord(
                    recordId = it.recordId,
                    fleetType = FleetType.valueOf(it.type),
                    licenseNumber = it.licenseNumber,
                    driverName = it.driverName,
                    tareWeight = it.tareWeight,
                    grossWeight = it.grossWeight,
                    entryDate = dateFormatter.parse(it.entryDate)!!
                )
            }
    }

    override suspend fun deleteWeighbridgeRecordById(recordId: String) {
        weighbridgeRecordDao.deleteWeighbridgeRecordById(recordId)
    }

    override suspend fun deleteAllWeighbridgeRecords() {
        weighbridgeRecordDao.deleteAllWeighbridgeRecords()
    }
}

interface WeighbridgeRecordRepository {
    suspend fun insertWeighbridgeRecord(record: WeighbridgeRecord)
    fun getAllWeighbridgeRecords(): Flow<List<WeighbridgeRecord>>
    fun getAllWeighbridgeRecordsSortedBy(
        sortingOption: SortingOption,
        isAscending: Boolean
    ): Flow<List<WeighbridgeRecord>>

    suspend fun getWeighbridgeRecordById(recordId: String): WeighbridgeRecord?
    suspend fun deleteWeighbridgeRecordById(recordId: String)
    suspend fun deleteAllWeighbridgeRecords()
}
