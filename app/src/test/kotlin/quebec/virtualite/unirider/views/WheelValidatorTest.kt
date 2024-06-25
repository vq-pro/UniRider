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
import quebec.virtualite.unirider.TestDomain.VOLTAGE_START
import quebec.virtualite.unirider.TestDomain.WH
import quebec.virtualite.unirider.TestDomain.WH_NEW
import quebec.virtualite.unirider.database.WheelEntity

@RunWith(MockitoJUnitRunner::class)
class WheelValidatorTest {

    @InjectMocks
    lateinit var comparator: WheelValidator

    // FIXME-1 Redesign this test for clarity
    @Test
    fun canSave() {
        // No change
        cannotSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_MAX, CHARGE_RATE, VOLTAGE_FULL, CHARGER_OFFSET, NOT_SOLD)

        // Name
        canSave(NAME_NEW, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_MAX, CHARGE_RATE, VOLTAGE_FULL, CHARGER_OFFSET, NOT_SOLD)
        cannotSave(
            "", PREMILEAGE_NEW, MILEAGE_NEW, WH_NEW,
            VOLTAGE_MIN_NEW, VOLTAGE_RESERVE_NEW, VOLTAGE_MAX_NEW,
            CHARGE_RATE_NEW, VOLTAGE_FULL_NEW, CHARGER_OFFSET_NEW, NOT_SOLD
        )

        // Pre-mileage
        canSave(NAME, PREMILEAGE_NEW, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_MAX, CHARGE_RATE, VOLTAGE_FULL, CHARGER_OFFSET, NOT_SOLD)
        canSave(
            NAME_NEW, 0, MILEAGE, WH_NEW,
            VOLTAGE_MIN_NEW, VOLTAGE_RESERVE_NEW, VOLTAGE_MAX_NEW,
            CHARGE_RATE_NEW, VOLTAGE_FULL_NEW, CHARGER_OFFSET_NEW, NOT_SOLD
        )

        // Mileage
        canSave(NAME, PREMILEAGE, MILEAGE_NEW, WH, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_MAX, CHARGE_RATE, VOLTAGE_FULL, CHARGER_OFFSET, NOT_SOLD)
        canSave(
            NAME_NEW, PREMILEAGE_NEW, 0, WH_NEW,
            VOLTAGE_MIN_NEW, VOLTAGE_RESERVE_NEW, VOLTAGE_MAX_NEW,
            CHARGE_RATE_NEW, VOLTAGE_FULL_NEW, CHARGER_OFFSET_NEW, NOT_SOLD
        )

        // Wh
        canSave(NAME, PREMILEAGE, MILEAGE, WH_NEW, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_MAX, CHARGE_RATE, VOLTAGE_FULL, CHARGER_OFFSET, NOT_SOLD)
        cannotSave(
            NAME_NEW, PREMILEAGE_NEW, MILEAGE_NEW, 0,
            VOLTAGE_MIN_NEW, VOLTAGE_RESERVE_NEW, VOLTAGE_MAX_NEW,
            CHARGE_RATE_NEW, VOLTAGE_FULL_NEW, CHARGER_OFFSET_NEW, NOT_SOLD
        )

