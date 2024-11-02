package quebec.virtualite.unirider.services

import quebec.virtualite.commons.android.utils.NumberUtils.round
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.views.NB_DECIMALS

open class CalculatorService {

    data class EstimatedValues(
        val remainingRange: Float,
        val totalRange: Float
    )

    private val soRperCells = SoRperCells()

    open fun estimatedValues(wheel: WheelEntity, voltage: Float, km: Float): EstimatedValues {
        val sor = soRperCells.voltageToSoR(wheel, voltage)
        if (sor == -1f)
            return EstimatedValues(-1f, -1f)

        var totalRange = 100 * km / (100 - sor)
        var remainingRange = totalRange - km

        if (remainingRange < 1.0f) {
            totalRange = km
            remainingRange = 0f
        }

        return EstimatedValues(
            round(remainingRange, NB_DECIMALS),
            round(totalRange, NB_DECIMALS)
        )
    }

    open fun percentage(wheel: WheelEntity?, voltage: Float): Float {
        val soR = soRperCells.voltageToSoR(wheel!!, voltage)
        return when {
            soR == -1f -> 100f
            else -> round(soR, NB_DECIMALS)
        }
    }

    open fun requiredVoltage(wheel: WheelEntity?, voltage: Float, km: Float, kmRequested: Float): Float {
        val estimatedTotalRange = estimatedValues(wheel!!, voltage, km).totalRange
        if (kmRequested >= estimatedTotalRange) {
            return wheel.voltageFull
        }

        val soRRequested = kmRequested / estimatedTotalRange * 100

        return soRperCells.soRtoVoltage(wheel, soRRequested);
    }

    open fun requiredVoltageFull(wheel: WheelEntity?): Float {
        return (wheel!!).voltageFull
    }
}
