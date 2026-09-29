package quebec.virtualite.unirider.services

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.test.domain.TestConstants.S18_1_CONNECTED
import quebec.virtualite.unirider.test.domain.TestConstants.SHERMAN_L_5
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_INITIAL
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_INITIAL5

@RunWith(MockitoJUnitRunner::class)
class CalculatorServiceTest
{
    @InjectMocks
    lateinit var service: CalculatorService

    private val WHEEL = SHERMAN_L_5

    @Test
    fun estimatedValues()
    {
        estimatedValues(SHERMAN_L_5, VOLTAGE_INITIAL5, 145.9f, 2.5f, 95.7f, 55.6f, 58.1f)
        estimatedValues(SHERMAN_L_5, VOLTAGE_INITIAL5, 141.1f, 20.0f, 77.2f, 67.7f, 87.7f)
        estimatedValues(SHERMAN_L_5, VOLTAGE_INITIAL5, 140.0f, 20.0f, 71f, 49.0f, 69.0f)
        estimatedValues(SHERMAN_L_5, VOLTAGE_INITIAL5, 137.1f, 30.0f, 50.5f, 30.6f, 60.6f)
        estimatedValues(SHERMAN_L_5, VOLTAGE_INITIAL5, 133.0f, 39.9f, 32.6f, 19.3f, 59.2f)
        estimatedValues(SHERMAN_L_5, VOLTAGE_INITIAL5, 132.0f, 39.9f, 29.3f, 16.5f, 56.4f)
        estimatedValues(SHERMAN_L_5, VOLTAGE_INITIAL5, 125.6f, 65.0f, 12.3f, 9.1f, 74.1f)
        estimatedValues(S18_1_CONNECTED, VOLTAGE_INITIAL, 76.2f, 15.0f, 53.3f, 17.1f, 32.1f)

        // Voltage initial lower after high-speed charging
        val lowerVoltageInitial = 146.4f
        estimatedValues(SHERMAN_L_5, lowerVoltageInitial, 140.0f, 20.0f, 73f, 54.2f, 74.2f)
        estimatedValues(SHERMAN_L_5, lowerVoltageInitial, 140.0f, 18.6f, 73f, 50.4f, 69.0f)
        estimatedValues(SHERMAN_L_5, lowerVoltageInitial, 138.0f, 20.0f, 58.1f, 27.8f, 47.8f)
        estimatedValues(SHERMAN_L_5, lowerVoltageInitial, 132.0f, 43.0f, 30.1f, 18.5f, 61.5f)

        // Voltage lower than reserve
        estimatedValues(SHERMAN_L_5, VOLTAGE_INITIAL5, 119.5f, 60f, 0f, 0f, 60f)
        estimatedValues(S18_1_CONNECTED, VOLTAGE_INITIAL, 66.5f, 20f, 0f, 0f, 20f)
    }

    @Test
    fun estimatedValues_invalid()
    {
        // Same voltage, little distance (should not be possible)
        estimatedValuesInvalid(SHERMAN_L_5, VOLTAGE_INITIAL5, VOLTAGE_INITIAL5, 2.5f)

        // Voltage higher than max
        estimatedValuesInvalid(SHERMAN_L_5, VOLTAGE_INITIAL5, 151.3f, 3f)
    }

    private fun estimatedValues(
        wheel: WheelEntity,
        voltageInitial: Float,
        voltageActual: Float,
        km: Float,
        expectedCharge: Float,
        expectedRemainingRange: Float,
        expectedTotalRange: Float
    )
    {
        // When
        val values = service.estimatedValues(wheel.copy(voltageInitial = voltageInitial), voltageActual, km)

        // Then
        assertThat(values?.charge, equalTo(expectedCharge))
        assertThat(values?.remainingRange, equalTo(expectedRemainingRange))
        assertThat(values?.totalRange, equalTo(expectedTotalRange))
    }

    private fun estimatedValuesInvalid(
        wheel: WheelEntity,
        voltageInitial: Float,
        voltageActual: Float,
        km: Float
    )
    {
        // When
        val values = service.estimatedValues(wheel.copy(voltageInitial = voltageInitial), voltageActual, km)

        // Then
        assertThat(values, equalTo(null))
    }

    @Test
    fun percentage()
    {
        percentage(SHERMAN_L_5, 151.2f, 100.0f)
        percentage(SHERMAN_L_5, 119.2f, 0.0f)
        percentage(SHERMAN_L_5, 136.9f, 44.6f)
        percentage(SHERMAN_L_5, 141.1f, 69.7f)

        // Voltage lower than reserve
        percentage(SHERMAN_L_5, 119.0f, 0.0f)
        percentage(S18_1_CONNECTED, 66.0f, 0.0f)

        // Voltage higher than max
        percentage(SHERMAN_L_5, 151.3f, 100f)
    }

    private fun percentage(wheel: WheelEntity, voltage: Float, expectedPercentage: Float)
    {
        // When
        val percentage = service.percentage(wheel, voltage)

        // Then
        assertThat(percentage, equalTo(expectedPercentage))
    }

    @Test
    fun requiredVoltageOffCharger()
    {
        requiredVoltageOffCharger(136.9f, 30f, 0f, 136.9f)
        requiredVoltageOffCharger(136.9f, 30f, 50f, 145.2f)
        requiredVoltageOffCharger(129f, 40f, 25f, 137.8f)
        requiredVoltageOffCharger(131f, 30f, 200f, WHEEL.voltageFull)
        requiredVoltageOffCharger(VOLTAGE_INITIAL5, 3f, 0f, WHEEL.voltageFull)
    }

    private fun requiredVoltageOffCharger(voltage: Float, km: Float, kmRequested: Float, expectedRequiredVoltage: Float)
    {
        // When
        val result = service.requiredVoltageOffCharger(WHEEL, voltage, km, kmRequested)

        // Then
        assertThat(result, equalTo(expectedRequiredVoltage))
    }

    @Test
    fun requiredVoltageOnChargerFull()
    {
        // When
        val result = service.requiredVoltageFull(WHEEL)

        // Then
        assertThat(result, equalTo(WHEEL.voltageFull))
    }
}
