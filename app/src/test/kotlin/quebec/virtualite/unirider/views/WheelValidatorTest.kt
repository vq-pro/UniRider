package quebec.virtualite.unirider.views

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.test.domain.TestConstants.CHARGE_RATE_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.DISTANCE_OFFSET_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.MILEAGE_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.NAME_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.PREMILEAGE_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.S18_1
import quebec.virtualite.unirider.test.domain.TestConstants.SOLD
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_FULL_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_MAX_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_MIN_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.WH_NEW

@RunWith(MockitoJUnitRunner::class)
class WheelValidatorTest {

    @InjectMocks
    private lateinit var comparator: WheelValidator

    private val WHEEL = S18_1

    @Test
    fun canSave() {
        // No change
        cannotSave(WHEEL)

        // Name
        canSave(WHEEL.copy(name = NAME_NEW))
        cannotSave(WHEEL.copy(name = ""))

        // Pre-mileage
        canSave(WHEEL.copy(premileage = PREMILEAGE_NEW))
        canSave(WHEEL.copy(premileage = 0))

        // Mileage
        canSave(WHEEL.copy(mileage = MILEAGE_NEW))
        canSave(WHEEL.copy(mileage = 0))

        // Wh
        canSave(WHEEL.copy(wh = WH_NEW))
        cannotSave(WHEEL.copy(wh = 0))

        // Voltage min
        canSave(WHEEL.copy(voltageMin = VOLTAGE_MIN_NEW))
        cannotSave(WHEEL.copy(voltageMin = 0f))
        cannotSave(WHEEL.copy(voltageMin = WHEEL.voltageMax + 0.1f))

        // Voltage max
        canSave(WHEEL.copy(voltageMax = VOLTAGE_MAX_NEW))
        cannotSave(WHEEL.copy(voltageMax = 0f))

        // Charge rate
        canSave(WHEEL.copy(chargeRate = CHARGE_RATE_NEW))
        cannotSave(WHEEL.copy(chargeRate = 0f))

        // Voltage full
        canSave(WHEEL.copy(voltageFull = VOLTAGE_FULL_NEW))
        cannotSave(WHEEL.copy(voltageFull = 0f))
        cannotSave(WHEEL.copy(voltageFull = WHEEL.voltageMax + 0.1f))
        cannotSave(WHEEL.copy(voltageFull = WHEEL.voltageMin - 0.1f))

        // Distance offset
        canSave(WHEEL.copy(distanceOffset = DISTANCE_OFFSET_NEW))
        canSave(WHEEL.copy(distanceOffset = 0f))

        // Sold indicator
        canSave(WHEEL.copy(isSold = SOLD))
    }

    private fun canSave(updatedWheel: WheelEntity) = canSaveOrNot(updatedWheel, true)
    private fun cannotSave(updatedWheel: WheelEntity) = canSaveOrNot(updatedWheel, false)

    private fun canSaveOrNot(updatedWheel: WheelEntity, expectedCanSave: Boolean) {
        // When
        val result = comparator.canSave(updatedWheel, WHEEL)

        // Then
        assertThat(result, equalTo(expectedCanSave))
    }
}
