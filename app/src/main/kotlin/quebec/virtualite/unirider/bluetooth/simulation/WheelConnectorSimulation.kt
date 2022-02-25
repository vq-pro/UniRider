package quebec.virtualite.unirider.bluetooth.simulation

import quebec.virtualite.unirider.bluetooth.Device
import quebec.virtualite.unirider.bluetooth.DeviceInfo
import quebec.virtualite.unirider.bluetooth.WheelConnector

open class WheelConnectorSimulation : WheelConnector {

    companion object {
        private var device = Device("Unknown", "XX:XX:XX:XX")
        private var mileage = 123f
        private var voltage = 61.2f

        fun setDevice(device: Device) {
            WheelConnectorSimulation.device = device
        }

        fun setMileage(mileage: Float) {
            WheelConnectorSimulation.mileage = mileage
        }

        fun setVoltage(voltage: Float) {
            WheelConnectorSimulation.voltage = voltage
        }
    }

    override fun getDeviceInfo(deviceAddress: String?, function: ((DeviceInfo) -> Unit)?) {
        function!!.invoke(DeviceInfo(mileage, voltage))
    }

    override fun scan(found: ((Device) -> Unit)?) {
        found!!.invoke(device)
    }
}
