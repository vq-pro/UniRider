package quebec.virtualite.unirider.bluetooth.impl

import quebec.virtualite.commons.android.bluetooth.BluetoothDevice

interface DeviceScanner {
    fun isStopped(): Boolean
    fun scan(onDetected: (BluetoothDevice) -> Unit)
    fun stop()
}
