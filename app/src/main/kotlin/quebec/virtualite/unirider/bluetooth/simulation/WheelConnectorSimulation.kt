package quebec.virtualite.unirider.bluetooth.simulation

import quebec.virtualite.unirider.bluetooth.Device
import quebec.virtualite.unirider.bluetooth.DeviceInfo
import quebec.virtualite.unirider.bluetooth.WheelConnector

open class WheelConnectorSimulation : WheelConnector {

    companion object {
        private var device = Device("KS-14S-SIM", "AB:CD:EF:GH:IJ:KL")
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

    override fun getDeviceInfo(deviceAddress: String?, onGotInfo: ((DeviceInfo?) -> Unit)?) {
        Thread.sleep(1000)
        onGotInfo!!.invoke(DeviceInfo(mileage, voltage))
    }

    override fun scan(onFound: ((Device) -> Unit)?) {
        Thread.sleep(1000)
        onFound!!.invoke(device)
    }

    override fun stopScanning() {
        // Nothing to do
    }
}
