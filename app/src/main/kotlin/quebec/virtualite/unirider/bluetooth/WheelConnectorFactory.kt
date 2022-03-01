package quebec.virtualite.unirider.bluetooth

import android.app.Activity
import quebec.virtualite.unirider.BuildConfig.BLUETOOTH_ACTUAL
import quebec.virtualite.unirider.bluetooth.impl.WheelConnectorImpl
import quebec.virtualite.unirider.bluetooth.simulation.WheelConnectorSimulation

object WheelConnectorFactory {
    fun getConnector(activity: Activity): WheelConnector {
        return if (BLUETOOTH_ACTUAL)
            WheelConnectorImpl(activity)
        else
            WheelConnectorSimulation()
    }
}
