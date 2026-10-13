package quebec.virtualite.unirider.views

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class BaseFragmentTest
{

    @InjectMocks
    private lateinit var baseFragment: BaseFragment

    @ParameterizedTest
    @CsvSource(
        delimiter = '-',
        value = [
            "55.6f - 55.6",
            "55f   - 55",
            "50f   - 50"
        ]
    )
    fun textKmWithDecimal(km: Float, expectedDisplay: String)
    {
        // When
        val result = baseFragment.textKmWithDecimal(km)

        // Then
        assertThat(result).isEqualTo(expectedDisplay)
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = '-',
        value = [
            "55.6f  - 55.6",
            "102.3f - 102.3"
        ]
    )
    fun textPercentageWithDecimal(percentage: Float, expectedDisplay: String)
    {
        // When
        val result = baseFragment.textPercentageWithDecimal(percentage)

        // Then
        assertThat(result).isEqualTo(expectedDisplay)
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = '-',
        value = [
            "32.5f - 32.5",
            "30.0f - 30.0",
            "29.9f - 29.9"
        ]
    )
    fun textWhPerKm(whPerKm: Float, expectedDisplay: String)
    {
        // When
        val result = baseFragment.textWhPerKm(whPerKm)

        // Then
        assertThat(result).isEqualTo(expectedDisplay)
    }
}
