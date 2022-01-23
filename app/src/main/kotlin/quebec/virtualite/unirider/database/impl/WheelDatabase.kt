package quebec.virtualite.unirider.database.impl

import androidx.room.Database
import androidx.room.RoomDatabase
import quebec.virtualite.unirider.database.WheelDistance

@Database(version = 1, entities = [WheelDistance::class])
abstract class WheelDatabase : RoomDatabase() {
    abstract fun wheelDao(): WheelDistanceDao
}
