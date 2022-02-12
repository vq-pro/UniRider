package quebec.virtualite.unirider.bluetooth

import android.app.Activity
import java.util.function.Consumer

interface DeviceScanner {

    fun init(activity: Activity)

    fun isStopped(): Boolean
    fun scan(whenDetecting: Consumer<Device>?)
    fun stop()
}
