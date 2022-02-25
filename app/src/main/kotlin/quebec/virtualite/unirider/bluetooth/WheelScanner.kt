package quebec.virtualite.unirider.bluetooth

// FIXME-1 Find new name for this
interface WheelScanner {
    fun getDeviceInfo(deviceAddress: String?, gotInfo: ((DeviceInfo) -> Unit)?)
    fun scan(found: ((Device) -> Unit)?)
}
