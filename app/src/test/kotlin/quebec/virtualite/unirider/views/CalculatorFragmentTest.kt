package quebec.virtualite.unirider.views

import android.os.Bundle
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.StringContains.containsString
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.utils.WidgetUtils

private const val NAME = "Sherman"
private const val PERCENTAGE = "100%"
private const val VOLTAGE = "100.8"

@RunWith(MockitoJUnitRunner::class)
class CalculatorFragmentTest {

    @Mock
    lateinit var mockedBundle: Bundle

    @Mock
    lateinit var mockedView: View

    @Mock
    lateinit var mockedCalculatorService: CalculatorService

    @Mock
    lateinit var mockedWheelBattery: TextView

    @Mock
    lateinit var mockedWheelName: TextView

    @Mock
    lateinit var mockedWheelVoltage: EditText

    @Mock
    lateinit var mockedWidgets: WidgetUtils

    @Captor
    lateinit var lambda: ArgumentCaptor<(String) -> Unit>

    @InjectMocks
    var fragment = CalculatorFragment()

    @Before
    fun init() {
        fragment.wheel = NAME
    }

    @Test
    fun onViewCreated() {
        // Given
        given<Any>(mockedView.findViewById(R.id.wheel_battery))
            .willReturn(mockedWheelBattery)

        given<Any>(mockedView.findViewById(R.id.wheel_name))
            .willReturn(mockedWheelName)

        given<Any>(mockedView.findViewById(R.id.wheel_voltage))
            .willReturn(mockedWheelVoltage)

        val onUpdateVoltageListener: TextWatcher = mock(TextWatcher::class.java)
        given(mockedWidgets.addTextChangedListener(any()))
            .willReturn(onUpdateVoltageListener)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        assertThat(fragment.wheelBattery, equalTo(mockedWheelBattery))
        assertThat(fragment.wheelName, equalTo(mockedWheelName))
        assertThat(fragment.wheelVoltage, equalTo(mockedWheelVoltage))

        verify(mockedWheelVoltage).addTextChangedListener(onUpdateVoltageListener)
        verify(mockedWidgets).addTextChangedListener(lambda.capture())
        assertThat(
            lambda.value.javaClass.name, containsString(
                "CalculatorFragment\$onUpdateVoltage\$"
            )
        )
    }

    @Test
    fun onUpdateVoltage() {
        // Given
        fragment.wheelBattery = mockedWheelBattery

        given(mockedCalculatorService.batteryOn(NAME, VOLTAGE))
            .willReturn(PERCENTAGE)

        // When
        fragment.onUpdateVoltage().invoke(VOLTAGE)

        // Then
        verify(mockedCalculatorService).batteryOn(NAME, VOLTAGE)
        verify(mockedWheelBattery).setText(PERCENTAGE)
    }
}
