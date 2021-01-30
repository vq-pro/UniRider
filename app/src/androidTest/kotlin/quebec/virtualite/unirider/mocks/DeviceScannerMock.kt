package quebec.virtualite.unirider.mocks

import quebec.virtualite.unirider.services.DeviceScanner
import java.util.function.Consumer

class DeviceScannerMock : DeviceScanner {

    override fun scan(whenDetecting: Consumer<String>) {
        whenDetecting.accept("toto")
    }
}