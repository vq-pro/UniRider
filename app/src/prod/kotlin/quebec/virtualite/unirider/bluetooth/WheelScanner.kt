package quebec.virtualite.unirider.bluetooth

import quebec.virtualite.unirider.views.MainActivity
import java.util.function.Consumer

open class WheelScanner(mainActivity: MainActivity) {

    private val connector: DeviceConnector = DeviceConnectorImpl()
    private val scanner: DeviceScanner = DeviceScannerImpl()

    init {
        connector.init(mainActivity)
        scanner.init(mainActivity)
    }

    open fun scan(function: Consumer<Device>?) {
        scanner.scan(function)
    }
}
