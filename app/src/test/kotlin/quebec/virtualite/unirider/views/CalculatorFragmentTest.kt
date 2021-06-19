package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.services.CalculatorService

@RunWith(MockitoJUnitRunner::class)
class CalculatorFragmentTest {

    private val BATTERY = "100%"

    @Mock
    lateinit var mockedBundle: Bundle

    @Mock
    lateinit var mockedButtonOk: Button

    @Mock
    lateinit var mockedFieldName: EditText

    @Mock
    lateinit var mockedFieldTitleMessage: TextView

    @Mock
    lateinit var mockedView: View

    @Mock
    lateinit var mockedCalculatorService: CalculatorService

    @Mock
    lateinit var mockedWheelBattery: TextView

    @InjectMocks
    var fragment = CalculatorFragment()

    @Ignore("is it necessary?")
    @Test
    fun onViewCreated() {
        // Given
        given<Any>(mockedView.findViewById(R.id.send))
            .willReturn(mockedButtonOk)

        given<Any>(mockedView.findViewById(R.id.title_message))
            .willReturn(mockedFieldTitleMessage)

        given<Any>(mockedView.findViewById(R.id.name))
            .willReturn(mockedFieldName)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedView).findViewById<Button>(R.id.send)
//        verify(mockedButtonOk).setOnClickListener(any())
    }

    // FIXME 1 Re-enable
    @Ignore
    fun onUpdateVoltage() {
        // Given
        fragment.calculatorService = mockedCalculatorService
        fragment.wheelBattery = mockedWheelBattery

//        given(mockedCalculatorService.batteryOn(anyString(), anyFloat(), anyFloat()))
//            .willReturn(BATTERY)

        // When
        fragment.onUpdateVoltage().invoke("95.5")

        // Then
        verify(mockedWheelBattery).setText(BATTERY)
    }
}
