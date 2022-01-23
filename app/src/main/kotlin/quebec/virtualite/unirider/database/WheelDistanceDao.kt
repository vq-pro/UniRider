package quebec.virtualite.unirider.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WheelDistanceDao {
    // FIXME 1 Define constant somewhere for table name, share with entity
    @Query("SELECT * FROM wheel_distance")
    fun getAllWheels(): List<WheelDistance>

    @Insert
    fun saveWheel(wheel: WheelDistance)
}