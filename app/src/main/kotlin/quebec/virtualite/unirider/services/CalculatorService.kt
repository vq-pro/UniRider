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

    private val SOR_PER_CELL = arrayOf(
        SoRPerCell(4200, 100f),
        SoRPerCell(4025, 95.9098f),
        SoRPerCell(4000, 92.0567f),
        SoRPerCell(3985, 88.2923f),
        SoRPerCell(3975, 84.6061f),
        SoRPerCell(3960, 80.9930f),
        SoRPerCell(3950, 77.4569f),
        SoRPerCell(3940, 73.9926f),
        SoRPerCell(3930, 70.5999f),
        SoRPerCell(3920, 67.2780f),
        SoRPerCell(3910, 64.0267f),
        SoRPerCell(3890, 60.8454f),
        SoRPerCell(3860, 57.7415f),
        SoRPerCell(3840, 54.7218f),
        SoRPerCell(3810, 51.7774f),
        SoRPerCell(3795, 48.9148f),
        SoRPerCell(3780, 46.1216f),
        SoRPerCell(3760, 43.3972f),
        SoRPerCell(3740, 40.7443f),
        SoRPerCell(3730, 38.1621f),
        SoRPerCell(3710, 35.6430f),
        SoRPerCell(3695, 33.1931f),
        SoRPerCell(3680, 30.8081f),
        SoRPerCell(3650, 28.4876f),
        SoRPerCell(3635, 26.2400f),
        SoRPerCell(3615, 24.0549f),
        SoRPerCell(3595, 21.9348f),
        SoRPerCell(3575, 19.8786f),
        SoRPerCell(3550, 17.8855f),
        SoRPerCell(3530, 15.9573f),
        SoRPerCell(3505, 14.0904f),
        SoRPerCell(3490, 12.2864f),
        SoRPerCell(3480, 10.5393f),
        SoRPerCell(3450, 8.8462f),
        SoRPerCell(3430, 7.2158f),
        SoRPerCell(3415, 5.6425f),
        SoRPerCell(3400, 4.1232f),
        SoRPerCell(3380, 2.6572f),
        SoRPerCell(3350, 1.2461f),
        SoRPerCell(3310, -0.1070f)
    )

    open fun estimatedValues(wheel: WheelEntity, voltage: Float, km: Float): EstimatedValues {
        val sor = getSoR(wheel, voltage)
        if (sor == -1f)
            return EstimatedValues(-1f, -1f, 0f)

        val totalRange = 100 * km / (100 - sor)
        val remainingRange = totalRange - km

        return EstimatedValues(
            round(remainingRange, NB_DECIMALS),
            round(totalRange, NB_DECIMALS),
            0f
        )
    }

    open fun percentage(wheel: WheelEntity?, voltage: Float): Float {
        val soR = getSoR(wheel!!, voltage)
        return when {
            soR == -1f -> 100f
            else -> round(soR, NB_DECIMALS)
        }
    }

    open fun requiredFullVoltage(wheel: WheelEntity?): Float {
        return (wheel!!).voltageFull
    }

    //    FIXME-1 Implement using km, voltage, and km desired
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
        if (voltage < wheel.voltageReserve)
            return 0f

        if (voltage > wheel.voltageMax)
            return -1f

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
