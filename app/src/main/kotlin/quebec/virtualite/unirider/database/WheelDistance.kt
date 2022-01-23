package quebec.virtualite.unirider.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wheel_distance")
data class WheelDistance(

    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val wheel: String,
    val distance: Int
)
