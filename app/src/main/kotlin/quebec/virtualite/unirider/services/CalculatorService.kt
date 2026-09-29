package quebec.virtualite.unirider.services

import quebec.virtualite.commons.android.utils.NumberUtils.round
import quebec.virtualite.unirider.database.WheelEntity

class CalculatorService
{
    data class EstimatedValues(
        val charge: Float,
        val remainingRange: Float,
        val totalRange: Float
    )

    private val calculator = CalculatorTable()

    fun estimatedValues(wheel: WheelEntity, voltage: Float, km: Float): EstimatedValues?
    {
        if (voltage > wheel.voltageInitial)
            return null

        val cellVoltageInitial = cellVoltage(wheel, wheel.voltageInitial)
        val cellVoltageActual = cellVoltage(wheel, voltage)

        val initialSoe = calculator.soE(cellVoltageInitial)
        val actualSoe = calculator.soE(cellVoltageActual)
        val usedSoe = initialSoe - actualSoe
        if (usedSoe < 2)
            return null

        val remainingRange = km * actualSoe / usedSoe
        val totalRange = km + remainingRange
        val charge = remainingRange * 100 / totalRange

        return EstimatedValues(round(charge), round(remainingRange), round(totalRange))
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
        val estimatedValues = estimatedValues(wheel, voltage, km)
            ?: return wheel.voltageFull

        val estimatedTotalRange = estimatedValues.totalRange
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
