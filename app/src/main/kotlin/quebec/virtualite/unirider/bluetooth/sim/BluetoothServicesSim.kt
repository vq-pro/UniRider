package quebec.virtualite.unirider.bluetooth.sim

import quebec.virtualite.commons.android.bluetooth.BluetoothDevice
import quebec.virtualite.unirider.bluetooth.BluetoothServices
import quebec.virtualite.unirider.bluetooth.WheelInfo
import java.lang.Thread.sleep

private const val BLUETOOTH_SIMULATED_DELAY = 0L

open class BluetoothServicesSim : BluetoothServices {

    companion object {
        private var device = BluetoothDevice("LK13447", "AB:CD:EF:GH:IJ:KL")
        private var km = 21.867f    // km wheel, returned using distance offset for GPS value
        private var mileage = 20020.518f
        private var voltages = listOf(141.01f, 142.5f, 143.5f, 144.0f)
        private var iCurrentVoltage = 0

        fun reset() {
            iCurrentVoltage = 0
        }

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

        fun setVoltages(voltages: List<Float>): Companion {
            iCurrentVoltage = 0
            Companion.voltages = voltages
            return this
        }
    }

    override fun getDeviceInfo(deviceAddress: String?, onGotInfo: ((WheelInfo?) -> Unit)?) {
        sleep(BLUETOOTH_SIMULATED_DELAY)

        val voltage = voltages[iCurrentVoltage]
        if (++iCurrentVoltage >= voltages.size)
            iCurrentVoltage = 0

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
