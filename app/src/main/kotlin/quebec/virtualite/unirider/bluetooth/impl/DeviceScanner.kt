package quebec.virtualite.unirider.bluetooth.impl

import android.app.Activity
import quebec.virtualite.unirider.bluetooth.Device
import java.util.function.Consumer

interface DeviceScanner {

    fun init(activity: Activity)

    fun isStopped(): Boolean
    fun scan(whenDetecting: Consumer<Device>?)
    fun stop()
}
