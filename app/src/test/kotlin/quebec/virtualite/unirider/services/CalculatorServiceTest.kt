package quebec.virtualite.unirider.services

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CalculatorServiceTest {

    private val KS_14S = "KingSong 14S"
    private val KS_S18 = "KingSong S18"
    private val NIKOLA = "Gotway Nikola+"
    private val SHERMAN = "Veteran Sherman"
    private val V10F = "Inmotion V10F"

    @InjectMocks
    var service = CalculatorService()

    @Test
    fun batteryOn() {
        // Gotway Nikola
        batteryOn(NIKOLA, "100.8", "100%")
        batteryOn(NIKOLA, "96.4", "80.7%")
        batteryOn(NIKOLA, "95.0", "74.6%")
        batteryOn(NIKOLA, "95.", "74.6%")
        batteryOn(NIKOLA, "95", "74.6%")
        batteryOn(NIKOLA, "89.1", "48.7%")
        batteryOn(NIKOLA, "78.0", "0%")

        // Inmotion V10F
        batteryOn(V10F, "74.6", "41.3%")

        // KingSong 14S
        batteryOn(KS_14S, "63.5", "80.7%")

        // Veteran Sherman
        batteryOn(SHERMAN, "96.5", "82.9%")

        // Invalid values
        batteryOn(NIKOLA, "", "")
        batteryOn(NIKOLA, "9", "")
    }

    @Test
    fun wheels() {
        // When
        val wheels = service.wheels()

        // Then
        assertThat(
            wheels, equalTo(
                listOf(
                    NIKOLA, V10F, KS_14S, KS_S18, SHERMAN
                )
            )
        )
    }

    internal fun batteryOn(wheelName: String, voltage: String, expectedBattery: String) {
        // When
        val battery = service.batteryOn(wheelName, voltage)

        // Then
        assertThat(battery, equalTo(expectedBattery))
    }
}
