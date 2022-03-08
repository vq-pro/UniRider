package quebec.virtualite.unirider.bluetooth

import android.app.Activity
import quebec.virtualite.unirider.BuildConfig.BLUETOOTH_ACTUAL
import quebec.virtualite.unirider.bluetooth.impl.BluetoothServicesImpl
import quebec.virtualite.unirider.bluetooth.sim.BluetoothServicesSim

object BluetoothServicesFactory {
    fun getBluetoothServices(activity: Activity): BluetoothServices {
        return if (BLUETOOTH_ACTUAL)
            BluetoothServicesImpl(activity)
        else
            BluetoothServicesSim()
    }
}
