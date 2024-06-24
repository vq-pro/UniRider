package quebec.virtualite.unirider.bluetooth.sim

import quebec.virtualite.commons.android.bluetooth.BluetoothDevice
import quebec.virtualite.unirider.bluetooth.BluetoothServices
import quebec.virtualite.unirider.bluetooth.WheelInfo
import java.lang.Thread.sleep

private const val BLUETOOTH_SIMULATED_DELAY = 250L

open class BluetoothServicesSim : BluetoothServices {

    companion object {
        private var device = BluetoothDevice("KS-14S-SIM", "AB:CD:EF:GH:IJ:KL")
        private var km = 15f
        private var mileage = 123f
        private var voltage = 61.2f

        fun setDevice(device: BluetoothDevice): Companion {
            Companion.device = device
            return this
        }

        fun setKm(km: Float): Companion {
            Companion.km = km
            return this
        }

        fun setMileage(mileage: Float): Companion {
            Companion.mileage = mileage
            return this
        }

        fun setVoltage(voltage: Float): Companion {
            Companion.voltage = voltage
            return this
        }
    }

    override fun getDeviceInfo(deviceAddress: String?, onGotInfo: ((WheelInfo?) -> Unit)?) {
        sleep(BLUETOOTH_SIMULATED_DELAY)
        onGotInfo!!.invoke(WheelInfo(km, mileage, 0f, voltage))
    }

    override fun scan(onFound: ((BluetoothDevice) -> Unit)?) {
        sleep(BLUETOOTH_SIMULATED_DELAY)
        onFound!!.invoke(device)
    }

    override fun stopScanning() {
        // Nothing to do
    }
}
