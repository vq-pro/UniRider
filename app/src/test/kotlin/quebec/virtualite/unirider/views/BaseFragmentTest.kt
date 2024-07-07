package quebec.virtualite.unirider.views

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
open class BaseFragmentTest {

    @InjectMocks
    lateinit var baseFragment: BaseFragment

    @Test
    fun textPercentageWithDecimal() {
        textPercentageWithDecimal(55.6f, "55.6")
        textPercentageWithDecimal(102.3f, "102.3")
    }

    private fun textPercentageWithDecimal(percentage: Float, expectedDisplay: String) {
        // When
        val result = baseFragment.textPercentageWithDecimal(percentage)

        // Then
        assertThat(result, equalTo(expectedDisplay))
    }

    @Test
    fun textWhPerKm() {
        textWhPerKm(32.5f, "32.5")
        textWhPerKm(30.0f, "30.0")
        textWhPerKm(29.9f, "29.9")
    }

    private fun textWhPerKm(whPerKm: Float, expectedDisplay: String) {
        // When
        val result = baseFragment.textWhPerKm(whPerKm)

        // Then
        assertThat(result, equalTo(expectedDisplay))
    }
}
