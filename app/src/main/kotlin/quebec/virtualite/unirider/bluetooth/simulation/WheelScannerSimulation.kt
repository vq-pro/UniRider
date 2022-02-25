package quebec.virtualite.unirider.bluetooth.simulation

import quebec.virtualite.unirider.bluetooth.Device
import quebec.virtualite.unirider.bluetooth.DeviceInfo
import quebec.virtualite.unirider.bluetooth.WheelScanner

open class WheelScannerSimulation : WheelScanner {

    companion object {
        private var device = Device("Unknown", "XX:XX:XX:XX")
        private var mileage = 0.0f

        fun setDevice(device: Device) {
            WheelScannerSimulation.device = device
        }

        fun setMileage(mileage: Float) {
            WheelScannerSimulation.mileage = mileage
        }
    }

    override fun getDeviceInfo(deviceAddress: String?, gotInfo: ((DeviceInfo) -> Unit)?) {
        gotInfo!!.invoke(DeviceInfo(mileage))
    }

    override fun scan(found: ((Device) -> Unit)?) {
        found!!.invoke(device)
    }
}
