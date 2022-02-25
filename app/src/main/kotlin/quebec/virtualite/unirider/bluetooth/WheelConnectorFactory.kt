package quebec.virtualite.unirider.bluetooth

import quebec.virtualite.unirider.BuildConfig.BLUETOOTH_ACTUAL
import quebec.virtualite.unirider.bluetooth.impl.WheelConnectorImpl
import quebec.virtualite.unirider.bluetooth.simulation.WheelConnectorSimulation
import quebec.virtualite.unirider.views.MainActivity

object WheelConnectorFactory {
    fun getConnector(mainActivity: MainActivity): WheelConnector {
        return if (BLUETOOTH_ACTUAL)
            WheelConnectorImpl(mainActivity)
        else
            WheelConnectorSimulation()
    }
}
