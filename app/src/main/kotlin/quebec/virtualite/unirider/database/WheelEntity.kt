package quebec.virtualite.unirider.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wheel")
data class WheelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val btName: String?,
    val btAddr: String?,
    val premileage: Int,
    val mileage: Int,
    val wh: Int,
    val voltageMax: Float,
    val voltageMin: Float,
    val chargeAmperage: Float,
    val chargeRate: Float,
    val voltageFull: Float,
    val distanceOffset: Float,
    val isSold: Boolean
) {
    fun isConnected() = (btName != null && btAddr != null)

    fun totalMileage() = premileage + mileage
}
