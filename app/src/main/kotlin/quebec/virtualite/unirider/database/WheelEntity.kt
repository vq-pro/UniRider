package quebec.virtualite.unirider.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wheel")
data class WheelEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val btName: String?,
    val btAddr: String?,
    val premileage: Int,
    val mileage: Int,
    val voltageMin: Float,
    val voltageMax: Float
)
