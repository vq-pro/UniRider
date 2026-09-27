package quebec.virtualite.unirider.services

import quebec.virtualite.commons.android.utils.NumberUtils.round
import quebec.virtualite.unirider.database.WheelEntity

class CalculatorService
{
    data class EstimatedValues(
        val remainingRange: Float,
        val totalRange: Float
    )

    private val calculator: Calculator = SoRperCells()
//    private val calculator: Calculator = CalculatorTable()

    fun estimatedValues(wheel: WheelEntity, voltage: Float, km: Float): EstimatedValues
    {
        val soE = calculator.soE(cellVoltage(wheel, voltage))
        if (soE == -1f)
            return EstimatedValues(-1f, -1f)

        var totalRange = 100 * km / (100 - soE)
        var remainingRange = totalRange - km

        if (remainingRange < 1.0f)
        {
            totalRange = km
            remainingRange = 0f
        }

        return EstimatedValues(
            round(remainingRange),
            round(totalRange)
        )
    }

    fun percentage(wheel: WheelEntity, voltage: Float): Float
    {
        val soE = calculator.soE(cellVoltage(wheel, voltage))
        return when
        {
            soE == -1f -> 100f
            else -> round(soE)
        }
    }

    fun requiredVoltageFull(wheel: WheelEntity) = wheel.voltageFull

    fun requiredVoltageOffCharger(wheel: WheelEntity, voltage: Float, km: Float, kmRequested: Float): Float
    {
        val estimatedTotalRange = estimatedValues(wheel, voltage, km).totalRange
        return when
        {
            kmRequested >= estimatedTotalRange -> wheel.voltageFull
            kmRequested <= 0.01f -> voltage
            else ->
            {
                val soE = kmRequested / estimatedTotalRange * 100
                round(wheelVoltage(wheel, calculator.voltage(soE)))
            }
        }
    }

    private fun cellVoltage(wheel: WheelEntity, wheelVoltage: Float): Float = wheelVoltage / cellsPerPack(wheel)

    private fun cellsPerPack(wheel: WheelEntity): Float = wheel.voltageMax / 4.2f

    private fun wheelVoltage(wheel: WheelEntity, cellVoltage: Float): Float = cellVoltage * cellsPerPack(wheel)
}
