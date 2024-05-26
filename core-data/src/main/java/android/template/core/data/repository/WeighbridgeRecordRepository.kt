package android.template.core.data.repository

import android.template.core.data.model.FleetType
import android.template.core.data.model.WeighbridgeRecord
import android.template.core.database.dao.WeighbridgeRecordDao
import android.template.core.database.model.WeighbridgeRecordDbModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import javax.inject.Inject

class DefaultWeighbridgeRecordRepository @Inject constructor(
    private val weighbridgeRecordDao: WeighbridgeRecordDao
) : WeighbridgeRecordRepository {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

    override suspend fun insertWeighbridgeRecord(record: WeighbridgeRecordDbModel) {
        weighbridgeRecordDao.insertWeighbridgeRecord(record)
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
                        entryDate = dateFormatter.parse(it.entryDate)
                    )
                }
            }
    }

    override suspend fun getWeighbridgeRecordById(recordId: String): WeighbridgeRecordDbModel? {
        return weighbridgeRecordDao.getWeighbridgeRecordById(recordId)
    }

    override suspend fun deleteWeighbridgeRecordById(recordId: String) {
        weighbridgeRecordDao.deleteWeighbridgeRecordById(recordId)
    }

    override suspend fun deleteAllWeighbridgeRecords() {
        weighbridgeRecordDao.deleteAllWeighbridgeRecords()
    }
}

interface WeighbridgeRecordRepository {
    suspend fun insertWeighbridgeRecord(record: WeighbridgeRecordDbModel)
    fun getAllWeighbridgeRecords(): Flow<List<WeighbridgeRecord>>
    suspend fun getWeighbridgeRecordById(recordId: String): WeighbridgeRecordDbModel?
    suspend fun deleteWeighbridgeRecordById(recordId: String)
    suspend fun deleteAllWeighbridgeRecords()
}
