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

    private val DISTANCE = 123
    private val NAME = "KingSong S20"
    private val VOLTAGE_MAX = 126.0f
    private val VOLTAGE_MIN = 90.0f

    @InjectMocks
    lateinit var service: CalculatorService

//    FIXME 2 Prévoir le cas où on vient d'ajouter une roue mais on n'a pas encore défini les voltages max et min

    @Test
    fun batteryOn() {
        // Given
        val wheel = WheelEntity(0, NAME, DISTANCE, VOLTAGE_MIN, VOLTAGE_MAX)

        // When
        val percentage = service.batteryOn(wheel, 108.0f)

        // Then

        // FIXME 0 Make this a float
        assertThat(percentage, equalTo("50.0%"))
    }
}
