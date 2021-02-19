package quebec.virtualite.unirider.services

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
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
        batteryOn("", 100.8f, 79.2f, "")
        batteryOn("9", 100.8f, 79.2f, "")
        batteryOn("63.5", 0f, 0f, "")
        batteryOn("96.4", 100.8f, 79.2f, "79.6%")
        batteryOn("89.1", 100.8f, 79.2f, "45.8%")
        batteryOn("100.8", 100.8f, 79.2f, "100%")
        batteryOn("79.2", 100.8f, 79.2f, "0%")
        batteryOn("63.5", 67.2f, 48.0f, "80.7%")
        batteryOn("96.5", 100.8f, 75.6f, "82.9%")
        batteryOn("74.6", 84f, 68f, "41.3%")
    }

    internal fun batteryOn(voltage: String, highest: Float, lowest: Float, expectedBattery: String) {
        // When
        val battery = calculator.batteryOn(voltage, highest, lowest)

        // Then
        assertThat(battery, equalTo(expectedBattery))
    }
}
