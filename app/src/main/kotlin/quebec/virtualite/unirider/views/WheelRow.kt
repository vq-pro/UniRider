package quebec.virtualite.unirider.views

class WheelRow(id: Long, name: String, mileage: Int) : HashMap<String, Any>() {
    init {
        put("id", id)
        put("name", name)
        put("mileage", mileage)
    }

    fun id(): Long {
        return get("id") as Long
    }

    fun mileage(): Int {
        return get("mileage") as Int
    }

    fun name(): String {
        return get("name").toString()
    }
}