        // Voltage min
        canSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN_NEW, VOLTAGE_RESERVE, VOLTAGE_MAX, CHARGE_RATE, VOLTAGE_FULL, CHARGER_OFFSET, NOT_SOLD)
        cannotSave(
            NAME_NEW, PREMILEAGE_NEW, MILEAGE_NEW, WH_NEW,
            0f, VOLTAGE_RESERVE_NEW, VOLTAGE_MAX_NEW,
            CHARGE_RATE_NEW, VOLTAGE_FULL_NEW, CHARGER_OFFSET_NEW, NOT_SOLD
        )

        // Min higher than max
        cannotSave(
            NAME,
            PREMILEAGE,
            MILEAGE,
            WH,
            VOLTAGE_MAX + 0.1f,
            VOLTAGE_RESERVE,
            VOLTAGE_MAX,
            CHARGE_RATE,
            VOLTAGE_FULL,
            CHARGER_OFFSET,
            NOT_SOLD
        )

        // Voltage reserve
        canSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_RESERVE_NEW, VOLTAGE_MAX, CHARGE_RATE, VOLTAGE_FULL, CHARGER_OFFSET, NOT_SOLD)
        cannotSave(
            NAME_NEW, PREMILEAGE_NEW, MILEAGE_NEW, WH_NEW,
            VOLTAGE_MIN, 0f, VOLTAGE_MAX_NEW,
            CHARGE_RATE_NEW, VOLTAGE_FULL_NEW, CHARGER_OFFSET_NEW, NOT_SOLD
        )

        // Reserve higher than max
        cannotSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_MAX + 0.1f, VOLTAGE_MAX, CHARGE_RATE, VOLTAGE_FULL, CHARGER_OFFSET, NOT_SOLD)

        // Reserve lower than min
        cannotSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_MIN - 0.1f, VOLTAGE_MAX, CHARGE_RATE, VOLTAGE_FULL, CHARGER_OFFSET, NOT_SOLD)

        // Voltage max
        canSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_MAX_NEW, CHARGE_RATE, VOLTAGE_FULL, CHARGER_OFFSET, NOT_SOLD)
        cannotSave(
            NAME_NEW, PREMILEAGE_NEW, MILEAGE_NEW, WH_NEW,
            VOLTAGE_MIN_NEW, VOLTAGE_RESERVE_NEW, 0f,
            CHARGE_RATE_NEW, VOLTAGE_FULL_NEW, CHARGER_OFFSET_NEW, NOT_SOLD
        )

        // Max lower than min
        cannotSave(
            NAME, PREMILEAGE, MILEAGE, WH,
            VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_MIN - 0.1f,
            CHARGE_RATE, VOLTAGE_FULL, CHARGER_OFFSET, NOT_SOLD
        )

        // Charge rate
        canSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_MAX, CHARGE_RATE_NEW, VOLTAGE_FULL, CHARGER_OFFSET, NOT_SOLD)
        cannotSave(
            NAME_NEW, PREMILEAGE_NEW, MILEAGE_NEW, WH_NEW,
            VOLTAGE_MIN_NEW, VOLTAGE_RESERVE_NEW, VOLTAGE_MAX_NEW,
            0f, VOLTAGE_FULL_NEW, CHARGER_OFFSET_NEW, NOT_SOLD
        )

        // Voltage full
        canSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_MAX, CHARGE_RATE, VOLTAGE_FULL_NEW, CHARGER_OFFSET, NOT_SOLD)
        canSave(
            NAME_NEW, PREMILEAGE_NEW, MILEAGE_NEW, WH_NEW,
            VOLTAGE_MIN_NEW, VOLTAGE_RESERVE_NEW, VOLTAGE_MAX_NEW,
            CHARGE_RATE_NEW, VOLTAGE_MAX_NEW, CHARGER_OFFSET_NEW, NOT_SOLD
        )

        // Charger offset
        canSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_MAX, CHARGE_RATE, VOLTAGE_FULL, CHARGER_OFFSET_NEW, NOT_SOLD)
        canSave(
            NAME_NEW, PREMILEAGE_NEW, MILEAGE_NEW, WH_NEW,
            VOLTAGE_MIN_NEW, VOLTAGE_RESERVE_NEW, VOLTAGE_MAX_NEW,
            CHARGE_RATE_NEW, VOLTAGE_FULL_NEW, 0f, NOT_SOLD
        )

        // Sold indicator
        canSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_MAX, CHARGE_RATE, VOLTAGE_FULL, CHARGER_OFFSET, SOLD)
        cannotSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_MAX, CHARGE_RATE, VOLTAGE_FULL, CHARGER_OFFSET, NOT_SOLD)
    }

    private fun canSave(
        name: String, premileage: Int, mileage: Int, wh: Int,
        voltageMin: Float, voltageReserve: Float, voltageMax: Float,
        chargeRate: Float, voltageFull: Float, chargerOffset: Float, isDeleted: Boolean

    ) = canSaveOrNot(name, premileage, mileage, wh, voltageMin, voltageReserve, voltageMax, chargeRate, voltageFull, chargerOffset, isDeleted, true)

    private fun cannotSave(
        name: String, premileage: Int, mileage: Int, wh: Int,
        voltageMin: Float, voltageReserve: Float, voltageMax: Float,
        chargeRate: Float, voltageFull: Float, chargerOffset: Float, isDeleted: Boolean

    ) = canSaveOrNot(name, premileage, mileage, wh, voltageMin, voltageReserve, voltageMax, chargeRate, voltageFull, chargerOffset, isDeleted, false)

    private fun canSaveOrNot(
        name: String, premileage: Int, mileage: Int, wh: Int,
        voltageMin: Float, voltageReserve: Float, voltageMax: Float,
        chargeRate: Float, voltageFull: Float, chargerOffset: Float, isDeleted: Boolean,
        expectedCanSave: Boolean
    ) {
        // Given
        val initial = WheelEntity(
            ID, NAME, DEVICE_NAME, DEVICE_ADDR,
            PREMILEAGE, MILEAGE, WH,
            VOLTAGE_MAX, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_START,
            CHARGE_RATE, VOLTAGE_FULL, CHARGER_OFFSET, NOT_SOLD
        )
        val updated = WheelEntity(
            ID, name.trim(), DEVICE_NAME, DEVICE_ADDR,
            premileage, mileage, wh,
            voltageMax, voltageMin, voltageReserve, VOLTAGE_START,
            chargeRate, voltageFull, chargerOffset, isDeleted
        )

        // When
        val result = comparator.canSave(updated, initial)

        // Then
        assertThat(result, equalTo(expectedCanSave))
    }
}
