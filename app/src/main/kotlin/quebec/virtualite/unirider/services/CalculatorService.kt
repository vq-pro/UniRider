package quebec.virtualite.unirider.services

import quebec.virtualite.unirider.database.WheelEntity

open class CalculatorService {

    open fun percentage(wheel: WheelEntity?, voltage: Float?): Float {
        if ((wheel!!.voltageMax <= 0f) || (wheel.voltageMin <= 0f)) {
            return 0f
        }

        return round(precisePercentage(wheel, voltage!!), 1)
    }

    // FIXME-1 Convert km to Int
    open fun range(wheel: WheelEntity?, voltage: Float, km: Float): Float {

        // FIXME-1 Get this from wheel
        val whTotal = 3600f
        val reserveVoltage = 80f

        val percentage = precisePercentage(wheel!!, voltage)
        val percentageOfReserve = precisePercentage(wheel, reserveVoltage)

        val whRemaining = whTotal * percentage / 100
        val whConsumed = whTotal - whRemaining
        val whPerKm = whConsumed / km
        val whReserve = whTotal * percentageOfReserve / 100
        val range = (whRemaining - whReserve) / whPerKm

        return round(range, 1)
    }

    private fun percentage(value: Float, range: Float): Float {
        return value * 100 / range
    }

    private fun precisePercentage(wheel: WheelEntity, voltage: Float): Float {
        val voltageRange = wheel.voltageMax - wheel.voltageMin
        return percentage(voltage - wheel.voltageMin, voltageRange)
    }

    private fun round(value: Float, numDecimals: Int): Float {
        val factor = Math.pow(10.0, numDecimals.toDouble())
        return (Math.round(value * factor) / factor).toFloat()
    }
}
