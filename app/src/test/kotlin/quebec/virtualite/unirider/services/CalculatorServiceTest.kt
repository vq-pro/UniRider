package quebec.virtualite.unirider.services

import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CalculatorServiceTest {

    @InjectMocks
    var calculator = CalculatorService()

    @Test
    fun batteryOn() {
        batteryOn("", "")
        batteryOn("96.4", "79.6%")
        batteryOn("89.1", "45.8%")
        batteryOn("100.8", "100%")
        batteryOn("79.2", "0%")
    }

    internal fun batteryOn(voltage: String, expectedBattery: String) {
        // When
        val battery = calculator.batteryOn(voltage)

        // Then
        assertThat(battery, org.hamcrest.core.Is.`is`(expectedBattery))
    }
}
