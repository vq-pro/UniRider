package quebec.virtualite.unirider.services

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.test.domain.TestConstants.S18_1_CONNECTED
import quebec.virtualite.unirider.test.domain.TestConstants.SHERMAN_L_5
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_INITIAL
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_INITIAL5
import java.util.stream.Stream

@ExtendWith(MockitoExtension::class)
class CalculatorServiceTest
{
    @InjectMocks
    lateinit var service: CalculatorService

    @ParameterizedTest
    @MethodSource("estimatedValues")
    fun estimatedValues(
        wheel: WheelEntity, voltageInitial: Float, voltageActual: Float, km: Float,
        expectedCharge: Float, expectedRemainingRange: Float, expectedTotalRange: Float
    )
    {
        // When
        val values = service.estimatedValues(wheel.copy(voltageInitial = voltageInitial), voltageActual, km)

        // Then
        assertThat(values?.charge).isEqualTo(expectedCharge)
        assertThat(values?.remainingRange).isEqualTo(expectedRemainingRange)
        assertThat(values?.totalRange).isEqualTo(expectedTotalRange)
    }

    @ParameterizedTest
    @MethodSource("estimatedValues_invalid")
    fun estimatedValues_invalid(wheel: WheelEntity, voltageInitial: Float, voltageActual: Float, km: Float)
    {
        // When
        val values = service.estimatedValues(wheel.copy(voltageInitial = voltageInitial), voltageActual, km)

        // Then
        assertThat(values).isEqualTo(null)
    }

    @ParameterizedTest
    @MethodSource("percentage")
    fun percentage(wheel: WheelEntity, voltage: Float, expectedPercentage: Float)
    {
        // When
        val percentage = service.percentage(wheel, voltage)

        // Then
        assertThat(percentage).isEqualTo(expectedPercentage)
    }

    @ParameterizedTest
    @MethodSource("requiredVoltageOffCharger")
    fun requiredVoltageOffCharger(voltage: Float, km: Float, kmRequested: Float, expectedRequiredVoltage: Float)
    {
        // When
        val result = service.requiredVoltageOffCharger(WHEEL, voltage, km, kmRequested)

        // Then
        assertThat(result).isEqualTo(expectedRequiredVoltage)
    }

    @Test
    fun requiredVoltageOnChargerFull()
    {
        // When
        val result = service.requiredVoltageFull(WHEEL)

        // Then
        assertThat(result).isEqualTo(WHEEL.voltageFull)
    }

    companion object
    {
        val WHEEL = SHERMAN_L_5

        @JvmStatic
        fun estimatedValues(): Stream<Arguments>
        {
            val lowerVoltageInitial = 146.4f
            return Stream.of(
                Arguments.of(SHERMAN_L_5, VOLTAGE_INITIAL5, 145.9f, 2.5f, 95.7f, 55.6f, 58.1f),
                Arguments.of(SHERMAN_L_5, VOLTAGE_INITIAL5, 141.1f, 20.0f, 77.2f, 67.7f, 87.7f),
                Arguments.of(SHERMAN_L_5, VOLTAGE_INITIAL5, 140.0f, 20.0f, 71f, 49.0f, 69.0f),
                Arguments.of(SHERMAN_L_5, VOLTAGE_INITIAL5, 137.1f, 30.0f, 50.5f, 30.6f, 60.6f),
                Arguments.of(SHERMAN_L_5, VOLTAGE_INITIAL5, 133.0f, 39.9f, 32.6f, 19.3f, 59.2f),
                Arguments.of(SHERMAN_L_5, VOLTAGE_INITIAL5, 132.0f, 39.9f, 29.3f, 16.5f, 56.4f),
                Arguments.of(SHERMAN_L_5, VOLTAGE_INITIAL5, 125.6f, 65.0f, 12.3f, 9.1f, 74.1f),
                Arguments.of(S18_1_CONNECTED, VOLTAGE_INITIAL, 76.2f, 15.0f, 53.3f, 17.1f, 32.1f),

                // Voltage initial lower after high-speed charging
                Arguments.of(SHERMAN_L_5, lowerVoltageInitial, 140.0f, 20.0f, 73f, 54.2f, 74.2f),
                Arguments.of(SHERMAN_L_5, lowerVoltageInitial, 140.0f, 18.6f, 73f, 50.4f, 69.0f),
                Arguments.of(SHERMAN_L_5, lowerVoltageInitial, 138.0f, 20.0f, 58.1f, 27.8f, 47.8f),
                Arguments.of(SHERMAN_L_5, lowerVoltageInitial, 132.0f, 43.0f, 30.1f, 18.5f, 61.5f),

                // Voltage lower than reserve
                Arguments.of(SHERMAN_L_5, VOLTAGE_INITIAL5, 119.5f, 60f, 0f, 0f, 60f),
                Arguments.of(S18_1_CONNECTED, VOLTAGE_INITIAL, 66.5f, 20f, 0f, 0f, 20f)
            )
        }

        @JvmStatic
        fun estimatedValues_invalid(): Stream<Arguments>
        {
            return Stream.of(
                // Same voltage, little distance (should not be possible)
                Arguments.of(SHERMAN_L_5, VOLTAGE_INITIAL5, VOLTAGE_INITIAL5, 2.5f),

                // Voltage higher than max
                Arguments.of(SHERMAN_L_5, VOLTAGE_INITIAL5, 151.3f, 3f)
            )
        }

        @JvmStatic
        fun percentage(): Stream<Arguments>
        {
            return Stream.of(
                Arguments.of(SHERMAN_L_5, 151.2f, 100.0f),
                Arguments.of(SHERMAN_L_5, 119.2f, 0.0f),
                Arguments.of(SHERMAN_L_5, 136.9f, 44.6f),
                Arguments.of(SHERMAN_L_5, 141.1f, 69.7f),

                // Voltage lower than reserve
                Arguments.of(SHERMAN_L_5, 119.0f, 0.0f),
                Arguments.of(S18_1_CONNECTED, 66.0f, 0.0f),

                // Voltage higher than max
                Arguments.of(SHERMAN_L_5, 151.3f, 100f),
            )
        }

        @JvmStatic
        fun requiredVoltageOffCharger(): Stream<Arguments>
        {
            return Stream.of(
                Arguments.of(136.9f, 30f, 0f, 136.9f),
                Arguments.of(136.9f, 30f, 50f, 145.2f),
                Arguments.of(129f, 40f, 25f, 137.8f),
                Arguments.of(131f, 30f, 200f, WHEEL.voltageFull),
                Arguments.of(VOLTAGE_INITIAL5, 3f, 0f, WHEEL.voltageFull),
            )
        }
    }
}
