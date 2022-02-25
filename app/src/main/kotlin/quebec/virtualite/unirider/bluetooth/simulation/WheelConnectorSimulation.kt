package quebec.virtualite.unirider.bluetooth.simulation

import quebec.virtualite.unirider.bluetooth.Device
import quebec.virtualite.unirider.bluetooth.DeviceInfo
import quebec.virtualite.unirider.bluetooth.WheelConnector

open class WheelConnectorSimulation : WheelConnector {

    companion object {
        private var device = Device("Unknown", "XX:XX:XX:XX")
        private var mileage = 0.0f

        fun setDevice(device: Device) {
            WheelConnectorSimulation.device = device
        }

        fun setMileage(mileage: Float) {
            WheelConnectorSimulation.mileage = mileage
        }
    }

    override fun getDeviceInfo(deviceAddress: String?, function: ((DeviceInfo) -> Unit)?) {
        if (deviceAddress.equals(device.address))
            function!!.invoke(DeviceInfo(mileage))
    }

    override fun scan(found: ((Device) -> Unit)?) {
        found!!.invoke(device)
    }
}