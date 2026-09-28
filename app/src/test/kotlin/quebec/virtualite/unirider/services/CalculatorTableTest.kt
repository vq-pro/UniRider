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
    fun soE()
    {
        soE(4.201f, 100.0f)
        soE(4.200f, 100.0f)
        soE(4.102f, 91.2f)
        soE(3.710f, 31.25f)
        soE(3.335f, 0.346f)
        soE(3.200f, 0.0f)
    }

    fun soE(voltage: Float, expectedSoe: Float)
    {
        // When
        val result = table.soE(voltage)

        // Then
        assertThat(result, equalTo(expectedSoe))
    }

    @Test
    fun voltage()
    {
        voltage(98.9f, 4.187f)
        voltage(95.6f, 4.150f)
        voltage(0f, 3.330f)
        voltage(-1f, 0f)
        voltage(100f, 4.2f)
        voltage(100.1f, 4.2f)
        voltage(39f, 3.769f)
        voltage(70.6f, 3.926f)
    }

    fun voltage(soE: Float, expectedVoltage: Float)
    {
        // When
        val result = table.voltage(soE)

        // Then
        assertThat(result, equalTo(expectedVoltage))
    }
}