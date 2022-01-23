package quebec.virtualite.unirider.database

import android.content.Context
import androidx.room.Room
import java.util.stream.Collectors.toList

class WheelDb(applicationContext: Context) {

    val db: WheelDatabase
    val dao: WheelDistanceDao

    init {
        db = Room.databaseBuilder(
            applicationContext,
            WheelDatabase::class.java,
            "wheel_database"
        ).build()

        dao = db.wheelDao()
    }

    fun deleteAll() {
        db.clearAllTables()
    }

    fun getWheelList(): List<String> {
        return dao.getAllWheels()
            .stream()
            .map { wd -> wd.wheel }
            .collect(toList())
    }

    fun saveWheels(wheels: List<String>) {
        wheels.forEach { wheelName ->
            dao.saveWheel(WheelDistance(0, wheelName, 0))
        }
    }
}