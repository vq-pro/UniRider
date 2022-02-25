package quebec.virtualite.unirider.bluetooth.impl

import android.bluetooth.BluetoothGattCharacteristic

abstract class DeviceConnectorWheel {

    lateinit var wheelData: WheelData

    abstract fun enableNotifications()
    abstract fun onCharacteristicChanged(characteristic: BluetoothGattCharacteristic)
}