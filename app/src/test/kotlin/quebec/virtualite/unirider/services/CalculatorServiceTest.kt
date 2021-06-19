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
    var service = CalculatorService()

    @Test
    fun batteryOn() {
        // Gotway Nikola
        batteryOn("100.8", 100.8f, 79.2f, "100%")
        batteryOn("96.4", 100.8f, 79.2f, "79.6%")
        batteryOn("95.0", 100.8f, 79.2f, "73.1%")
        batteryOn("95.", 100.8f, 79.2f, "73.1%")
        batteryOn("95", 100.8f, 79.2f, "73.1%")
        batteryOn("89.1", 100.8f, 79.2f, "45.8%")
        batteryOn("79.2", 100.8f, 79.2f, "0%")

        // Inmotion V10F
        batteryOn("74.6", 84f, 68f, "41.3%")

        // KingSong 14D
        batteryOn("63.5", 67.2f, 48.0f, "80.7%")

        // Veteran Sherman
        batteryOn("96.5", 100.8f, 75.6f, "82.9%")

        // Invalid values
        batteryOn("", 100.8f, 79.2f, "")
        batteryOn("9", 100.8f, 79.2f, "")
        batteryOn("63.5", 0f, 0f, "")
    }

    @Test
    fun wheels() {
        // When
        val wheels = service.wheels()

        // Then
        assertThat(
            wheels, equalTo(
                listOf(
                    // FIXME 0 Remove voltages from this list, should be purely internal to the service
                    Wheel("Gotway Nikola+", 100.8f, 78.0f),
                    Wheel("Inmotion V10F", 84f, 68f),
                    Wheel("KingSong 14S", 67.2f, 48.0f),
                    Wheel("KingSong S18", 84.5f, 63.0f),
                    Wheel("Veteran Sherman", 100.8f, 75.6f)
                )
            )
        )
    }

    internal fun batteryOn(voltage: String, highest: Float, lowest: Float, expectedBattery: String) {
        // When
        val battery = service.batteryOn(voltage, highest, lowest)

        // Then
        assertThat(battery, equalTo(expectedBattery))
    }
}
