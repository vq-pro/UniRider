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

    override fun findDuplicate(wheel: WheelEntity?): Boolean {
        val existing = dao.findWheel(wheel!!.name)
        return (existing != null) && (existing.id != wheel.id)
    }

    override fun findWheel(name: String): WheelEntity? {
        return dao.findWheel(name)
    }

    override fun getWheel(id: Long): WheelEntity? {
        return dao.getWheel(id)
    }

    override fun getWheels(): List<WheelEntity> {
        return dao.getAllWheels()
    }

    override fun saveWheel(wheel: WheelEntity?) {
        if (wheel!!.id == 0L)
            dao.insertWheel(wheel)
        else
            dao.updateWheel(wheel)
    }

    override fun saveWheels(wheels: List<WheelEntity>?) {
        wheels!!.forEach { wheel ->
            saveWheel(wheel)
        }
    }
}
