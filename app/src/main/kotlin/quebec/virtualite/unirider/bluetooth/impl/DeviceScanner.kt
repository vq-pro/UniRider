package quebec.virtualite.unirider.bluetooth.impl

import quebec.virtualite.unirider.bluetooth.Device

interface DeviceScanner {
    fun isStopped(): Boolean
    fun scan(onDetected: (Device) -> Unit)
    fun stop()
}
