package quebec.virtualite.unirider.database.impl

import androidx.room.Database
import androidx.room.RoomDatabase
import quebec.virtualite.unirider.database.WheelEntity

@Database(entities = [WheelEntity::class], exportSchema = true, version = 1)
abstract class WheelDatabase : RoomDatabase() {
    abstract fun wheelDao(): WheelDistanceDao
}
