package android.template.core.data.model

import java.util.Date

data class WeighbridgeRecord(
    val recordId: String,
    val fleetType: FleetType,
    val licenseNumber: String,
    val driverName: String,
    val tareWeight: Double,
    val grossWeight: Double,
    val entryDate: Date,
)