package quebec.virtualite.unirider.views

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.database.WheelEntity

@RunWith(MockitoJUnitRunner::class)
class SaveComparatorTest {

    private val BT_ADDR = "AA:BB:CC:DD:EE:FF"
    private val BT_NAME = "LK2000"
    private val ID = 1111L
    private val MILEAGE = 2222
    private val NAME = "Sherman"
    private val NEW_MILEAGE = 3333
    private val NEW_NAME = "Sherman Max"
    private val NEW_VOLTAGE_MAX = 100.9f
    private val NEW_VOLTAGE_MIN = 74.5f
    private val VOLTAGE_MAX = 100.8f
    private val VOLTAGE_MIN = 75.6f

    @InjectMocks
    lateinit var comparator: SaveComparator

    @Test
    fun canSave() {
        canSave(NAME, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX, false)

        canSave(NEW_NAME, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX, true)
        canSave("", NEW_MILEAGE, NEW_VOLTAGE_MIN, NEW_VOLTAGE_MAX, false)

        canSave(NAME, NEW_MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX, true)
        canSave(NEW_NAME, 0, NEW_VOLTAGE_MIN, NEW_VOLTAGE_MAX, true)

        canSave(NAME, MILEAGE, NEW_VOLTAGE_MIN, VOLTAGE_MAX, true)
        canSave(NEW_NAME, NEW_MILEAGE, 0f, NEW_VOLTAGE_MAX, false)

        canSave(NAME, MILEAGE, VOLTAGE_MIN, NEW_VOLTAGE_MAX, true)
        canSave(NEW_NAME, NEW_MILEAGE, NEW_VOLTAGE_MIN, 0f, false)
    }

    private fun canSave(name: String, mileage: Int, voltageMin: Float, voltageMax: Float, expectedCanSave: Boolean) {
        // Given
        val initial = WheelEntity(ID, NAME, BT_NAME, BT_ADDR, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)
        val updated = WheelEntity(ID, name.trim(), BT_NAME, BT_ADDR, mileage, voltageMin, voltageMax)

        // When
        val result = comparator.canSave(updated, initial)

        // Then
        assertThat(result, equalTo(expectedCanSave))
    }
}
