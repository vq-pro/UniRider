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
        canSave(NEW_NAME, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX, true)
        canSave(NAME, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX, false)
        canSave("", MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX, false)
    }

    private fun canSave(name: String, mileage: Int, voltageMin: Float, voltageMax: Float, expectedCanSave: Boolean) {
        // Given
        val initial = WheelEntity(ID, NAME, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)
        val updated = WheelEntity(ID, name.trim(), mileage, voltageMin, voltageMax)

        // When
        val result = comparator.canSave(updated, initial)

        // Then
        assertThat(result, equalTo(expectedCanSave))
    }
}