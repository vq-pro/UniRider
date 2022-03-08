package quebec.virtualite.unirider.bluetooth.simulation

import quebec.virtualite.commons.android.bluetooth.BluetoothDevice
import quebec.virtualite.unirider.bluetooth.WheelConnector
import quebec.virtualite.unirider.bluetooth.WheelInfo

open class WheelConnectorSimulation : WheelConnector {

    companion object {
        private var device = BluetoothDevice("KS-14S-SIM", "AB:CD:EF:GH:IJ:KL")
        private var mileage = 123f
        private var voltage = 61.2f

        fun setDevice(device: BluetoothDevice) {
            WheelConnectorSimulation.device = device
        }

        fun setMileage(mileage: Float) {
            WheelConnectorSimulation.mileage = mileage
        }

        fun setVoltage(voltage: Float) {
            WheelConnectorSimulation.voltage = voltage
        }
    }

    override fun getDeviceInfo(deviceAddress: String?, onGotInfo: ((WheelInfo?) -> Unit)?) {
        Thread.sleep(1000)
        onGotInfo!!.invoke(WheelInfo(mileage, 0f, voltage))
    }

    override fun scan(onFound: ((BluetoothDevice) -> Unit)?) {
        Thread.sleep(1000)
        onFound!!.invoke(device)
    }

    override fun stopScanning() {
        // Nothing to do
    }
}
