package quebec.virtualite.unirider.services

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.commons.android.utils.NumberUtils.round
import quebec.virtualite.unirider.TestDomain.CHARGER_OFFSET
import quebec.virtualite.unirider.TestDomain.CHARGE_RATE
import quebec.virtualite.unirider.TestDomain.DEVICE_ADDR
import quebec.virtualite.unirider.TestDomain.DEVICE_NAME
import quebec.virtualite.unirider.TestDomain.MILEAGE
import quebec.virtualite.unirider.TestDomain.NAME
import quebec.virtualite.unirider.TestDomain.NOT_SOLD
import quebec.virtualite.unirider.TestDomain.PREMILEAGE
import quebec.virtualite.unirider.TestDomain.SHERMAN_L_5
import quebec.virtualite.unirider.TestDomain.VOLTAGE_FULL
import quebec.virtualite.unirider.TestDomain.WH
import quebec.virtualite.unirider.database.WheelEntity

@RunWith(MockitoJUnitRunner::class)
class CalculatorServiceTest {

    @InjectMocks
    lateinit var service: CalculatorService

    private val WHEEL = SHERMAN_L_5

    @Test
    fun adjustedReserve() {
        // When
        val result = service.adjustedReserve(WHEEL)

        // Then
        assertThat(result, equalTo(WHEEL.voltageReserve + 2))
    }

    @Ignore
    @Test
    fun estimatedAndRequired() {
        estimatedValues(91.9f, 38f, 43.2f, 81.2f)
        requiredVoltage(100.6f, 32.7f, 43f, round(91.8f + WHEEL.chargerOffset, 1))
        requiredVoltage(100.6f, 32.7f, 40f, round(91.2f + WHEEL.chargerOffset, 1))
    }

    @Test
    fun estimatedValues() {
        estimatedValues(141.12f, 19.4f, 39.9f, 59.3f);
        estimatedValues(133.02f, 39.9f, 19.8f, 59.7f);
        estimatedValues(132f, 39.9f, 16.9f, 56.8f);
        estimatedValues(125.64f, 65f, 9.1f, 74.1f);
//        estimatedValues(100.4f, 83.5f, 81f, null, 7.2f, 88.2f, 29.8f)
//        estimatedValues(98.2f, 91.0f, 42f, null, 52.5f, 94.5f, 24.5f)
//        estimatedValues(100.8f, 83.5f, 81f, null, 7.0f, 88.0f, 30.5f)
//        estimatedValues(100.6f, 91.9f, 40f, 35f, 40.4f, 80.4f, 31.1f)
//        estimatedValues(100.4f, 83.5f, 81f, 35f, 6.1f, 87.1f, 29.8f)
//        estimatedValues(98.2f, 91.0f, 42f, 35f, 36.7f, 78.7f, 24.5f)
//        estimatedValues(100.8f, 83.5f, 81f, 35f, 6.1f, 87.1f, 30.5f)

        // Voltage lower than reserve
//        estimatedValues(100.8f, 79.5f, 81.2f, null, 0f, 81.2f, 37.5f)

        // Voltage higher than max
//        estimatedValues(101.6f, 100.9f, 3f, null, 81.0f, 84.0f, 33.3f)
    }

    private fun estimatedValues(
        voltageActual: Float,
        km: Float,
        expectedRemainingRange: Float,
        expectedTotalRange: Float
    ) {
        // When
        val values = service.estimatedValues(WHEEL.copy(), voltageActual, km)

        // Then
        assertThat(values.remainingRange, equalTo(expectedRemainingRange))
        assertThat(values.totalRange, equalTo(expectedTotalRange))
    }

    @Test
    fun percentage() {
        percentage(151.2f, 100.0f)
        percentage(104.4f, 0.0f)
        percentage(127.8f, 50.0f)
        percentage(135.8f, 67.1f)
        percentage(152.7f, 103.2f)
    }

    private fun percentage(voltage: Float, expectedPercentage: Float) {
        // When
        val percentage = service.percentage(WHEEL, voltage)

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
            WheelEntity(
                0, NAME, DEVICE_NAME, DEVICE_ADDR,
                PREMILEAGE, MILEAGE, WH,
                voltageMax, voltageMin, 1f, voltageMax,
                CHARGE_RATE, VOLTAGE_FULL, CHARGER_OFFSET, NOT_SOLD
            )

        // When
        val percentage = service.percentage(wheel, 108.0f)

        // Then
        assertThat(percentage, equalTo(0.0f))
    }

    @Ignore
    @Test
    fun requiredVoltage() {
        requiredVoltage(151.2f, 25f, 20f, 85.5f + WHEEL.chargerOffset)
        requiredVoltage(151.2f, 35f, 40f, 91.8f + WHEEL.chargerOffset)
        requiredVoltage(151.2f, 25f, 200f, WHEEL.voltageFull)
    }

    private fun requiredVoltage(voltageStart: Float, whPerKm: Float, km: Float, expectedRequiredVoltage: Float) {
        // When
        val result = service.requiredVoltage(WHEEL.copy(voltageStart = voltageStart), whPerKm, km)

        // Then
        assertThat(result, equalTo(expectedRequiredVoltage))
    }

    @Test
    fun requiredFullVoltage() {
        // When
        val result = service.requiredFullVoltage(WHEEL)

        // Then
        assertThat(result, equalTo(WHEEL.voltageFull))
    }
}
