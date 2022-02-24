package quebec.virtualite.unirider.services

import quebec.virtualite.unirider.database.WheelEntity

open class CalculatorService {

    open fun percentage(wheel: WheelEntity?, voltage: Float?): Float {
        if ((wheel!!.voltageMax <= 0f) || (wheel.voltageMin <= 0f)) {
            return 0f
        }

        val range = wheel.voltageMax - wheel.voltageMin
        return percentage(voltage!! - wheel.voltageMin, range)
    }

    private fun percentage(value: Float, range: Float): Float {
        return round(value * 100 / range, 1)
    }

    private fun round(value: Float, numDecimals: Int): Float {
        val factor = Math.pow(10.0, numDecimals.toDouble())
        return (Math.round(value * factor) / factor).toFloat()
    }
}
