package android.template.core.database.dao

import android.template.core.database.model.WeighbridgeRecordDbModel
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeighbridgeRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeighbridgeRecord(record: WeighbridgeRecordDbModel)

    @Query("SELECT * FROM ${WeighbridgeRecordDbModel.TABLE_NAME} ORDER BY entryDate DESC")
    fun getAllWeighbridgeRecords(): Flow<List<WeighbridgeRecordDbModel>>

    @Query("SELECT * FROM ${WeighbridgeRecordDbModel.TABLE_NAME} WHERE recordId = :recordId")
    suspend fun getWeighbridgeRecordById(recordId: String): WeighbridgeRecordDbModel?

    @Query("DELETE FROM ${WeighbridgeRecordDbModel.TABLE_NAME} WHERE recordId = :recordId")
    suspend fun deleteWeighbridgeRecordById(recordId: String)

    @Query("DELETE FROM ${WeighbridgeRecordDbModel.TABLE_NAME}")
    suspend fun deleteAllWeighbridgeRecords()
}