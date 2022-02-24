package quebec.virtualite.commons.android.utils

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ByteArrayUtilsTest {
    @InjectMocks
    private lateinit var utils: ByteArrayUtils

    @Test
    fun byteArrayInt2() {
        // When
        val result = utils.byteArrayInt2(0xCD.toByte(), 0xAB.toByte())

        // Then
        MatcherAssert.assertThat(result.toUInt(), equalTo(0xABCDu))
    }

    @Test
    fun byteArrayInt4() {
        // When
        val result = utils.byteArrayInt4(0xCD.toByte(), 0xAB.toByte(), 0x00.toByte(), 0xEF.toByte())

        // Then
        MatcherAssert.assertThat(result.toULong(), equalTo(0xABCDEF00u))
    }

    @Test
    fun byteArrayToString() {
        // Given
        val input: ByteArray = byteArrayOf(0xAB.toByte(), 0xCD.toByte(), 0x0A.toByte(), 0)

        // When
        val result = utils.byteArrayToString(input)

        // Then
        MatcherAssert.assertThat(result, equalTo("AB-CD-0A-00"))
    }
}