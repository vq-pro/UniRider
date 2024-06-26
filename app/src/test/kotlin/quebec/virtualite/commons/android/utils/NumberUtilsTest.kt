package quebec.virtualite.commons.android.utils

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class NumberUtilsTest {

    @Test
    fun floatOf() {
        floatOf("12.0", 12.0f)
        floatOf("12.4 ", 12.4f)
        floatOf("", 0.0f)
        floatOf(" ", 0.0f)
    }

    @Test
    fun intOf() {
        intOf("12", 12)
        intOf("12 ", 12)
        intOf("", 0)
        intOf(" ", 0)
    }

    @Test
    fun round() {
        round(1.123f, 1.1f)
        round(2.001f, 2.0f)
        round(2.549f, 2.5f)
        round(2.550f, 2.6f)
        round(2.551f, 2.6f)
        round(24094.455f, 24094.5f)
    }

    @Test
    fun safeFloatOf() {
        safeFloatOf("1", 1f)
        safeFloatOf("1.1", 1.1f)
        safeFloatOf(" ", 0f)
        safeFloatOf("ab ", 0f)
    }

    @Test
    fun safeIntOf() {
        safeIntOf("1", 1)
        safeIntOf("1.1", 0)
        safeIntOf(" ", 0)
        safeIntOf("ab ", 0)
    }

    private fun floatOf(value: String, expectedResult: Float) {
        // When
        val result = NumberUtils.floatOf(value)

        // Then
        assertThat(result, equalTo(expectedResult))
    }

    private fun intOf(value: String, expectedResult: Int) {
        // When
        val result = NumberUtils.intOf(value)

        // Then
        assertThat(result, equalTo(expectedResult))
    }

    private fun round(value: Float, expectedResult: Float) {
        // When
        val result = NumberUtils.round(value, 1)

        // Then
        assertThat(result, equalTo(expectedResult))
    }

    private fun safeFloatOf(string: String, expectedValue: Float) {
        // When
        val result = NumberUtils.safeFloatOf(string)

        // Then
        assertThat(result, equalTo(expectedValue))
    }

    private fun safeIntOf(string: String, expectedValue: Int) {
        // When
        val result = NumberUtils.safeIntOf(string)

        // Then
        assertThat(result, equalTo(expectedValue))
    }
}
