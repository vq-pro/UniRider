package quebec.virtualite.unirider.database

interface WheelDb {

    fun deleteAll()
    fun getWheelList(): List<String>
    fun saveWheels(wheels: List<String>)
}