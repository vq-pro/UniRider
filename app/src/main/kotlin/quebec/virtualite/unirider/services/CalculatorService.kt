package quebec.virtualite.unirider.services

import quebec.virtualite.commons.android.utils.NumberUtils.round
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.views.NB_DECIMALS
import java.lang.Float.min

open class CalculatorService {

    data class EstimatedValues(
        val remainingRange: Float,
        val totalRange: Float,
        val whPerKm: Float
    )

    //    FIXME-1 Complete table
    private val ENERGY_PER_CELL_VOLTAGE = arrayOf(
        arrayOf(4200, 181440),
        arrayOf(3920, 145475),
        arrayOf(3695, 99730),
        arrayOf(3680, 96035),
        arrayOf(3650, 92355),
        arrayOf(3505, 67205),
        arrayOf(3490, 63700),
        arrayOf(3480, 60210),
        arrayOf(3430, 53280)
    )

    open fun estimatedValues(wheel: WheelEntity, voltage: Float, km: Float): EstimatedValues {
        val sor = getSoR(wheel, voltage)

        val totalRange = 100 * km / (100 - sor)
        val remainingRange = totalRange - km

        return EstimatedValues(
            round(remainingRange, NB_DECIMALS),
            round(totalRange, NB_DECIMALS),
            0f
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
            percentage * voltageRange + wheel.voltageMin + wheel.chargerOffset, wheel.voltageFull
        )

        return round(voltageRequired, NB_DECIMALS)
    }

    internal fun adjustedReserve(wheel: WheelEntity): Float {
        return wheel.voltageReserve + 2
    }


    private fun calculateSoR(row: Array<Int>): Float {
        val max = ENERGY_PER_CELL_VOLTAGE[0][1]
        val soE = row[1].toFloat() / max.toFloat()
        val safeSoE = (soE - 0.2) * 10 / 8
        val range60 = -0.068599 + 0.34751 * safeSoE * 100 + 0.0025607 * safeSoE * safeSoE * 100 * 100
        val soR = range60 / 60.29f * 100f

        return soR.toFloat()
    }

    private fun getSoR(wheel: WheelEntity, voltage: Float): Float {
        val numberOfCellsPerPack = wheel.voltageMax / 4.2f
        val averageCell = voltage / numberOfCellsPerPack
        val averageCellInt = round(averageCell * 1000, 0).toInt()

        var upperRow = ENERGY_PER_CELL_VOLTAGE[0]
        for (row in ENERGY_PER_CELL_VOLTAGE) {
            if (averageCellInt > row[0]) {
                return prorateSoR(averageCellInt, upperRow, row)
            }

            upperRow = row
        }

        return 0f
    }

    private fun prorateSoR(averageCellInt: Int, upperRow: Array<Int>, lowerRow: Array<Int>): Float {
        val upperSoR = calculateSoR(upperRow)
        val lowerSoR = calculateSoR(lowerRow)

        val diffCellBetweenRows = upperRow[0] - lowerRow[0]
        val diffActual = averageCellInt - lowerRow[0]
        val percentActual = diffActual.toFloat() / diffCellBetweenRows.toFloat()

        val diffUpperToLowerSoR = upperSoR - lowerSoR
        val diffSoR = diffUpperToLowerSoR * percentActual

        return lowerSoR + diffSoR
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
