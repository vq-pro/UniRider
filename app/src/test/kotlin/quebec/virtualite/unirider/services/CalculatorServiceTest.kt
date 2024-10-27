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
import quebec.virtualite.unirider.TestDomain.S18_1
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
//        estimatedValues(91.9f, 38f, 43.2f, 81.2f)
        requiredVoltage(100.6f, 32.7f, 43f, round(91.8f + WHEEL.chargerOffset, 1))
        requiredVoltage(100.6f, 32.7f, 40f, round(91.2f + WHEEL.chargerOffset, 1))
    }

    @Test
    fun estimatedValues() {
        estimatedValues(SHERMAN_L_5, 141.1f, 19.4f, 39.6f, 59.0f);
        estimatedValues(SHERMAN_L_5, 133.0f, 39.9f, 19.7f, 59.6f);
        estimatedValues(SHERMAN_L_5, 132.0f, 39.9f, 16.9f, 56.8f);
        estimatedValues(SHERMAN_L_5, 125.6f, 65.0f, 9.0f, 74.0f);
        estimatedValues(S18_1, 76.2f, 15.0f, 14.5f, 29.5f);

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
