package quebec.virtualite.unirider.bluetooth.impl

import quebec.virtualite.unirider.bluetooth.Device
import quebec.virtualite.unirider.bluetooth.WheelScanner
import quebec.virtualite.unirider.views.MainActivity

open class WheelScannerImpl(mainActivity: MainActivity) : WheelScanner {

    private val connector: DeviceConnector = DeviceConnectorImpl()
    private val scanner: DeviceScanner = DeviceScannerImpl()

    init {
        connector.init(mainActivity)
        scanner.init(mainActivity)
    }

    override fun scan(found: ((Device) -> Unit)?) {
        scanner.scan(found)
    }
}
