package quebec.virtualite.unirider.services

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.TestDomain.DEVICE_ADDR
import quebec.virtualite.unirider.TestDomain.DEVICE_NAME
import quebec.virtualite.unirider.TestDomain.MILEAGE
import quebec.virtualite.unirider.TestDomain.NAME
import quebec.virtualite.unirider.TestDomain.PREMILEAGE
import quebec.virtualite.unirider.TestDomain.SHERMAN_MAX_3
import quebec.virtualite.unirider.TestDomain.WH
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.services.CalculatorService.Companion.CHARGER_OFFSET

@RunWith(MockitoJUnitRunner::class)
class CalculatorServiceTest {

    @InjectMocks
    lateinit var service: CalculatorService

    @Test
    fun convertConsumption() {
        convertConsumption(29.8f, 25)
        convertConsumption(29.9f, 25)
        convertConsumption(30.0f, 30)
        convertConsumption(30.1f, 30)
        convertConsumption(30.2f, 30)
        convertConsumption(32.5f, 30)
        convertConsumption(16.8f, 15)
    }

    private fun convertConsumption(rawConsumption: Float, expectedConsumption: Int) {
        // When
        val result = service.convertConsumption(rawConsumption)

        // Then
        assertThat(result, equalTo(expectedConsumption))
    }

    @Test
    fun estimatedValues() {
        estimatedValues(100.4f, 91.9f, 42f, 56.7f, 98.7f, 25)
        estimatedValues(98.2f, 91.0f, 42f, 62.9f, 104.9f, 20)

        estimatedValues(100.8f, 83.5f, 81f, 14.3f, 95.3f, 30)
        estimatedValues(100.4f, 83.5f, 81f, 16.7f, 97.7f, 25)

        // Voltage lower than reserve
        estimatedValues(100.8f, 79.5f, 81.2f, 0f, 81.2f, 35)
    }

    private fun estimatedValues(
        voltageStart: Float,
        voltageActual: Float,
        km: Float,
        expectedRemainingRange: Float,
        expectedTotalRange: Float,
        expectedWhPerKm: Int
    ) {
        // When
        val values = service.estimatedValues(
            SHERMAN_MAX_3.copy(voltageStart = voltageStart), voltageActual, km
        )

        // Then
        assertThat(values.remainingRange, equalTo(expectedRemainingRange))
        assertThat(values.totalRange, equalTo(expectedTotalRange))
        assertThat(values.whPerKm, equalTo(expectedWhPerKm))
    }

    @Test
    fun percentage() {
        percentage(100.8f, 100.0f)
        percentage(75.6f, 0.0f)
        percentage(88.2f, 50.0f)
        percentage(92.5f, 67.1f)
    }

    private fun percentage(voltage: Float, expectedPercentage: Float) {
        // When
        val percentage = service.percentage(SHERMAN_MAX_3, voltage)

        // Then
        assertThat(percentage, equalTo(expectedPercentage))
    }

    @Test
    fun percentage_whenVoltagesNotSet_zero() {
        percentage_whenVoltagesNotSet_zero(0f, 1f)
        percentage_whenVoltagesNotSet_zero(-1f, 1f)
        percentage_whenVoltagesNotSet_zero(-100f, 1f)
        percentage_whenVoltagesNotSet_zero(1f, 0f)
        percentage_whenVoltagesNotSet_zero(1f, -1f)
        percentage_whenVoltagesNotSet_zero(1f, -100f)
    }

    private fun percentage_whenVoltagesNotSet_zero(voltageMin: Float, voltageMax: Float) {
        // Given
        val wheel = WheelEntity(0, NAME, DEVICE_NAME, DEVICE_ADDR, PREMILEAGE, MILEAGE, WH, voltageMax, voltageMin, 1f, voltageMax)

        // When
        val percentage = service.percentage(wheel, 108.0f)

        // Then
        assertThat(percentage, equalTo(0.0f))
    }

    @Test
    fun requiredVoltage() {
        requiredVoltage(25, 20f, 84.2f + CHARGER_OFFSET)
        requiredVoltage(35, 40f, 91.2f + CHARGER_OFFSET)
        requiredVoltage(25, 200f, SHERMAN_MAX_3.voltageMax - CHARGER_OFFSET)
    }

    private fun requiredVoltage(whPerKm: Int, km: Float, expectedRequiredVoltage: Float) {
        // When
        val requiredVoltage = service.requiredVoltage(SHERMAN_MAX_3, whPerKm, km)

        // Then
        assertThat(requiredVoltage, equalTo(expectedRequiredVoltage))
    }
}
