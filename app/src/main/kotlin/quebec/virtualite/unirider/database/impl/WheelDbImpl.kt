package quebec.virtualite.unirider.database.impl

import android.content.Context
import androidx.room.Room
import quebec.virtualite.unirider.database.WheelDb
import quebec.virtualite.unirider.database.WheelEntity

class WheelDbImpl(applicationContext: Context) : WheelDb {
    internal var db: WheelDatabase
    internal var dao: WheelMileageDao

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

    override fun findWheel(name: String): WheelEntity? {
        return dao.findWheel(name)
    }

    override fun getWheelList(): List<WheelEntity> {
        return dao.getAllWheels()
    }

    override fun saveWheels(wheels: List<WheelEntity>) {
        wheels.forEach { wheel ->
            if (wheel.id == 0L)
                dao.insertWheel(wheel)
            else
                dao.updateWheel(wheel)
        }
    }
}
