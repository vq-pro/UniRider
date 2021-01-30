package quebec.virtualite.unirider.mocks

import android.app.Activity
import quebec.virtualite.unirider.services.DeviceScanner
import quebec.virtualite.unirider.views.MainActivity
import java.util.function.Consumer

class DeviceScannerMock : DeviceScanner {

    lateinit var devices: List<String>

    override fun init(activity: Activity) {
    }

    override fun scan(whenDetecting: Consumer<String>) {
        for (device in devices) {
            whenDetecting.accept(device)
        }
    }
}