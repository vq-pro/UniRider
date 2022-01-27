package quebec.virtualite.unirider.services

import quebec.virtualite.unirider.database.WheelEntity

open class CalculatorService {

    open fun batteryOn(wheel: WheelEntity?, voltage: Float?): Float {
        val range = wheel!!.voltageMax - wheel.voltageMin
        val percentage: Float = (voltage!! - wheel.voltageMin) * 100f / range

        return percentage
    }
}
