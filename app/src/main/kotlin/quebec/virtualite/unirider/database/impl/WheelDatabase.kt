package quebec.virtualite.unirider.database.impl

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import quebec.virtualite.unirider.database.WheelEntity

@Database(entities = [WheelEntity::class], exportSchema = true, version = 2)
abstract class WheelDatabase : RoomDatabase() {
    abstract fun wheelDao(): WheelDao

    companion object {
        val MIGRATIONS = arrayOf(
            object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE wheel ADD COLUMN voltageInitial FLOAT NOT NULL DEFAULT 0")
                }
            }
        )
    }
}
