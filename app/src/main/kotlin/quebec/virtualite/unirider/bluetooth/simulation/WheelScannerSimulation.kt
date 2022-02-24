package quebec.virtualite.unirider.bluetooth.simulation

import quebec.virtualite.unirider.bluetooth.Device
import quebec.virtualite.unirider.bluetooth.DeviceInfo
import quebec.virtualite.unirider.bluetooth.WheelScanner

open class WheelScannerSimulation : WheelScanner {

    companion object {
        private var simulatedMileage = 0.0f

        fun setMileage(simulatedMileage: Float) {
            WheelScannerSimulation.simulatedMileage = simulatedMileage
        }
    }

    private val SIMULATED_DEVICE = Device("KS-14Sxx9999", "AB:CD:EF:GH")

    override fun getDeviceInfo(deviceAddress: String?, gotInfo: ((DeviceInfo) -> Unit)?) {
        gotInfo!!.invoke(DeviceInfo(simulatedMileage))
    }

    override fun scan(found: ((Device) -> Unit)?) {
        found!!.invoke(SIMULATED_DEVICE)
    }
}
