package quebec.virtualite.unirider.views

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.TestDomain.DEVICE_ADDR
import quebec.virtualite.unirider.TestDomain.DEVICE_NAME
import quebec.virtualite.unirider.TestDomain.ID
import quebec.virtualite.unirider.TestDomain.MILEAGE
import quebec.virtualite.unirider.TestDomain.MILEAGE_NEW
import quebec.virtualite.unirider.TestDomain.NAME
import quebec.virtualite.unirider.TestDomain.NAME_NEW
import quebec.virtualite.unirider.TestDomain.PREMILEAGE
import quebec.virtualite.unirider.TestDomain.PREMILEAGE_NEW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MAX
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MAX_NEW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MIN
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MIN_NEW
import quebec.virtualite.unirider.TestDomain.WH
import quebec.virtualite.unirider.TestDomain.WH_NEW
import quebec.virtualite.unirider.database.WheelEntity

@RunWith(MockitoJUnitRunner::class)
class SaveComparatorTest {

    @InjectMocks
    lateinit var comparator: SaveComparator

    @Test
    fun canSave() {
        canSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_MAX, false)

        canSave(NAME_NEW, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_MAX, true)
        canSave("", PREMILEAGE_NEW, MILEAGE_NEW, WH_NEW, VOLTAGE_MIN_NEW, VOLTAGE_MAX_NEW, false)

        canSave(NAME, PREMILEAGE_NEW, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_MAX, true)
        canSave(NAME_NEW, 0, MILEAGE, WH_NEW, VOLTAGE_MIN_NEW, VOLTAGE_MAX_NEW, true)

        canSave(NAME, PREMILEAGE, MILEAGE_NEW, WH, VOLTAGE_MIN, VOLTAGE_MAX, true)
        canSave(NAME_NEW, PREMILEAGE_NEW, 0, WH_NEW, VOLTAGE_MIN_NEW, VOLTAGE_MAX_NEW, true)

        canSave(NAME, PREMILEAGE, MILEAGE, WH_NEW, VOLTAGE_MIN, VOLTAGE_MAX, true)
        canSave(NAME_NEW, PREMILEAGE_NEW, MILEAGE_NEW, 0, VOLTAGE_MIN_NEW, VOLTAGE_MAX_NEW, false)

        canSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN_NEW, VOLTAGE_MAX, true)
        canSave(NAME_NEW, PREMILEAGE_NEW, MILEAGE_NEW, WH_NEW, 0f, VOLTAGE_MAX_NEW, false)

        canSave(NAME, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_MAX_NEW, true)
        canSave(NAME_NEW, PREMILEAGE_NEW, MILEAGE_NEW, WH_NEW, VOLTAGE_MIN_NEW, 0f, false)
    }

    private fun canSave(name: String, premileage: Int, mileage: Int, wh: Int, voltageMin: Float, voltageMax: Float, expectedCanSave: Boolean) {
        // Given
        val initial = WheelEntity(ID, NAME, DEVICE_NAME, DEVICE_ADDR, PREMILEAGE, MILEAGE, WH, VOLTAGE_MIN, VOLTAGE_MAX)
        val updated = WheelEntity(ID, name.trim(), DEVICE_NAME, DEVICE_ADDR, premileage, mileage, wh, voltageMin, voltageMax)

        // When
        val result = comparator.canSave(updated, initial)

        // Then
        assertThat(result, equalTo(expectedCanSave))
    }
}
