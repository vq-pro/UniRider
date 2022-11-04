package quebec.virtualite.unirider.services

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.TestDomain.CHARGE_RATE
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

    // FIXME-1 Adjust value to 1.5V more instead of 2V more
    @Test
    fun adjustedReserve() {
        // When
        val result = service.adjustedReserve(SHERMAN_MAX_3)

        // Then
        assertThat(result, equalTo(SHERMAN_MAX_3.voltageReserve + 2))
    }

    @Test
    fun estimatedAndRequired() {
        estimatedValues(100.6f, 91.9f, 38f, 43.2f, 81.2f, 32.7f)
        requiredVoltage(100.6f, 32.7f, 43f, 91.8f + CHARGER_OFFSET)
        requiredVoltage(100.6f, 32.7f, 40f, 91.2f + CHARGER_OFFSET)
    }

    @Test
    fun estimatedValues() {
        estimatedValues(100.6f, 91.9f, 40f, 45.5f, 85.5f, 31.1f)
        estimatedValues(100.4f, 83.5f, 81f, 7.2f, 88.2f, 29.8f)
        estimatedValues(98.2f, 91.0f, 42f, 52.5f, 94.5f, 24.5f)
        estimatedValues(100.8f, 83.5f, 81f, 7.0f, 88.0f, 30.5f)

        // Voltage lower than reserve
        estimatedValues(100.8f, 79.5f, 81.2f, 0f, 81.2f, 37.5f)
    }

    private fun estimatedValues(
        voltageStart: Float,
        voltageActual: Float,
        km: Float,
        expectedRemainingRange: Float,
        expectedTotalRange: Float,
        expectedWhPerKm: Float
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
        val wheel = WheelEntity(0, NAME, DEVICE_NAME, DEVICE_ADDR, PREMILEAGE, MILEAGE, WH, voltageMax, voltageMin, 1f, voltageMax, CHARGE_RATE)

        // When
        val percentage = service.percentage(wheel, 108.0f)

        // Then
        assertThat(percentage, equalTo(0.0f))
    }

    @Test
    fun requiredVoltage() {
        requiredVoltage(100.8f, 25f, 20f, 85.5f + CHARGER_OFFSET)
        requiredVoltage(100.8f, 35f, 40f, 91.8f + CHARGER_OFFSET)
        requiredVoltage(100.8f, 25f, 200f, SHERMAN_MAX_3.voltageMax - CHARGER_OFFSET)
    }

    private fun requiredVoltage(voltageStart: Float, whPerKm: Float, km: Float, expectedRequiredVoltage: Float) {
        // When
        val requiredVoltage = service.requiredVoltage(SHERMAN_MAX_3.copy(voltageStart = voltageStart), whPerKm, km)

        // Then
        assertThat(requiredVoltage, equalTo(expectedRequiredVoltage))
    }
}
