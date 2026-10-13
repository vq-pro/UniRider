package quebec.virtualite.commons.android.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ByteArrayUtilsTest
{
    @InjectMocks
    private lateinit var utils: ByteArrayUtils

    @Test
    fun byteArrayInt2()
    {
        // When
        val result = utils.byteArrayInt2(0xAB.toByte(), 0xCD.toByte())

        // Then
        assertThat(result.toUInt()).isEqualTo(0xABCDu)
    }

    @Test
    fun byteArrayInt4()
    {
        // When
        val result = utils.byteArrayInt4(0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte(), 0x00.toByte())

        // Then
        assertThat(result.toULong()).isEqualTo(0xABCDEF00u.toULong())
    }

    @Test
    fun byteArrayToString()
    {
        // Given
        val input: ByteArray = byteArrayOf(0xAB.toByte(), 0xCD.toByte(), 0x0A.toByte(), 0)

        // When
        val result = utils.byteArrayToString(input)

        // Then
        assertThat(result).isEqualTo("AB-CD-0A-00")
    }
}