package quebec.virtualite.unirider.views

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.test.domain.TestConstants.CHARGE_AMPERAGE_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.CHARGE_RATE_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.DISTANCE_OFFSET_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.MILEAGE_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.NAME_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.PREMILEAGE_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.S18_1_CONNECTED
import quebec.virtualite.unirider.test.domain.TestConstants.SOLD
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_FULL_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_MAX_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_MIN_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.WH_NEW
import java.util.stream.Stream

@ExtendWith(MockitoExtension::class)
class WheelValidatorTest
{
    @InjectMocks
    private lateinit var comparator: WheelValidator

    companion object
    {
        val WHEEL = S18_1_CONNECTED

        @JvmStatic
        fun canSave(): Stream<Arguments>
        {
            return Stream.of(
                // Name
                Arguments.of(WHEEL.copy(name = NAME_NEW)),

                // Pre-mileage
                Arguments.of(WHEEL.copy(premileage = PREMILEAGE_NEW)),
                Arguments.of(WHEEL.copy(premileage = 0)),

                // Mileage
                Arguments.of(WHEEL.copy(mileage = MILEAGE_NEW)),
                Arguments.of(WHEEL.copy(mileage = 0)),

                // Wh
                Arguments.of(WHEEL.copy(wh = WH_NEW)),

                // Voltage min
                Arguments.of(WHEEL.copy(voltageMin = VOLTAGE_MIN_NEW)),

                // Voltage max
                Arguments.of(WHEEL.copy(voltageMax = VOLTAGE_MAX_NEW)),

                // Charge amperage
                Arguments.of(WHEEL.copy(chargeAmperage = CHARGE_AMPERAGE_NEW)),

                // Charge rate
                Arguments.of(WHEEL.copy(chargeRate = CHARGE_RATE_NEW)),

                // Voltage full
                Arguments.of(WHEEL.copy(voltageFull = VOLTAGE_FULL_NEW)),

                // Distance offset
                Arguments.of(WHEEL.copy(distanceOffset = DISTANCE_OFFSET_NEW)),
                Arguments.of(WHEEL.copy(distanceOffset = 0f)),

                // Sold indicator
                Arguments.of(WHEEL.copy(isSold = SOLD))
            )
        }

        @JvmStatic
        fun cannotSave(): Stream<Arguments>
        {
            return Stream.of(
                // No change
                Arguments.of(WHEEL),

                // Name
                Arguments.of(WHEEL.copy(name = "")),

                // Wh
                Arguments.of(WHEEL.copy(wh = 0)),

                // Voltage min
                Arguments.of(WHEEL.copy(voltageMin = 0f)),
                Arguments.of(WHEEL.copy(voltageMin = WHEEL.voltageMax + 0.1f)),

                // Voltage max
                Arguments.of(WHEEL.copy(voltageMax = 0f)),

                // Charge amperage
                Arguments.of(WHEEL.copy(chargeAmperage = 0f)),

                // Charge rate
                Arguments.of(WHEEL.copy(chargeRate = 0f)),

                // Voltage full
                Arguments.of(WHEEL.copy(voltageFull = 0f)),
                Arguments.of(WHEEL.copy(voltageFull = WHEEL.voltageMax + 0.1f)),
                Arguments.of(WHEEL.copy(voltageFull = WHEEL.voltageMin - 0.1f))
            )
        }
    }

    @ParameterizedTest
    @MethodSource("canSave")
    fun canSave(updatedWheel: WheelEntity) = canSaveOrNot(updatedWheel, true)

    @ParameterizedTest
    @MethodSource("cannotSave")
    fun cannotSave(updatedWheel: WheelEntity) = canSaveOrNot(updatedWheel, false)

    private fun canSaveOrNot(updatedWheel: WheelEntity, expectedCanSave: Boolean)
    {
        // When
        val result = comparator.canSave(updatedWheel, WHEEL)

        // Then
        assertThat(result).isEqualTo(expectedCanSave)
    }
}
