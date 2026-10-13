package quebec.virtualite.commons.android.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class NumberUtilsTest
{
    @ParameterizedTest
    @CsvSource(
        delimiter = '-',
        value = [
            "12.0 - 12.0f",
            "12.4 - 12.4f",
            "''   - 0.0f",
            "' '  - 0.0f"
        ]
    )
    fun floatOf(value: String, expectedResult: Float)
    {
        // When
        val result = NumberUtils.floatOf(value)

        // Then
        assertThat(result).isEqualTo(expectedResult)
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = '-',
        value = [
            "12    - 12",
            "'12 ' - 12",
            "''    - 0",
            "' '   - 0"
        ]
    )
    fun intOf(value: String, expectedResult: Int)
    {
        // When
        val result = NumberUtils.intOf(value)

        // Then
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun round_1parm()
    {
        // Given
        val value = 1.123f

        // When
        val result = NumberUtils.round(value)

        // Then
        assertThat(result).isEqualTo(NumberUtils.round(value, NB_DECIMALS))
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = '-',
        value = [
            "    1.123f -     1.1f",
            "    2.001f -     2.0f",
            "    2.549f -     2.5f",
            "    2.550f -     2.6f",
            "    2.551f -     2.6f",
            "24094.455f - 24094.5f"
        ]
    )
    fun round_2parms(value: Float, expectedResult: Float)
    {
        // When
        val result = NumberUtils.round(value, 1)

        // Then
        assertThat(result).isEqualTo(expectedResult)
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = '-',
        value = [
            "1     - 1f",
            "1.1   - 1.1f",
            "' '   - 0f",
            "'ab ' - 0f"
        ]
    )
    fun safeFloatOf(string: String, expectedValue: Float)
    {
        // When
        val result = NumberUtils.safeFloatOf(string)

        // Then
        assertThat(result).isEqualTo(expectedValue)
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = '-',
        value = [
            "1     - 1",
            "1.1   - 0",
            "' '   - 0",
            "'ab ' - 0"
        ]
    )
    fun safeIntOf(string: String, expectedValue: Int)
    {
        // When
        val result = NumberUtils.safeIntOf(string)

        // Then
        assertThat(result).isEqualTo(expectedValue)
    }
}
