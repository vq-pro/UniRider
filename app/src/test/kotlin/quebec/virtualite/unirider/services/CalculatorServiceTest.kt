package quebec.virtualite.unirider.services

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.database.WheelEntity

@RunWith(MockitoJUnitRunner::class)
class CalculatorServiceTest {

    private val BT_NAME = "KS-S20xx9999"
    private val MILEAGE = 123
    private val NAME = "KingSong S20"
    private val VOLTAGE_MAX = 126.0f
    private val VOLTAGE_MIN = 90.0f

    @InjectMocks
    lateinit var service: CalculatorService

    @Test
    fun percentage() {
        // Given
        val wheel = WheelEntity(0, NAME, BT_NAME, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)

        // When
        val percentage = service.percentage(wheel, 108.0f)

        // Then
        assertThat(percentage, equalTo(50.0f))
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
        val wheel = WheelEntity(0, NAME, BT_NAME, MILEAGE, voltageMin, voltageMax)

        // When
        val percentage = service.percentage(wheel, 108.0f)

        // Then
        assertThat(percentage, equalTo(0.0f))
    }
}
