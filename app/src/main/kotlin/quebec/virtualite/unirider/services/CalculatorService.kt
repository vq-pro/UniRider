package quebec.virtualite.unirider.services

import quebec.virtualite.commons.android.utils.NumberUtils.round
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.views.BaseFragment.Companion.NB_DECIMALS
import java.lang.Float.min
import kotlin.math.max

open class CalculatorService {

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
            round(remainingRange, NB_DECIMALS),
            round(totalRange, NB_DECIMALS),
            round(whPerKmActual, NB_DECIMALS)
        )
    }

    open fun percentage(wheel: WheelEntity?, voltage: Float?): Float {
        if ((wheel!!.voltageMax <= 0f) || (wheel.voltageMin <= 0f)) {
            return 0f
        }

        return round(rawPercentage(wheel, voltage!!) * 100, NB_DECIMALS)
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
            percentage * voltageRange + wheel.voltageMin + wheel.chargerOffset,
            wheel.voltageFull
        )

        return round(voltageRequired, NB_DECIMALS)
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

    private fun wh(wheel: WheelEntity, highVoltage: Float, lowVoltage: Float): Float {
        val percentageHigh = rawPercentage(wheel, highVoltage)
        val percentageLow = rawPercentage(wheel, lowVoltage)

        val percentageForSegment = percentageHigh - percentageLow
        return percentageForSegment * wheel.wh
    }
}
