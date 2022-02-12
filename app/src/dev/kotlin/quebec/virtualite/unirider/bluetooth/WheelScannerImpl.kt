package quebec.virtualite.unirider.bluetooth

import quebec.virtualite.unirider.views.MainActivity

open class WheelScannerImpl(mainActivity: MainActivity) : WheelScanner {

    private val SIMULATED_DEVICE = Device("KS-14Sxx9999", "AB:CD:EF:GH")

    override fun scan(found: ((Device) -> Unit)?) {
        found!!.invoke(SIMULATED_DEVICE)
    }
}
