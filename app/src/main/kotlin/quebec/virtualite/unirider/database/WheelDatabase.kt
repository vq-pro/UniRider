package quebec.virtualite.unirider.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [WheelDistance::class])
abstract class WheelDatabase : RoomDatabase() {
    abstract fun wheelDao(): WheelDistanceDao
}

//@Database(entities = [Book::class], version = 1)
//abstract class BookDatabase : RoomDatabase() {
//    abstract fun bookDao(): BookDao
//}
