package quebec.virtualite.unirider.bluetooth

import quebec.virtualite.unirider.BuildConfig.BLUETOOTH_ACTUAL
import quebec.virtualite.unirider.bluetooth.impl.WheelScannerImpl
import quebec.virtualite.unirider.bluetooth.simulation.WheelScannerSimulation
import quebec.virtualite.unirider.views.MainActivity

object WheelScannerFactory {
    fun getScannerImpl(mainActivity: MainActivity): WheelScanner {
        return if (BLUETOOTH_ACTUAL)
            WheelScannerImpl(mainActivity)
        else
            WheelScannerSimulation()
    }
}
