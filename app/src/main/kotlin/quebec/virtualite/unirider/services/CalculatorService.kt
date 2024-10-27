package quebec.virtualite.unirider.services

import quebec.virtualite.commons.android.utils.NumberUtils.round
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.views.NB_DECIMALS
import java.lang.Float.min

open class CalculatorService {

    data class EstimatedValues(
        val remainingRange: Float, val totalRange: Float, val whPerKm: Float
    )

    data class SoRPerCell(
        val averageCell: Int, val soR: Float
    )

    //    FIXME-1 Complete table
    private val SOR_PER_CELL = arrayOf(
        SoRPerCell(4200, 100f),
        SoRPerCell(3920, 67.2780f),
        SoRPerCell(3810, 49.0909f),
        SoRPerCell(3795, 46.1040f),
        SoRPerCell(3695, 33.1931f),
        SoRPerCell(3680, 30.8081f),
        SoRPerCell(3650, 28.4876f),
        SoRPerCell(3505, 14.0904f),
        SoRPerCell(3490, 12.2864f),
        SoRPerCell(3480, 10.5393f),
        SoRPerCell(3430, 7.2158f)
    )

    open fun estimatedValues(wheel: WheelEntity, voltage: Float, km: Float): EstimatedValues {
        val sor = getSoR(wheel, voltage)
        if (sor == -1f) {
            return EstimatedValues(-1f, -1f, 0f)
        }

        val totalRange = 100 * km / (100 - sor)
        val remainingRange = totalRange - km

        return EstimatedValues(
            round(remainingRange, NB_DECIMALS), round(totalRange, NB_DECIMALS), 0f
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

    private fun getSoR(wheel: WheelEntity, voltage: Float): Float {
        val numberOfCellsPerPack = wheel.voltageMax / 4.2f
        val averageCellFloat = voltage / numberOfCellsPerPack
        val averageCellInt = round(averageCellFloat * 1000, 0).toInt()

        var upperRow = SOR_PER_CELL[0]
        for (row in SOR_PER_CELL) {
            if (averageCellInt > row.averageCell) {
                return prorateSoR(averageCellInt, upperRow, row)
            }

            upperRow = row
        }

        return 0f
    }

    private fun prorateSoR(averageCell: Int, upperRow: SoRPerCell, lowerRow: SoRPerCell): Float {
        if (upperRow == lowerRow) return -1f

        val diffCellBetweenRows = upperRow.averageCell - lowerRow.averageCell
        val diffActual = averageCell - lowerRow.averageCell
        val percentActual = diffActual.toFloat() / diffCellBetweenRows.toFloat()

        val diffUpperToLowerSoR = upperRow.soR - lowerRow.soR
        val diffSoR = diffUpperToLowerSoR * percentActual

        return lowerRow.soR + diffSoR
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
