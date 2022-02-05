package quebec.virtualite.commons.android.utils

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class NumberUtilsTest {
    @Test
    fun intOf() {
        intOf("12", 12)
        intOf("12 ", 12)
        intOf("", 0)
        intOf(" ", 0)
    }

    private fun intOf(value: String, expectedResult: Int) {
        // When
        val result = NumberUtils.intOf(value)

        // Then
        assertThat(result, equalTo(expectedResult))
    }
}