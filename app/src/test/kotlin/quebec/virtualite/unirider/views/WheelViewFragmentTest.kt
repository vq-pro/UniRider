package quebec.virtualite.unirider.views

import android.app.Dialog
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
import quebec.virtualite.unirider.TestDomain.DEVICE_ADDR
import quebec.virtualite.unirider.TestDomain.DEVICE_NAME
import quebec.virtualite.unirider.TestDomain.ID
import quebec.virtualite.unirider.TestDomain.MILEAGE
import quebec.virtualite.unirider.TestDomain.MILEAGE_NEW
import quebec.virtualite.unirider.TestDomain.MILEAGE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.NAME
import quebec.virtualite.unirider.TestDomain.PERCENTAGE
import quebec.virtualite.unirider.TestDomain.PERCENTAGE_S
import quebec.virtualite.unirider.TestDomain.S18_1
import quebec.virtualite.unirider.TestDomain.S18_DISCONNECTED
import quebec.virtualite.unirider.TestDomain.VOLTAGE
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MAX
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MIN
import quebec.virtualite.unirider.TestDomain.VOLTAGE_NEW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_S
import quebec.virtualite.unirider.bluetooth.DeviceInfo
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.views.WheelViewFragment.Companion.PARAMETER_WHEEL_ID

@RunWith(MockitoJUnitRunner::class)
class WheelViewFragmentTest : BaseFragmentTest(WheelViewFragment::class.java) {

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
    lateinit var mockedTextBtName: TextView

    @Mock
    lateinit var mockedTextMileage: TextView

    @Mock
    lateinit var mockedTextName: TextView

    @Mock
    lateinit var mockedWaitDialog: Dialog

    @Before
    fun before() {
        fragment.parmWheelId = ID
        fragment.wheel = S18_1

        mockField(R.id.button_connect, mockedButtonConnect)
        mockField(R.id.button_delete, mockedButtonDelete)
        mockField(R.id.button_edit, mockedButtonEdit)
        mockField(R.id.view_name, mockedTextName)
        mockField(R.id.view_bt_name, mockedTextBtName)
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
        fragment.wheel = null
        given(mockedDb.getWheel(ID))
            .willReturn(S18_1)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedDb).getWheel(ID)

        assertThat(fragment.wheel, equalTo(S18_1))

        assertThat(fragment.textName, equalTo(mockedTextName))
        assertThat(fragment.textBtName, equalTo(mockedTextBtName))
        assertThat(fragment.textMileage, equalTo(mockedTextMileage))
        assertThat(fragment.editVoltage, equalTo(mockedEditVoltage))
        assertThat(fragment.textBattery, equalTo(mockedTextBattery))
        assertThat(fragment.buttonConnect, equalTo(mockedButtonConnect))
        assertThat(fragment.buttonEdit, equalTo(mockedButtonEdit))
        assertThat(fragment.buttonDelete, equalTo(mockedButtonDelete))

        verify(mockedTextName).text = NAME
        verify(mockedTextBtName).text = DEVICE_NAME
        verify(mockedTextMileage).text = "$MILEAGE"

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
        verify(mockedTextName, never()).text = anyString()
        verify(mockedTextBtName, never()).text = anyString()
        verify(mockedTextMileage, never()).text = anyString()
    }

    @Test
    fun onConnect_whenFirstTime_gotoScanFragment() {
        // Given
        fragment.wheel = S18_DISCONNECTED

        fragment.textBtName = mockedTextBtName
        fragment.textMileage = mockedTextMileage

        // When
        fragment.onConnect().invoke(mockedView)

        // Then
        verifyNavigatedTo(
            R.id.action_WheelViewFragment_to_WheelScanFragment,
            Pair(PARAMETER_WHEEL_ID, ID)
        )
    }

    @Test
    fun onConnect_whenNotFirstTime_connectAndUpdate() {
        // Given
        fragment.buttonConnect = mockedButtonConnect
        fragment.textBtName = mockedTextBtName
        fragment.textMileage = mockedTextMileage

        // When
        fragment.onConnect().invoke(mockedView)

        // Then
        verifyRunWithWaitDialog(fragment)
        verifyConnectorGetDeviceInfo(DEVICE_ADDR, DeviceInfo(MILEAGE_NEW_RAW, VOLTAGE_NEW_RAW))

        verify(mockedDb)
            .saveWheel(WheelEntity(ID, NAME, DEVICE_NAME, DEVICE_ADDR, MILEAGE_NEW, VOLTAGE_MIN, VOLTAGE_MAX))
        verify(mockedTextMileage).text = "$MILEAGE_NEW"
        verify(mockedEditVoltage).setText("$VOLTAGE_NEW")
    }

    @Test
    fun onDelete() {
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
        fragment.textBattery = mockedTextBattery

        given(mockedCalculatorService.percentage(fragment.wheel, 200f))
            .willReturn(2 * PERCENTAGE)

        // When
        fragment.onUpdateVoltage().invoke("200 ")

        // Then
        verify(mockedTextBattery).text = ""
    }

    class TestableWheelViewFragment(val test: WheelViewFragmentTest) : WheelViewFragment() {

        override fun initConnector() {
        }

        override fun initDB(function: () -> Unit) {
            test.initDB(this, function)
        }

        override fun navigateTo(id: Int, param: Pair<String, Any>) {
            test.navigateTo(id, param)
        }

        override fun runBackground(function: () -> Unit) {
            test.runBackground(function)
        }

        override fun runDB(function: () -> Unit) {
            test.runDB(function)
        }

        override fun runUI(function: () -> Unit) {
            test.runUI(function)
        }
    }
}
