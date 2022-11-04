package quebec.virtualite.unirider.views

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
open class BaseFragmentMethodTest {

    @InjectMocks
    lateinit var base: BaseFragment

    @Test
    fun textWhPerKm() {
        textWhPerKm(32.5f, "30+")
        textWhPerKm(30.0f, "30+")
        textWhPerKm(29.9f, "25+")
    }

    private fun textWhPerKm(whPerKm: Float, expectedDisplay: String) {
        // When
        val result = base.textWhPerKm(whPerKm)

        // Then
        assertThat(result, equalTo(expectedDisplay))
    }
}