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
import quebec.virtualite.unirider.TestDomain.NOT_DELETED
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
    fun adjustedReserve() {
        // When
        val result = service.adjustedReserve(SHERMAN_MAX_3)

        // Then
        assertThat(result, equalTo(SHERMAN_MAX_3.voltageReserve + 2))
    }

    @Test
    fun estimatedAndRequired() {
        estimatedValues(100.6f, 91.9f, 38f, null, 43.2f, 81.2f, 32.7f)
        requiredVoltage(100.6f, 32.7f, 43f, 91.8f + CHARGER_OFFSET)
        requiredVoltage(100.6f, 32.7f, 40f, 91.2f + CHARGER_OFFSET)
    }

    @Test
    fun estimatedValues() {
        estimatedValues(100.6f, 91.9f, 40f, null, 45.5f, 85.5f, 31.1f)
        estimatedValues(100.4f, 83.5f, 81f, null, 7.2f, 88.2f, 29.8f)
        estimatedValues(98.2f, 91.0f, 42f, null, 52.5f, 94.5f, 24.5f)
        estimatedValues(100.8f, 83.5f, 81f, null, 7.0f, 88.0f, 30.5f)
        estimatedValues(100.6f, 91.9f, 40f, 35f, 40.4f, 80.4f, 31.1f)
        estimatedValues(100.4f, 83.5f, 81f, 35f, 6.1f, 87.1f, 29.8f)
        estimatedValues(98.2f, 91.0f, 42f, 35f, 36.7f, 78.7f, 24.5f)
        estimatedValues(100.8f, 83.5f, 81f, 35f, 6.1f, 87.1f, 30.5f)

        // Voltage lower than reserve
        estimatedValues(100.8f, 79.5f, 81.2f, null, 0f, 81.2f, 37.5f)

        // Voltage higher than max
        estimatedValues(101.6f, 100.9f, 3f, null, 81.0f, 84.0f, 33.3f)
    }

    private fun estimatedValues(
        voltageStart: Float,
        voltageActual: Float,
        km: Float,
        rateOverride: Float?,
        expectedRemainingRange: Float,
        expectedTotalRange: Float,
        expectedWhPerKm: Float
    ) {
        // Given
        val wheel = SHERMAN_MAX_3.copy(voltageStart = voltageStart)

        // When
        val values = service.estimatedValues(wheel, voltageActual, km, rateOverride)

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
        percentage(101.6f, 103.2f)
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
        val wheel =
            WheelEntity(0, NAME, DEVICE_NAME, DEVICE_ADDR, PREMILEAGE, MILEAGE, WH, voltageMax, voltageMin, 1f, voltageMax, CHARGE_RATE, NOT_DELETED)

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
