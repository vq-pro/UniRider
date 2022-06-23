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
        val whConsumedAfterStart = wh(wheel!!, wheel.voltageStart!!, voltage)
        val whRemainingToReserve = wh(wheel, voltage, wheel.voltageReserve)

        val whPerKm = whConsumedAfterStart / km
        val remainingRange = max(whRemainingToReserve / whPerKm, 0f)
        val totalRange = remainingRange + km

        return EstimatedValues(
            round(remainingRange, 1),
            round(totalRange, 1),
            round(whPerKm, 1)
        )
    }

    open fun roundedPercentage(wheel: WheelEntity?, voltage: Float?): Float {
        if ((wheel!!.voltageMax <= 0f) || (wheel.voltageMin <= 0f)) {
            return 0f
        }

        return round(percentage(wheel, voltage!!) * 100, 1)
    }

    private fun percentage(value: Float, range: Float): Float {
        return value / range
    }

    private fun percentage(wheel: WheelEntity, voltage: Float): Float {
        val voltageRange = wheel.voltageMax - wheel.voltageMin
        return percentage(voltage - wheel.voltageMin, voltageRange)
    }

    private fun round(value: Float, numDecimals: Int): Float {
        val factor = Math.pow(10.0, numDecimals.toDouble())
        return (Math.round(value * factor) / factor).toFloat()
    }

    private fun wh(wheel: WheelEntity, highVoltage: Float, lowVoltage: Float): Float {
        val percentageHigh = percentage(wheel, highVoltage)
        val percentageLow = percentage(wheel, lowVoltage)

        val percentageForSegment = percentageHigh - percentageLow
        return percentageForSegment * wheel.wh
    }
}
