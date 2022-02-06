package quebec.virtualite.unirider.views

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.ArgumentMatchers.eq
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.exceptions.WheelNotFoundException
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.views.WheelViewFragment.Companion.PARAMETER_WHEEL_NAME
import java.lang.Float.parseFloat

@RunWith(MockitoJUnitRunner::class)
class WheelViewFragmentTest :
    BaseFragmentTest(WheelViewFragment::class.java.simpleName) {

    private val MILEAGE = 1111
    private val ID = 2222L
    private val NAME = "Sherman"
    private val PERCENTAGE = 100.0f
    private val PERCENTAGE_S = "100.0%"
    private val VOLTAGE_S = "100.8"
    private val VOLTAGE_MAX = 100.8f
    private val VOLTAGE_MIN = 75.6f
    private val VOLTAGE = parseFloat(VOLTAGE_S)

    @InjectMocks
    val fragment: WheelViewFragment = TestableWheelViewFragment(this)

    @Mock
    lateinit var mockedButtonEdit: Button

    @Mock
    lateinit var mockedCalculatorService: CalculatorService

    @Mock
    lateinit var mockedEditVoltage: EditText

    @Mock
    lateinit var mockedTextBattery: TextView

    @Mock
    lateinit var mockedTextMileage: TextView

    @Mock
    lateinit var mockedTextName: TextView

    @Before
    fun before() {
        fragment.parmWheelName = NAME

        mockField(R.id.button_edit, mockedButtonEdit)
        mockField(R.id.view_name, mockedTextName)
        mockField(R.id.view_mileage, mockedTextMileage)
        mockField(R.id.edit_voltage, mockedEditVoltage)
        mockField(R.id.view_battery, mockedTextBattery)
    }

    @Test
    fun onCreateView() {
        // Given
//        FIXME-1 Change this to ID
        mockArgument(fragment, PARAMETER_WHEEL_NAME, NAME)

        // When
        fragment.onCreateView(mockedInflater, mockedContainer, SAVED_INSTANCE_STATE)

        // Then
        verifyInflate(R.layout.wheel_view_fragment)
    }

    @Test
    fun onViewCreated() {
        // Given
        val wheel = WheelEntity(ID, NAME, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)
        given(mockedDb.findWheel(NAME))
            .willReturn(wheel)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedDb).findWheel(NAME)

        assertThat(fragment.wheel, equalTo(wheel))

        assertThat(fragment.textName, equalTo(mockedTextName))
        verify(fragment.textName).setText(NAME)

        assertThat(fragment.textMileage, equalTo(mockedTextMileage))
        verify(mockedTextMileage).setText("$MILEAGE")

        assertThat(fragment.editVoltage, equalTo(mockedEditVoltage))
        verifyOnUpdateText(mockedEditVoltage, "onUpdateVoltage")

        assertThat(fragment.textBattery, equalTo(mockedTextBattery))

        assertThat(fragment.buttonEdit, equalTo(mockedButtonEdit))
        verifyOnClick(mockedButtonEdit, "onEdit")
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
    fun onEdit() {
        // Given
        fragment.wheel = WheelEntity(ID, NAME, 0, 0f, 0f)

        // When
        fragment.onEdit().invoke(mockedView)

        // Then
        verifyNavigatedTo(R.id.action_WheelViewFragment_to_WheelEditFragment, Pair(PARAMETER_WHEEL_NAME, NAME))
    }

    @Test
    fun onUpdateVoltage() {
        // Given
        fragment.wheel = WheelEntity(0, NAME, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)
        fragment.textBattery = mockedTextBattery

        given(mockedCalculatorService.percentage(fragment.wheel, VOLTAGE))
            .willReturn(PERCENTAGE)

        // When
        fragment.onUpdateVoltage().invoke("$VOLTAGE_S ")

        // Then
        verify(mockedCalculatorService).percentage(fragment.wheel, VOLTAGE)
        verify(mockedTextBattery).text = PERCENTAGE_S
    }

    @Test
    fun onUpdateVoltage_whenBlank_noDisplay() {
        // Given
        fragment.wheel = WheelEntity(0, NAME, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)
        fragment.textBattery = mockedTextBattery

        // When
        fragment.onUpdateVoltage().invoke(" ")

        // Then
        verify(mockedCalculatorService, never()).percentage(eq(fragment.wheel), anyFloat())
        verify(mockedTextBattery).text = ""
    }

    class TestableWheelViewFragment(val test: WheelViewFragmentTest) : WheelViewFragment() {

        override fun connectDb(function: () -> Unit) {
            test.connectDb(this, function)
        }

        override fun navigateTo(id: Int, param: Pair<String, String>) {
            test.navigateTo(id, param)
        }

        override fun runDb(function: () -> Unit) {
            test.runDb(function)
        }
    }
}
