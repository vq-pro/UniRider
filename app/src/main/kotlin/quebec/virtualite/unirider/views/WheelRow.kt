package quebec.virtualite.unirider.views

class WheelRow(name: String, distance: Int) : HashMap<String, Any>() {
    init {
        put("name", name)
        put("distance", distance)
    }

    fun distance(): Int {
        return get("distance") as Int
    }

    fun name(): String {
        return get("name").toString()
    }
}
