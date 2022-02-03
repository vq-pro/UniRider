package quebec.virtualite.unirider.services

import quebec.virtualite.unirider.database.WheelEntity

open class CalculatorService {

    open fun percentage(wheel: WheelEntity?, voltage: Float?): Float {
        if ((wheel!!.voltageMax <= 0f) || (wheel.voltageMin <= 0f)) {
            return 0f
        }

        val range = wheel.voltageMax - wheel.voltageMin
        val percentage: Float = (voltage!! - wheel.voltageMin) * 100f / range

        return percentage
    }
}
