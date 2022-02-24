package quebec.virtualite.unirider.bluetooth.impl

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DeviceConnectorImplTest {

    @InjectMocks
    private lateinit var connector: DeviceConnectorImpl

    @Test
    fun byteArrayInt2() {
        // When
        val result = connector.byteArrayInt2(0xCD.toByte(), 0xAB.toByte())

        // Then
        assertThat(result.toUInt(), equalTo(0xABCDu))
    }

    @Test
    fun byteArrayInt4() {
        // When
        val result = connector.byteArrayInt4(0xCD.toByte(), 0xAB.toByte(), 0x00.toByte(), 0xEF.toByte())

        // Then
        assertThat(result.toULong(), equalTo(0xABCDEF00u))
    }

    @Test
    fun byteArrayToString() {
        // Given
        val input: ByteArray = byteArrayOf(0xAB.toByte(), 0xCD.toByte(), 0x0A.toByte(), 0)

        // When
        val result = connector.byteArrayToString(input)

        // Then
        assertThat(result, equalTo("AB-CD-0A-00"))
    }
}
