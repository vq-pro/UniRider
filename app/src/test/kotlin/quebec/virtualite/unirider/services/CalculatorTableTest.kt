package quebec.virtualite.unirider.services

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CalculatorTableTest
{
    @InjectMocks
    lateinit var table: CalculatorTable

    @Test
    fun distanceRemaining()
    {
        distanceRemaining(4.091f, 3.809f, 30.0f, 30.81f)
        distanceRemaining(4.091f, 4.053f, 2.5f, 56.84f)
        distanceRemaining(4.069f, 3.893f, 15.0f, 42.39f)
        distanceRemaining(4.069f, 3.667f, 43.0f, 18.48f)
    }

    fun distanceRemaining(initialVoltage: Float, actualVoltage: Float, distance: Float, expectedRemaining: Float)
    {
        // When
        val result = table.distanceRemaining(initialVoltage, actualVoltage, distance)

        // Start = 90.2%
        // Actual = 45.7%
        // End = 0%

        // Distance actual    = 90.2-45.7 = 44.5% - 30 km
        // Distance remaining = 45.7 = 45.7% - 30.81 km
        // Distance total     = 90.2% - 60.81 km

        // Distance actual    = 88-65 = 23% - 15 km
        // Distance remaining = 65 = 65% - 42.391 km
        // Distance total     = 88% - 57.39 km

        // Then
        assertThat(result, equalTo(expectedRemaining))
    }

    @Test
    fun soe()
    {
        soe(4.201f, 0.0f)
        soe(4.200f, 100.0f)
        soe(4.102f, 91.2f)
        soe(3.710f, 31.25f)
        soe(3.335f, 0.346f)
        soe(3.200f, 0.0f)
    }

    fun soe(voltage: Float, expectedSoe: Float)
    {
        // When
        val result = table.soe(voltage)

        // Then
        assertThat(result, equalTo(expectedSoe))
    }
}