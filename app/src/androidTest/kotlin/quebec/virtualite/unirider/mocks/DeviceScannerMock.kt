package quebec.virtualite.unirider.mocks

import android.app.Activity
import quebec.virtualite.unirider.services.Device
import quebec.virtualite.unirider.services.DeviceScanner
import java.util.function.Consumer

class DeviceScannerMock : DeviceScanner {

    lateinit var devices: List<Device>

    private var stopped = true

    override fun init(activity: Activity) {}

    override fun isStopped(): Boolean {
        return stopped
    }

    override fun scan(whenDetecting: Consumer<Device>?) {
        stopped = false
        for (device in devices) {
            whenDetecting!!.accept(Device(device.name, device.address))

            // Mock double detection
            whenDetecting.accept(Device(device.name, device.address))
        }
    }

    override fun stop() {
        stopped = true
    }
}