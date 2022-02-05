package quebec.virtualite.unirider.database

interface WheelDb {

    fun deleteAll()
    fun findWheel(name: String): WheelEntity?
    fun getWheel(id: Long): WheelEntity?
    fun getWheelList(): List<WheelEntity>
    fun saveWheel(wheel: WheelEntity?)
    fun saveWheels(wheels: List<WheelEntity>?)
}