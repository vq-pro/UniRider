package quebec.virtualite.commons.android.bluetooth

data class BluetoothDevice(
    val name: String,
    val address: String
) {
    override fun toString() = name
}
