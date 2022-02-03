package quebec.virtualite.unirider.views

class WheelRow(name: String, mileage: Int) : HashMap<String, Any>() {
    init {
        put("name", name)
        put("mileage", mileage)
    }

    fun mileage(): Int {
        return get("mileage") as Int
    }

    fun name(): String {
        return get("name").toString()
    }
}
