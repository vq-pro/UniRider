package quebec.virtualite.unirider.mocks

import android.app.Activity
import quebec.virtualite.unirider.services.Device
import quebec.virtualite.unirider.services.DeviceScanner
import quebec.virtualite.unirider.views.MainActivity
import java.util.function.Consumer

class DeviceScannerMock : DeviceScanner {

    lateinit var devices: List<Device>

    override fun init(activity: Activity) {}

    override fun scan(whenDetecting: Consumer<Device>) {
        for (device in devices) {
            whenDetecting.accept(Device(device.name, device.address))

            // Mock double detection
            whenDetecting.accept(Device(device.name, device.address))
        }
    }
}