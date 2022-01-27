package quebec.virtualite.unirider.services

import quebec.virtualite.unirider.database.WheelEntity
import java.util.Locale.ENGLISH

open class CalculatorService {

    open fun batteryOn(wheel: WheelEntity?, voltage: Float?): String {
        val range = wheel!!.voltageMax - wheel.voltageMin
        val percentage: Float = (voltage!! - wheel.voltageMin) * 100f / range

        if (percentage in 0f..100f) {
            return "%.1f%%"
                .format(ENGLISH, percentage)
        }

        return ""
    }
}
