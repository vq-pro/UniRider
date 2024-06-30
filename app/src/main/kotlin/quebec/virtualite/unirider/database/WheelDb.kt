package quebec.virtualite.unirider.database

interface WheelDb {
    fun deleteAll()
    fun deleteWheel(id: Long)
    fun findDuplicate(wheel: WheelEntity?): Boolean
    fun findWheel(name: String): WheelEntity?
    fun getWheel(id: Long): WheelEntity?
    fun getWheels(): List<WheelEntity>
    fun saveWheel(wheel: WheelEntity?)
    fun saveWheels(wheels: List<WheelEntity>?)
}