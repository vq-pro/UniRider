package quebec.virtualite.unirider.views

data class WheelChargeContext(
    val rates: ArrayList<String> = ArrayList(),
    var selectedRate: Int = -1,
    var voltage: Float = 0f
)
