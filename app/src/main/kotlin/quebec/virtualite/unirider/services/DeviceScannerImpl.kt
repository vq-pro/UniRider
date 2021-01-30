package quebec.virtualite.unirider.services

import quebec.virtualite.unirider.services.DeviceScanner
import java.util.function.Consumer

class DeviceScannerImpl : DeviceScanner {

    override fun scan(whenDetecting: Consumer<String>) {
        whenDetecting.accept("KingSong")
    }
}