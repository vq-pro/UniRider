package quebec.virtualite.unirider.services

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class CalculatorTableTest
{
    @InjectMocks
    lateinit var table: CalculatorTable

    @ParameterizedTest
    @CsvSource(
        delimiter = '-',
        value = [
            "4.201f - 100.0f",
            "4.200f - 100.0f",
            "4.102f - 91.2f",
            "3.710f - 31.25f",
            "3.335f - 0.346f",
            "3.200f - 0.0f",
        ]
    )
    fun soE(voltage: Float, expectedSoe: Float)
    {
        // When
        val result = table.soE(voltage)

        // Then
        assertThat(result).isEqualTo(expectedSoe)
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = '-',
        value = [
            "98.9f  - 4.187f",
            "95.6f  - 4.150f",
            "0f     - 3.330f",
            "'-1f'  - 0f",
            "100f   - 4.2f",
            "100.1f - 4.2f",
            "39f    - 3.769f",
            "70.6f  - 3.926f",
        ]
    )
    fun voltage(soE: Float, expectedVoltage: Float)
    {
        // When
        val result = table.voltage(soE)

        // Then
        assertThat(result).isEqualTo(expectedVoltage)
    }
}