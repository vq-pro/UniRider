package quebec.virtualite.unirider.views

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.TestDomain.CHARGER_OFFSET
import quebec.virtualite.unirider.TestDomain.CHARGER_OFFSET_NEW
import quebec.virtualite.unirider.TestDomain.CHARGE_RATE
import quebec.virtualite.unirider.TestDomain.CHARGE_RATE_NEW
import quebec.virtualite.unirider.TestDomain.DEVICE_ADDR
import quebec.virtualite.unirider.TestDomain.DEVICE_NAME
import quebec.virtualite.unirider.TestDomain.ID
import quebec.virtualite.unirider.TestDomain.MILEAGE
import quebec.virtualite.unirider.TestDomain.MILEAGE_NEW
import quebec.virtualite.unirider.TestDomain.NAME
import quebec.virtualite.unirider.TestDomain.NAME_NEW
import quebec.virtualite.unirider.TestDomain.NOT_SOLD
import quebec.virtualite.unirider.TestDomain.PREMILEAGE
import quebec.virtualite.unirider.TestDomain.PREMILEAGE_NEW
import quebec.virtualite.unirider.TestDomain.SOLD
import quebec.virtualite.unirider.TestDomain.VOLTAGE_FULL
import quebec.virtualite.unirider.TestDomain.VOLTAGE_FULL_NEW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MAX
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MAX_NEW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MIN
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MIN_NEW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_RESERVE
import quebec.virtualite.unirider.TestDomain.VOLTAGE_RESERVE_NEW
import quebec.virtualite.unirider.TestDomain.WH
import quebec.virtualite.unirider.TestDomain.WH_NEW
import quebec.virtualite.unirider.database.WheelEntity

@RunWith(MockitoJUnitRunner::class)
class WheelValidatorTest {

    @InjectMocks
    lateinit var comparator: WheelValidator

    private val WHEEL = WheelEntity(
        ID, NAME, DEVICE_NAME, DEVICE_ADDR,
        PREMILEAGE, MILEAGE, WH,
        VOLTAGE_MAX, VOLTAGE_MIN, VOLTAGE_RESERVE,
        CHARGE_RATE, VOLTAGE_FULL, CHARGER_OFFSET, NOT_SOLD
    )

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

        // Voltage reserve
        canSave(WHEEL.copy(voltageReserve = VOLTAGE_RESERVE_NEW))
        cannotSave(WHEEL.copy(voltageReserve = 0f))
        cannotSave(WHEEL.copy(voltageReserve = WHEEL.voltageMax + 0.1f))
        cannotSave(WHEEL.copy(voltageReserve = WHEEL.voltageMin - 0.1f))

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

        // Charger offset
        canSave(WHEEL.copy(chargerOffset = CHARGER_OFFSET_NEW))
        canSave(WHEEL.copy(chargerOffset = 0f))

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
