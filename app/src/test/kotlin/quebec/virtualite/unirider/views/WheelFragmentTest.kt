package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.StringContains.containsString
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.ArgumentMatchers.eq
import org.mockito.BDDMockito.given
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelDb
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.exceptions.WheelNotFoundException
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.utils.WidgetUtils
import java.lang.Float.parseFloat

private const val NAME = "Sherman"
private const val PERCENTAGE = 100.0f
private const val PERCENTAGE_S = "100.0%"
private const val VOLTAGE_S = "100.8"
private const val VOLTAGE_MAX = 100.8f
private const val VOLTAGE_MIN = 75.6f

@RunWith(MockitoJUnitRunner::class)
class WheelFragmentTest {

    @Mock
    lateinit var mockedBundle: Bundle

    @Mock
    lateinit var mockedCalculatorService: CalculatorService

    @Mock
    lateinit var mockedDb: WheelDb

    @Mock
    lateinit var mockedView: View

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
    var fragment: WheelFragment = TestableWheelFragment(this)

    @Before
    fun init() {
        fragment.parmWheelName = NAME
    }

    @Test
    fun onViewCreated() {
        // Given
        val wheel = WheelEntity(0, NAME, 0, 0f, 0f)
        given(mockedDb.findWheel(NAME))
            .willReturn(wheel)

        given<Any>(mockedView.findViewById(R.id.wheel_battery))
            .willReturn(mockedWheelBattery)

        given<Any>(mockedView.findViewById(R.id.wheel_name))
            .willReturn(mockedWheelName)

        given<Any>(mockedView.findViewById(R.id.wheel_voltage))
            .willReturn(mockedWheelVoltage)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedDb).findWheel(NAME)

        assertThat(fragment.wheel, equalTo(wheel))
        assertThat(fragment.wheelBattery, equalTo(mockedWheelBattery))
        assertThat(fragment.wheelName, equalTo(mockedWheelName))
        assertThat(fragment.wheelVoltage, equalTo(mockedWheelVoltage))

        verify(mockedWidgets).addTextChangedListener(eq(mockedWheelVoltage), lambda.capture())
        assertThat(lambda.value.javaClass.name, containsString("WheelFragment\$onUpdateVoltage\$"))
    }

    @Test
    fun onViewCreated_whenWheelIsntFound() {
        // Given
        given(mockedDb.findWheel(NAME))
            .willReturn(null)

        // When
        val result = { fragment.onViewCreated(mockedView, mockedBundle) }

        // Then
        assertThrows(WheelNotFoundException::class.java, result)
    }

    @Test
    fun onUpdateVoltage() {
        // Given
        fragment.wheel = WheelEntity(0, NAME, 0, VOLTAGE_MAX, VOLTAGE_MIN)
        fragment.wheelBattery = mockedWheelBattery

        given(mockedCalculatorService.batteryOn(fragment.wheel, VOLTAGE))
            .willReturn(PERCENTAGE)

        // When
        fragment.onUpdateVoltage().invoke("$VOLTAGE_S ")

        // Then
        verify(mockedCalculatorService).batteryOn(fragment.wheel, VOLTAGE)
        verify(mockedWheelBattery).text = PERCENTAGE_S
    }

    @Test
    fun onUpdateVoltage_whenBlank_noDisplay() {
        // Given
        fragment.wheel = WheelEntity(0, NAME, 0, VOLTAGE_MAX, VOLTAGE_MIN)
        fragment.wheelBattery = mockedWheelBattery

        // When
        fragment.onUpdateVoltage().invoke(" ")

        // Then
        verify(mockedCalculatorService, never()).batteryOn(eq(fragment.wheel), anyFloat())
        verify(mockedWheelBattery).text = ""
    }

    class TestableWheelFragment(val test: WheelFragmentTest) : WheelFragment() {

        override fun connectDb() {
            db = test.mockedDb
        }

        override fun subThread(function: () -> Unit) {
            function()
        }
    }
}

private val VOLTAGE = parseFloat(VOLTAGE_S)

