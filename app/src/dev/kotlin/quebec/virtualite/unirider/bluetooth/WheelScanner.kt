package quebec.virtualite.unirider.bluetooth

import quebec.virtualite.unirider.views.MainActivity

open class WheelScanner(mainActivity: MainActivity) {

    private val SIMULATED_DEVICE = Device("KS-14Sxx9999", "AB:CD:EF:GH")

    open fun scan(found: ((Device) -> Unit)?) {
        found!!.invoke(SIMULATED_DEVICE)
    }
}
