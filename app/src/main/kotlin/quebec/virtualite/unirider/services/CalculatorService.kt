package quebec.virtualite.unirider.services

import quebec.virtualite.unirider.database.WheelEntity
import kotlin.math.max

open class CalculatorService {

    data class EstimatedValues(
        val remainingRange: Float,
        val totalRange: Float,
        val whPerKm: Float
    )

    open fun estimatedValues(wheel: WheelEntity?, voltage: Float, km: Float): EstimatedValues {
        val percentage = precisePercentage(wheel!!, voltage) / 100
        val percentageOfReserve = precisePercentage(wheel, wheel.voltageReserve) / 100

        val whRemaining = wheel.wh * percentage
        val whConsumed = wheel.wh - whRemaining
        val whPerKm = whConsumed / km
        val whReserve = wheel.wh * percentageOfReserve

        val remainingRange = max((whRemaining - whReserve) / whPerKm, 0f)
        val totalRange = remainingRange + km

        return EstimatedValues(
            round(remainingRange, 1),
            round(totalRange, 1),
            round(whPerKm, 1)
        )
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
