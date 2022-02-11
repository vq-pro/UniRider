package quebec.virtualite.unirider.views

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.services.WheelScanner

@RunWith(MockitoJUnitRunner::class)
class WheelScannerTest {
    @InjectMocks
    val scanner = WheelScanner()

    @Test
    fun scan() {
        // When
        scanner.scan()

        // Then
    }
}
