package quebec.virtualite.unirider.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wheel")
data class WheelEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val mileage: Int,
    val voltageMax: Float,
    val voltageMin: Float
)
