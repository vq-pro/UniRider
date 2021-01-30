package quebec.virtualite.unirider.mocks

import android.app.Activity
import quebec.virtualite.unirider.services.Device
import quebec.virtualite.unirider.services.DeviceScanner
import quebec.virtualite.unirider.views.MainActivity
import java.util.function.Consumer

class DeviceScannerMock : DeviceScanner {

    lateinit var deviceNames: List<String>

    override fun init(activity: Activity) {}

    override fun scan(whenDetecting: Consumer<Device>) {
        for (deviceName in deviceNames) {
            whenDetecting.accept(Device(deviceName, ""))

            // Mock double detection
            whenDetecting.accept(Device(deviceName, ""))
        }
    }
}