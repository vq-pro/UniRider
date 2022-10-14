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
        val whPerKm: Int
    )

    open fun estimatedValues(wheel: WheelEntity?, voltage: Float, km: Float): EstimatedValues {
        val whConsumedAfterStart = wh(wheel!!, wheel.voltageStart, voltage)
        val whRemainingToReserve = wh(wheel, voltage, wheel.voltageReserve)

        val whPerKm = convertConsumption(whConsumedAfterStart / km)
        val whPerKmTopOfTheRange = whPerKm + 5
        val remainingRange = max(whRemainingToReserve / whPerKmTopOfTheRange, 0f)
        val totalRange = remainingRange + km

        return EstimatedValues(
            round(remainingRange, NUM_DECIMALS),
            round(totalRange, NUM_DECIMALS),
            whPerKm
        )
    }

    open fun percentage(wheel: WheelEntity?, voltage: Float?): Float {
        if ((wheel!!.voltageMax <= 0f) || (wheel.voltageMin <= 0f)) {
            return 0f
        }

        return round(rawPercentage(wheel, voltage!!) * 100, NUM_DECIMALS)
    }

    open fun requiredVoltage(wheel: WheelEntity?, whPerKm: Int, km: Float): Float {
        val whPerKmTopOfTheRange = whPerKm + 5
        val whRequiredToReserve = whPerKmTopOfTheRange * km
        val whReserve = wh(wheel!!, wheel.voltageReserve, wheel.voltageMin)
        val whTotalRequired = whRequiredToReserve + whReserve
        val percentage = whTotalRequired / wheel.wh
        val voltageRange = wheel.voltageMax - wheel.voltageMin
        val voltageRequired = min(
            percentage * voltageRange + wheel.voltageMin + CHARGER_OFFSET,
            wheel.voltageMax - CHARGER_OFFSET
        )

        return round(voltageRequired, NUM_DECIMALS)
    }

    internal fun convertConsumption(rawConsumption: Float): Int {
        val consumption = rawConsumption / 5
        return consumption.toInt() * 5
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
