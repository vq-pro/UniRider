package quebec.virtualite.commons.android.bluetooth

interface BluetoothDeviceScanner {
    fun isStopped(): Boolean
    fun scan(onDetected: (BluetoothDevice) -> Unit)
    fun stop()
}
