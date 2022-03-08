package quebec.virtualite.unirider.bluetooth.sim

import quebec.virtualite.commons.android.bluetooth.BluetoothDevice
import quebec.virtualite.unirider.bluetooth.BluetoothServices
import quebec.virtualite.unirider.bluetooth.WheelInfo

open class BluetoothServicesSim : BluetoothServices {

    companion object {
        private var device = BluetoothDevice("KS-14S-SIM", "AB:CD:EF:GH:IJ:KL")
        private var mileage = 123f
        private var voltage = 61.2f

        fun setDevice(device: BluetoothDevice) {
            Companion.device = device
        }

        fun setMileage(mileage: Float) {
            Companion.mileage = mileage
        }

        fun setVoltage(voltage: Float) {
            Companion.voltage = voltage
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
