package quebec.virtualite.unirider.database.impl

import android.content.Context
import androidx.room.Room
import quebec.virtualite.unirider.database.WheelDb
import quebec.virtualite.unirider.database.WheelDistance
import java.util.stream.Collectors

class WheelDbImpl(applicationContext: Context) : WheelDb {
    internal var db: WheelDatabase
    internal var dao: WheelDistanceDao

    init {
        db = Room.databaseBuilder(
            applicationContext,
            WheelDatabase::class.java,
            "wheel_database"
        ).build()

        dao = db.wheelDao()
    }

    override fun deleteAll() {
        db.clearAllTables()
    }

    override fun getWheelList(): List<String> {
        return dao.getAllWheels()
            .stream()
            .map { wd -> wd.wheel }
            .collect(Collectors.toList())
    }

    override fun saveWheels(wheels: List<String>) {
        wheels.forEach { wheelName ->
            dao.saveWheel(WheelDistance(0, wheelName, 0))
        }
    }
}
