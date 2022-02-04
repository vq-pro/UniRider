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
import quebec.virtualite.commons.android.views.WidgetUtils
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelDb
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.exceptions.WheelNotFoundException
import quebec.virtualite.unirider.services.CalculatorService
import java.lang.Float.parseFloat

@RunWith(MockitoJUnitRunner::class)
class WheelFragmentTest {

    private val MILEAGE = 1111
    private val ID = 2222L
    private val NAME = "Sherman"
    private val NEW_MILEAGE = 3333
    private val PERCENTAGE = 100.0f
    private val PERCENTAGE_S = "100.0%"
    private val VOLTAGE_S = "100.8"
    private val VOLTAGE_MAX = 100.8f
    private val VOLTAGE_MIN = 75.6f
    private val VOLTAGE = parseFloat(VOLTAGE_S)
    private val ZERO_MILEAGE = 0

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
    lateinit var mockedWheelMileage: EditText

    @Mock
    lateinit var mockedWheelName: TextView

    @Mock
    lateinit var mockedWheelVoltage: EditText

    @Mock
    lateinit var mockedWidgets: WidgetUtils

    @Captor
    lateinit var lambdaOnUpdateText: ArgumentCaptor<(String) -> Unit>

    @InjectMocks
    var fragment: WheelFragment = TestableWheelFragment(this)

    @Before
    fun before() {
        fragment.parmWheelName = NAME

        given<Any>(mockedView.findViewById(R.id.wheel_name))
            .willReturn(mockedWheelName)

        given<Any>(mockedView.findViewById(R.id.wheel_mileage))
            .willReturn(mockedWheelMileage)

        given<Any>(mockedView.findViewById(R.id.wheel_voltage))
            .willReturn(mockedWheelVoltage)

        given<Any>(mockedView.findViewById(R.id.wheel_battery))
            .willReturn(mockedWheelBattery)
    }

    @Test
    fun onViewCreated() {
        // Given
        val wheel = WheelEntity(0, NAME, MILEAGE, 0f, 0f)
        given(mockedDb.findWheel(NAME))
            .willReturn(wheel)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedDb).findWheel(NAME)

        assertThat(fragment.wheel, equalTo(wheel))
        assertThat(fragment.wheelName, equalTo(mockedWheelName))
        assertThat(fragment.wheelMileage, equalTo(mockedWheelMileage))
        assertThat(fragment.wheelVoltage, equalTo(mockedWheelVoltage))
        assertThat(fragment.wheelBattery, equalTo(mockedWheelBattery))

        verify(mockedWheelMileage).setText(MILEAGE.toString())
        verify(mockedWidgets).addTextChangedListener(eq(mockedWheelMileage), lambdaOnUpdateText.capture())
        assertThat(lambdaOnUpdateText.value.javaClass.name, containsString("WheelFragment\$onUpdateMileage\$"))

        verify(mockedWidgets).addTextChangedListener(eq(mockedWheelVoltage), lambdaOnUpdateText.capture())
        assertThat(lambdaOnUpdateText.value.javaClass.name, containsString("WheelFragment\$onUpdateVoltage\$"))
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
    fun onUpdateMileage() {
        // Given
        fragment.wheel = WheelEntity(ID, NAME, MILEAGE, VOLTAGE_MAX, VOLTAGE_MIN)

        // When
        fragment.onUpdateMileage().invoke("$NEW_MILEAGE ")

        // Then
        verify(mockedDb).saveWheels(listOf(WheelEntity(ID, NAME, NEW_MILEAGE, VOLTAGE_MAX, VOLTAGE_MIN)))
    }

    @Test
    fun onUpdateMileage_whenMileageIsEmpty_zero() {
        // Given
        fragment.wheel = WheelEntity(ID, NAME, MILEAGE, VOLTAGE_MAX, VOLTAGE_MIN)

        // When
        fragment.onUpdateMileage().invoke("")

        // Then
        verify(mockedDb).saveWheels(listOf(WheelEntity(ID, NAME, ZERO_MILEAGE, VOLTAGE_MAX, VOLTAGE_MIN)))
    }

    @Test
    fun onUpdateVoltage() {
        // Given
        fragment.wheel = WheelEntity(0, NAME, MILEAGE, VOLTAGE_MAX, VOLTAGE_MIN)
        fragment.wheelBattery = mockedWheelBattery

        given(mockedCalculatorService.percentage(fragment.wheel, VOLTAGE))
            .willReturn(PERCENTAGE)

        // When
        fragment.onUpdateVoltage().invoke("$VOLTAGE_S ")

        // Then
        verify(mockedCalculatorService).percentage(fragment.wheel, VOLTAGE)
        verify(mockedWheelBattery).text = PERCENTAGE_S
    }

    @Test
    fun onUpdateVoltage_whenBlank_noDisplay() {
        // Given
        fragment.wheel = WheelEntity(0, NAME, MILEAGE, VOLTAGE_MAX, VOLTAGE_MIN)
        fragment.wheelBattery = mockedWheelBattery

        // When
        fragment.onUpdateVoltage().invoke(" ")

        // Then
        verify(mockedCalculatorService, never()).percentage(eq(fragment.wheel), anyFloat())
        verify(mockedWheelBattery).text = ""
    }

    class TestableWheelFragment(val test: WheelFragmentTest) : WheelFragment() {

        override fun connectDb(function: () -> Unit) {
            db = test.mockedDb
            function()
        }

        override fun runDb(function: () -> Unit) {
            function()
        }
    }
}
