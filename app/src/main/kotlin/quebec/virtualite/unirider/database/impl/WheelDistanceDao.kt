package quebec.virtualite.unirider.database.impl

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import quebec.virtualite.unirider.database.WheelEntity

@Dao
interface WheelDistanceDao {
    // FIXME 1 Define constant somewhere for table name, share with entity
    @Query("SELECT * FROM wheel")
    fun getAllWheels(): List<WheelEntity>

    @Query("SELECT * FROM wheel WHERE name=:name")
    fun findWheel(name: String): WheelEntity?

    @Insert
    fun saveWheel(wheel: WheelEntity)
}