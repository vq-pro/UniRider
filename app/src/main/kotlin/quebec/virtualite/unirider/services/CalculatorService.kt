package quebec.virtualite.unirider.services

import quebec.virtualite.unirider.database.WheelEntity

open class CalculatorService {

    open fun estimatedRange(wheel: WheelEntity?, voltage: Float, km: Int): Float {
        // FIXME-1 Get this from wheel
        val whTotal = 3600f
        val reserveVoltage = 80f

        val percentage = precisePercentage(wheel!!, voltage) / 100
        val percentageOfReserve = precisePercentage(wheel, reserveVoltage) / 100

        val whRemaining = whTotal * percentage
        val whConsumed = whTotal - whRemaining
        val whPerKm = whConsumed / km
        val whReserve = whTotal * percentageOfReserve
        val range = (whRemaining - whReserve) / whPerKm

        return round(range, 1)
    }

    open fun percentage(wheel: WheelEntity?, voltage: Float?): Float {
        if ((wheel!!.voltageMax <= 0f) || (wheel.voltageMin <= 0f)) {
            return 0f
        }

        return round(precisePercentage(wheel, voltage!!), 1)
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
