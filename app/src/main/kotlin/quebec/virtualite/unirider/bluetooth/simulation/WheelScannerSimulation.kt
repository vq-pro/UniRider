package quebec.virtualite.unirider.bluetooth.simulation

import quebec.virtualite.unirider.bluetooth.Device
import quebec.virtualite.unirider.bluetooth.WheelScanner

open class WheelScannerSimulation : WheelScanner {

    private val SIMULATED_DEVICE = Device("KS-14Sxx9999", "AB:CD:EF:GH")

    override fun scan(found: ((Device) -> Unit)?) {
        found!!.invoke(SIMULATED_DEVICE)
    }
}
