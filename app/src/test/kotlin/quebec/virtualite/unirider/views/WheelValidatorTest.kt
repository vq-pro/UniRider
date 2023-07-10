package quebec.virtualite.unirider.views

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner
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
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MAX
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MAX_NEW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MIN
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MIN_NEW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_RESERVE
import quebec.virtualite.unirider.TestDomain.VOLTAGE_RESERVE_NEW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_START
import quebec.virtualite.unirider.TestDomain.WH
import quebec.virtualite.unirider.TestDomain.WH_NEW
import quebec.virtualite.unirider.database.WheelEntity

@RunWith(MockitoJUnitRunner::class)
class WheelValidatorTest {

    @InjectMocks
    lateinit var comparator: WheelValidator

    @Test
    fun canSave() {
        // No change
        canSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_MAX, CHARGE_RATE, NOT_SOLD, false)

        // Name
        canSave(NAME_NEW, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_MAX, CHARGE_RATE, NOT_SOLD, true)
        canSave("", PREMILEAGE_NEW, MILEAGE_NEW, WH_NEW, VOLTAGE_MIN_NEW, VOLTAGE_RESERVE_NEW, VOLTAGE_MAX_NEW, CHARGE_RATE_NEW, NOT_SOLD, false)

        // Pre-mileage
        canSave(NAME, PREMILEAGE_NEW, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_MAX, CHARGE_RATE, NOT_SOLD, true)
        canSave(NAME_NEW, 0, MILEAGE, WH_NEW, VOLTAGE_MIN_NEW, VOLTAGE_RESERVE_NEW, VOLTAGE_MAX_NEW, CHARGE_RATE_NEW, NOT_SOLD, true)

        // Mileage
        canSave(NAME, PREMILEAGE, MILEAGE_NEW, WH, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_MAX, CHARGE_RATE, NOT_SOLD, true)
        canSave(NAME_NEW, PREMILEAGE_NEW, 0, WH_NEW, VOLTAGE_MIN_NEW, VOLTAGE_RESERVE_NEW, VOLTAGE_MAX_NEW, CHARGE_RATE_NEW, NOT_SOLD, true)

        // Wh
        canSave(NAME, PREMILEAGE, MILEAGE, WH_NEW, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_MAX, CHARGE_RATE, NOT_SOLD, true)
        canSave(NAME_NEW, PREMILEAGE_NEW, MILEAGE_NEW, 0, VOLTAGE_MIN_NEW, VOLTAGE_RESERVE_NEW, VOLTAGE_MAX_NEW, CHARGE_RATE_NEW, NOT_SOLD, false)

        // Voltage min
        canSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN_NEW, VOLTAGE_RESERVE, VOLTAGE_MAX, CHARGE_RATE, NOT_SOLD, true)
        canSave(NAME_NEW, PREMILEAGE_NEW, MILEAGE_NEW, WH_NEW, 0f, VOLTAGE_RESERVE_NEW, VOLTAGE_MAX_NEW, CHARGE_RATE_NEW, NOT_SOLD, false)
        // Min higher than max
        canSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MAX + 0.1f, VOLTAGE_RESERVE, VOLTAGE_MAX, CHARGE_RATE, NOT_SOLD, false)

        // Voltage reserve
        canSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_RESERVE_NEW, VOLTAGE_MAX, CHARGE_RATE, NOT_SOLD, true)
        canSave(NAME_NEW, PREMILEAGE_NEW, MILEAGE_NEW, WH_NEW, VOLTAGE_MIN, 0f, VOLTAGE_MAX_NEW, CHARGE_RATE_NEW, NOT_SOLD, false)
        // Reserve higher than max
        canSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_MAX + 0.1f, VOLTAGE_MAX, CHARGE_RATE, NOT_SOLD, false)
        // Reserve lower than min
        canSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_MIN - 0.1f, VOLTAGE_MAX, CHARGE_RATE, NOT_SOLD, false)

        // Voltage max
        canSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_MAX_NEW, CHARGE_RATE, NOT_SOLD, true)
        canSave(NAME_NEW, PREMILEAGE_NEW, MILEAGE_NEW, WH_NEW, VOLTAGE_MIN_NEW, VOLTAGE_RESERVE_NEW, 0f, CHARGE_RATE_NEW, NOT_SOLD, false)
        // Max lower than min
        canSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_MIN - 0.1f, CHARGE_RATE, NOT_SOLD, false)

        // Charge rate
        canSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_MAX, CHARGE_RATE_NEW, NOT_SOLD, true)
        canSave(NAME_NEW, PREMILEAGE_NEW, MILEAGE_NEW, WH_NEW, VOLTAGE_MIN_NEW, VOLTAGE_RESERVE_NEW, VOLTAGE_MAX_NEW, 0f, NOT_SOLD, false)

        // Sold indicator
        canSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_MAX, CHARGE_RATE, SOLD, true)
        canSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_MAX, CHARGE_RATE, NOT_SOLD, false)
    }

    private fun canSave(
        name: String, premileage: Int, mileage: Int, wh: Int,
        voltageMin: Float, voltageReserve: Float, voltageMax: Float,
        chargeRate: Float, isDeleted: Boolean,
        expectedCanSave: Boolean
    ) {
        // Given
        val initial = WheelEntity(
            ID, NAME, DEVICE_NAME, DEVICE_ADDR,
            PREMILEAGE, MILEAGE, WH,
            VOLTAGE_MAX, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_START,
            CHARGE_RATE, NOT_SOLD
        )
        val updated = WheelEntity(
            ID, name.trim(), DEVICE_NAME, DEVICE_ADDR,
            premileage, mileage, wh,
            voltageMax, voltageMin, voltageReserve, VOLTAGE_START,
            chargeRate, isDeleted
        )

        // When
        val result = comparator.canSave(updated, initial)

        // Then
        assertThat(result, equalTo(expectedCanSave))
    }
}
