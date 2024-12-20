package quebec.virtualite.unirider.services

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.TestDomain.S18_1
import quebec.virtualite.unirider.TestDomain.SHERMAN_L_5
import quebec.virtualite.unirider.database.WheelEntity

@RunWith(MockitoJUnitRunner::class)
class CalculatorServiceTest {

    @InjectMocks
    lateinit var service: CalculatorService

    private val WHEEL = SHERMAN_L_5

    @Test
    fun estimatedValues() {
        estimatedValues(SHERMAN_L_5, 141.1f, 20.0f, 40.5f, 60.5f);
        estimatedValues(SHERMAN_L_5, 133.0f, 39.9f, 19.7f, 59.6f);
        estimatedValues(SHERMAN_L_5, 132.0f, 39.9f, 16.9f, 56.8f);
        estimatedValues(SHERMAN_L_5, 125.6f, 65.0f, 9.0f, 74.0f);
        estimatedValues(S18_1, 76.2f, 15.0f, 16.1f, 31.1f);

        // Voltage lower than reserve
        estimatedValues(SHERMAN_L_5, 119.5f, 60f, 0f, 60f)
        estimatedValues(S18_1, 67.5f, 20f, 0f, 20f);

        // Voltage higher than max
        estimatedValues(SHERMAN_L_5, 151.3f, 3f, -1f, -1f)
    }

    private fun estimatedValues(
        wheel: WheelEntity,
        voltageActual: Float,
        km: Float,
        expectedRemainingRange: Float,
        expectedTotalRange: Float
    ) {
        // When
        val values = service.estimatedValues(wheel, voltageActual, km)

        // Then
        assertThat(values.remainingRange, equalTo(expectedRemainingRange))
        assertThat(values.totalRange, equalTo(expectedTotalRange))
    }

    @Test
    fun percentage() {
        percentage(SHERMAN_L_5, 151.2f, 100.0f)
        percentage(SHERMAN_L_5, 119.2f, 0.0f)
        percentage(SHERMAN_L_5, 136.9f, 50.4f)
        percentage(SHERMAN_L_5, 141.1f, 67.0f)

        // Voltage lower than reserve
        percentage(SHERMAN_L_5, 119.0f, 0.0f)
        percentage(S18_1, 66.0f, 0.0f)

        // Voltage higher than max
        percentage(SHERMAN_L_5, 151.3f, 100f)
    }

    private fun percentage(wheel: WheelEntity, voltage: Float, expectedPercentage: Float) {
        // When
        val percentage = service.percentage(wheel, voltage)

        // Then
        assertThat(percentage, equalTo(expectedPercentage))
    }

    @Test
    fun requiredVoltage() {
        requiredVoltage(136.9f, 30f, 50f, 142.8f + WHEEL.chargerOffset)
        requiredVoltage(129f, 40f, 25f, 136.7f + WHEEL.chargerOffset)
        requiredVoltage(131f, 30f, 200f, WHEEL.voltageFull)
    }

    private fun requiredVoltage(voltage: Float, km: Float, kmRequested: Float, expectedRequiredVoltage: Float) {
        // When
        val result = service.requiredVoltage(WHEEL, voltage, km, kmRequested)

        // Then
        assertThat(result, equalTo(expectedRequiredVoltage))
    }

    @Test
    fun requiredVoltageFull() {
        // When
        val result = service.requiredVoltageFull(WHEEL)

        // Then
        assertThat(result, equalTo(WHEEL.voltageFull))
    }
}
