package quebec.virtualite.unirider.views

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verifyNoInteractions
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.services.WheelScanner
import quebec.virtualite.unirider.views.WheelViewFragment.Companion.PARAMETER_WHEEL_ID
import java.lang.Float.parseFloat

@RunWith(MockitoJUnitRunner::class)
class WheelViewFragmentTest :
    BaseFragmentTest(WheelViewFragment::class.java) {

    private val ID = 1111L
    private val MILEAGE = 2222
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
    lateinit var mockedButtonConnect: Button

    @Mock
    lateinit var mockedButtonDelete: Button

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

    @Mock
    lateinit var mockedWheelScanner: WheelScanner

    @Before
    fun before() {
        fragment.parmWheelId = ID

        mockField(R.id.button_connect, mockedButtonConnect)
        mockField(R.id.button_delete, mockedButtonDelete)
        mockField(R.id.button_edit, mockedButtonEdit)
        mockField(R.id.view_name, mockedTextName)
        mockField(R.id.view_mileage, mockedTextMileage)
        mockField(R.id.edit_voltage, mockedEditVoltage)
        mockField(R.id.view_battery, mockedTextBattery)
    }

    @Test
    fun onCreateView() {
        // Given
        mockArgument(fragment, PARAMETER_WHEEL_ID, ID)

        // When
        fragment.onCreateView(mockedInflater, mockedContainer, SAVED_INSTANCE_STATE)

        // Then
        verifyInflate(R.layout.wheel_view_fragment)
    }

    @Test
    fun onViewCreated() {
        // Given
        val wheel = WheelEntity(ID, NAME, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)
        given(mockedDb.getWheel(ID))
            .willReturn(wheel)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedDb).getWheel(ID)

        assertThat(fragment.wheel, equalTo(wheel))

        assertThat(fragment.textName, equalTo(mockedTextName))
        assertThat(fragment.textMileage, equalTo(mockedTextMileage))
        assertThat(fragment.editVoltage, equalTo(mockedEditVoltage))
        assertThat(fragment.textBattery, equalTo(mockedTextBattery))
        assertThat(fragment.buttonConnect, equalTo(mockedButtonConnect))
        assertThat(fragment.buttonEdit, equalTo(mockedButtonEdit))
        assertThat(fragment.buttonDelete, equalTo(mockedButtonDelete))

        verify(mockedTextName).setText(NAME)
        verify(mockedTextMileage).setText("$MILEAGE")

        verifyOnUpdateText(mockedEditVoltage, "onUpdateVoltage")
        verifyOnClick(mockedButtonConnect, "onConnect")
        verifyOnClick(mockedButtonEdit, "onEdit")
        verifyOnLongClick(mockedButtonDelete, "onDelete")
    }

    @Test
    fun onViewCreated_whenWheelIsntFound() {
        // Given
        given(mockedDb.getWheel(ID))
            .willReturn(null)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verifyNoInteractions(mockedWidgets)
        verify(mockedTextName, never()).setText(anyString())
        verify(mockedTextMileage, never()).setText(anyString())
    }

    @Test
    fun onConnect() {
        // Given
        fragment.textMileage = mockedTextMileage

        // When
        fragment.onConnect().invoke(mockedView)

        // Then
        verify(mockedWheelScanner).scan()

        // FIXME-1 Remove this when we can actually connect
        verify(mockedTextMileage).setText("655")
    }

    @Test
    fun onDelete() {
        // Given
        fragment.wheel = WheelEntity(ID, NAME, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)

        // When
        fragment.onDelete().invoke(mockedView)

        // Then
        verifyNavigatedTo(
            R.id.action_WheelViewFragment_to_WheelDeleteConfirmationFragment,
            Pair(PARAMETER_WHEEL_ID, ID)
        )
    }

    @Test
    fun onEdit() {
        // Given
        fragment.wheel = WheelEntity(ID, NAME, 0, 0f, 0f)

        // When
        fragment.onEdit().invoke(mockedView)

        // Then
        verifyNavigatedTo(
            R.id.action_WheelViewFragment_to_WheelEditFragment,
            Pair(PARAMETER_WHEEL_ID, ID)
        )
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

    @Test
    fun onUpdateVoltage_whenOverTheTop_noDisplay() {
        // Given
        fragment.wheel = WheelEntity(0, NAME, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)
        fragment.textBattery = mockedTextBattery

        given(mockedCalculatorService.percentage(fragment.wheel, 200f))
            .willReturn(2 * PERCENTAGE)

        // When
        fragment.onUpdateVoltage().invoke("200 ")

        // Then
        verify(mockedTextBattery).text = ""
    }

    class TestableWheelViewFragment(val test: WheelViewFragmentTest) : WheelViewFragment() {

        override fun connectDb(function: () -> Unit) {
            test.connectDb(this, function)
        }

//        override fun navigateBack() {
//            test.navigateBack()
//        }

        override fun navigateTo(id: Int, param: Pair<String, Any>) {
            test.navigateTo(id, param)
        }

//        override fun runDb(function: () -> Unit) {
//            test.runDb(function)
//        }
    }
}
