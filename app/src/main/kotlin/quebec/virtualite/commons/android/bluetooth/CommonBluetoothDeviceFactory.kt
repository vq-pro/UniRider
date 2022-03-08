package quebec.virtualite.commons.android.bluetooth

import android.bluetooth.BluetoothGatt
import quebec.virtualite.commons.android.bluetooth.impl.CommonBluetoothDeviceImpl

interface CommonBluetoothDeviceFactory {
    fun getConnector(gatt: BluetoothGatt, services: List<String>): CommonBluetoothDeviceImpl
}