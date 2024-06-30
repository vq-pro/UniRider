package quebec.virtualite.unirider.database.impl

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import quebec.virtualite.unirider.database.WheelEntity

@Dao
interface WheelDao {
    @Query("DELETE FROM wheel WHERE id=:id")
    fun deleteWheel(id: Long)

    @Query("SELECT * FROM wheel WHERE name=:name")
    fun findWheel(name: String): WheelEntity?

    @Query("SELECT * FROM wheel")
    fun getAllWheels(): List<WheelEntity>

    @Query("SELECT * FROM wheel WHERE id=:id")
    fun getWheel(id: Long): WheelEntity?

    @Insert
    fun insertWheel(wheel: WheelEntity)

    @Update
    fun updateWheel(wheel: WheelEntity)
}
