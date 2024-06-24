package quebec.virtualite.unirider.services

import quebec.virtualite.unirider.database.WheelEntity
import java.lang.Float.min
import kotlin.math.max

open class CalculatorService {

    companion object {
        const val CHARGER_OFFSET = 1.5f
        const val NUM_DECIMALS = 1
    }

    data class EstimatedValues(
        val remainingRange: Float,
        val totalRange: Float,
        val whPerKm: Float
    )

    open fun estimatedValues(wheel: WheelEntity?, voltage: Float, km: Float, whPerKmOverride: Float?): EstimatedValues {
        val whConsumedAfterStart = wh(wheel!!, wheel.voltageStart, voltage)
        val whPerKmActual = (whConsumedAfterStart / km)
        val whPerKm = whPerKmOverride ?: whPerKmActual

        val whRemainingToReserve = wh(wheel, voltage, adjustedReserve(wheel))
        val remainingRange = max(whRemainingToReserve / whPerKm, 0f)
        val totalRange = remainingRange + km

        return EstimatedValues(
            round(remainingRange, NUM_DECIMALS),
            round(totalRange, NUM_DECIMALS),
            round(whPerKmActual, NUM_DECIMALS)
        )
    }

    open fun percentage(wheel: WheelEntity?, voltage: Float?): Float {
        if ((wheel!!.voltageMax <= 0f) || (wheel.voltageMin <= 0f)) {
            return 0f
        }

        return round(rawPercentage(wheel, voltage!!) * 100, NUM_DECIMALS)
    }

    open fun requiredFullVoltage(wheel: WheelEntity?): Float {
        return (wheel!!).voltageFull
    }

    open fun requiredVoltage(wheel: WheelEntity?, whPerKm: Float, km: Float): Float {

        val whReserve = wh(wheel!!, adjustedReserve(wheel), wheel.voltageMin)

        val whRequiredToReserve = whPerKm * km
        val whTotalRequired = whRequiredToReserve + whReserve
        val percentage = whTotalRequired / wheel.wh
        val voltageRange = wheel.voltageMax - wheel.voltageMin
        val voltageRequired = min(
            percentage * voltageRange + wheel.voltageMin + CHARGER_OFFSET,
            wheel.voltageFull
        )

        return round(voltageRequired, NUM_DECIMALS)
    }

    internal fun adjustedReserve(wheel: WheelEntity): Float {
        return wheel.voltageReserve + 2
    }

    private fun rawPercentage(value: Float, range: Float): Float {
        return value / range
    }

    private fun rawPercentage(wheel: WheelEntity, voltage: Float): Float {
        val voltageRange = wheel.voltageMax - wheel.voltageMin
        return rawPercentage(voltage - wheel.voltageMin, voltageRange)
    }

    private fun round(value: Float, numDecimals: Int): Float {
        val factor = Math.pow(10.0, numDecimals.toDouble())
        return (Math.round(value * factor) / factor).toFloat()
    }

    private fun wh(wheel: WheelEntity, highVoltage: Float, lowVoltage: Float): Float {
        val percentageHigh = rawPercentage(wheel, highVoltage)
        val percentageLow = rawPercentage(wheel, lowVoltage)

        val percentageForSegment = percentageHigh - percentageLow
        return percentageForSegment * wheel.wh
    }
}
