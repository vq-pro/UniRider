package quebec.virtualite.unirider.views

class WheelRow(id: Long, name: String, mileage: Float) : HashMap<String, Any>() {
    init {
        put("id", id)
        put("name", name)
        put("mileage", mileage)
    }

    fun id(): Long {
        return get("id") as Long
    }

    fun mileage(): Float {
        return get("mileage") as Float
    }

    fun name(): String {
        return get("name").toString()
    }
}
