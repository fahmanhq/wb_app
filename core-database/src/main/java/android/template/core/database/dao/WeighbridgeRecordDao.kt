package android.template.core.database.dao

import android.template.core.database.model.WeighbridgeRecordDbModel
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface WeighbridgeRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeighbridgeRecord(record: WeighbridgeRecordDbModel)

    @Query("SELECT * FROM ${WeighbridgeRecordDbModel.TABLE_NAME} ORDER BY entryDate DESC")
    fun getAllWeighbridgeRecords(): Flow<List<WeighbridgeRecordDbModel>>

    fun getAllWeighbridgeRecordsSortedBy(
        baseField: String,
        isAscending: Boolean
    ): Flow<List<WeighbridgeRecordDbModel>> {
        val query =
            "SELECT * FROM ${WeighbridgeRecordDbModel.TABLE_NAME} " +
                    "ORDER BY $baseField ${if (isAscending) "ASC" else "DESC"}"
        return getAllWeighbridgeRecordsViaQuery(SimpleSQLiteQuery(query))
    }

    @RawQuery(observedEntities = [WeighbridgeRecordDbModel::class])
    fun getAllWeighbridgeRecordsViaQuery(
        query: SupportSQLiteQuery
    ): Flow<List<WeighbridgeRecordDbModel>>

    @Query("SELECT * FROM ${WeighbridgeRecordDbModel.TABLE_NAME} WHERE recordId = :recordId")
    suspend fun getWeighbridgeRecordById(recordId: String): WeighbridgeRecordDbModel?

    @Query("DELETE FROM ${WeighbridgeRecordDbModel.TABLE_NAME} WHERE recordId = :recordId")
    suspend fun deleteWeighbridgeRecordById(recordId: String)

    @Query("DELETE FROM ${WeighbridgeRecordDbModel.TABLE_NAME}")
    suspend fun deleteAllWeighbridgeRecords()
}