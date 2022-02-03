package quebec.virtualite.unirider.database.impl

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import quebec.virtualite.unirider.database.WheelEntity

@Dao
interface WheelMileageDao {
    @Query("SELECT * FROM wheel")
    fun getAllWheels(): List<WheelEntity>

    @Query("SELECT * FROM wheel WHERE name=:name")
    fun findWheel(name: String): WheelEntity?

    @Insert
    fun insertWheel(wheel: WheelEntity)

    @Update
    fun updateWheel(wheel: WheelEntity)
}
