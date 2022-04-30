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
import org.mockito.ArgumentMatchers.anyInt
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
import quebec.virtualite.unirider.TestDomain.KM
import quebec.virtualite.unirider.TestDomain.KM_S
import quebec.virtualite.unirider.TestDomain.LABEL_KM
import quebec.virtualite.unirider.TestDomain.MILEAGE
import quebec.virtualite.unirider.TestDomain.MILEAGE_NEW
import quebec.virtualite.unirider.TestDomain.MILEAGE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.NAME
import quebec.virtualite.unirider.TestDomain.PERCENTAGE
import quebec.virtualite.unirider.TestDomain.PERCENTAGE_S
import quebec.virtualite.unirider.TestDomain.PREMILEAGE
import quebec.virtualite.unirider.TestDomain.REMAINING_RANGE
import quebec.virtualite.unirider.TestDomain.REMAINING_RANGE_S
import quebec.virtualite.unirider.TestDomain.S18_1
import quebec.virtualite.unirider.TestDomain.S18_DISCONNECTED
import quebec.virtualite.unirider.TestDomain.TEMPERATURE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.TOTAL_RANGE
import quebec.virtualite.unirider.TestDomain.TOTAL_RANGE_S
import quebec.virtualite.unirider.TestDomain.VOLTAGE
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MAX
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MIN
import quebec.virtualite.unirider.TestDomain.VOLTAGE_NEW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_S
import quebec.virtualite.unirider.bluetooth.WheelInfo
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.services.CalculatorService.EstimatedValues
import quebec.virtualite.unirider.views.WheelViewFragment.Companion.PARAMETER_WHEEL_ID

@RunWith(MockitoJUnitRunner::class)
class WheelViewFragmentTest : BaseFragmentTest(WheelViewFragment::class.java) {

    @InjectMocks
    lateinit var fragment: WheelViewFragment

    @Mock
    lateinit var mockedButtonConnect: Button

    @Mock
    lateinit var mockedButtonDelete: Button

    @Mock
    lateinit var mockedButtonEdit: Button

    @Mock
    lateinit var mockedCalculatorService: CalculatorService

    @Mock
    lateinit var mockedEditKm: EditText

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
    lateinit var mockedTextRemainingRange: TextView

    @Mock
    lateinit var mockedTextTotalRange: TextView

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
        mockField(R.id.edit_km, mockedEditKm)
        mockField(R.id.view_remaining_range, mockedTextRemainingRange)
        mockField(R.id.view_total_range, mockedTextTotalRange)

        mockExternal()
        mockFragments()
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
        assertThat(fragment.editKm, equalTo(mockedEditKm))
        assertThat(fragment.textRemainingRange, equalTo(mockedTextRemainingRange))
        assertThat(fragment.textTotalRange, equalTo(mockedTextTotalRange))
        assertThat(fragment.buttonConnect, equalTo(mockedButtonConnect))
        assertThat(fragment.buttonEdit, equalTo(mockedButtonEdit))
        assertThat(fragment.buttonDelete, equalTo(mockedButtonDelete))

        verify(mockedTextName).text = NAME
        verify(mockedTextBtName).text = DEVICE_NAME
        verify(mockedTextMileage).text = "${PREMILEAGE + MILEAGE}"

        verifyOnUpdateText(mockedEditKm, "onUpdateKm")
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

        injectMocks()

        // When
        fragment.onConnect().invoke(mockedView)

        // Then
        verify(mockedFragments).navigateTo(
            R.id.action_WheelViewFragment_to_WheelScanFragment,
            Pair(PARAMETER_WHEEL_ID, ID)
        )
    }

    @Test
    fun onConnect_whenNotFirstTime_connectAndUpdate() {
        // Given
        injectMocks()

        // When
        fragment.onConnect().invoke(mockedView)

        // Then
        val connectionPayload = WheelInfo(MILEAGE_NEW_RAW, TEMPERATURE_NEW_RAW, VOLTAGE_NEW_RAW)

        verifyRunWithWaitDialog()
        verifyConnectorGetDeviceInfo(DEVICE_ADDR, connectionPayload)
        verifyDoneWaiting(connectionPayload)

        verify(mockedDb).saveWheel(WheelEntity(ID, NAME, DEVICE_NAME, DEVICE_ADDR, PREMILEAGE, MILEAGE_NEW, VOLTAGE_MIN, VOLTAGE_MAX))
        verify(mockedTextMileage).text = "${PREMILEAGE + MILEAGE_NEW}"
        verify(mockedEditVoltage).setText("$VOLTAGE_NEW")
    }

    @Test
    fun onDelete() {
        // When
        fragment.onDelete().invoke(mockedView)

        // Then
        verify(mockedFragments).navigateTo(
            R.id.action_WheelViewFragment_to_WheelDeleteConfirmationFragment,
            Pair(PARAMETER_WHEEL_ID, ID)
        )
    }

    @Test
    fun onEdit() {
        // When
        fragment.onEdit().invoke(mockedView)

        // Then
        verify(mockedFragments).navigateTo(
            R.id.action_WheelViewFragment_to_WheelEditFragment,
            Pair(PARAMETER_WHEEL_ID, ID)
        )
    }

    @Test
    fun onUpdateKm() {
        // Given
        injectMocks()
        mockVoltage(" ")

        // When
        fragment.onUpdateKm().invoke("$KM_S ")

        // Then
        verify(mockedCalculatorService, never()).estimatedValues(fragment.wheel, VOLTAGE, KM)
        verify(mockedTextRemainingRange).text = ""
        verify(mockedTextTotalRange).text = ""
    }

    @Test
    fun onUpdateKm_whenBlank_noDisplay() {
        // Given
        injectMocks()
        mockVoltage(VOLTAGE_S)

        // When
        fragment.onUpdateKm().invoke(" ")

        // Then
        verify(mockedCalculatorService, never()).estimatedValues(eq(fragment.wheel), anyFloat(), anyInt())
        verify(mockedTextRemainingRange).text = ""
        verify(mockedTextTotalRange).text = ""
    }

    @Test
    fun onUpdateKm_withVoltage_updateEstimatedValues() {
        // Given
        injectMocks()
        mockVoltage(VOLTAGE_S)

        given(mockedCalculatorService.estimatedValues(fragment.wheel, VOLTAGE, KM))
            .willReturn(EstimatedValues(REMAINING_RANGE, TOTAL_RANGE))

        // When
        fragment.onUpdateKm().invoke("$KM_S ")

        // Then
        verify(mockedCalculatorService).estimatedValues(fragment.wheel, VOLTAGE, KM)
        verify(mockedTextRemainingRange).text = "$REMAINING_RANGE_S $LABEL_KM"
        verify(mockedTextTotalRange).text = "$TOTAL_RANGE_S $LABEL_KM"
    }

    @Test
    fun onUpdateVoltage() {
        // Given
        injectMocks()
        mockKm(" ")

        given(mockedCalculatorService.percentage(fragment.wheel, VOLTAGE))
            .willReturn(PERCENTAGE)

        // When
        fragment.onUpdateVoltage().invoke("$VOLTAGE_S ")

        // Then
        verify(mockedCalculatorService).percentage(fragment.wheel, VOLTAGE)
        verify(mockedTextBattery).text = PERCENTAGE_S
        verify(mockedTextRemainingRange).text = ""
        verify(mockedTextTotalRange).text = ""
    }

    @Test
    fun onUpdateVoltage_whenBlank_noDisplay() {
        // Given
        injectMocks()
        mockKm(" ")

        // When
        fragment.onUpdateVoltage().invoke(" ")

        // Then
        verify(mockedCalculatorService, never()).percentage(eq(fragment.wheel), anyFloat())
        verify(mockedTextBattery).text = ""
        verify(mockedTextRemainingRange).text = ""
        verify(mockedTextTotalRange).text = ""
    }

    @Test
    fun onUpdateVoltage_whenOverTheTop_noDisplay() {
        // Given
        injectMocks()
        mockKm(" ")

        given(mockedCalculatorService.percentage(fragment.wheel, 200f))
            .willReturn(2 * PERCENTAGE)

        // When
        fragment.onUpdateVoltage().invoke("200 ")

        // Then
        verify(mockedTextBattery).text = ""
        verify(mockedTextRemainingRange).text = ""
        verify(mockedTextTotalRange).text = ""
    }

    @Test
    fun onUpdateVoltage_withKm_updateEstimatedValues() {
        // Given
        injectMocks()
        mockKm(KM_S)

        given(mockedCalculatorService.estimatedValues(fragment.wheel, VOLTAGE, KM))
            .willReturn(EstimatedValues(REMAINING_RANGE, TOTAL_RANGE))

        given(mockedCalculatorService.percentage(fragment.wheel, VOLTAGE))
            .willReturn(PERCENTAGE)

        // When
        fragment.onUpdateVoltage().invoke("$VOLTAGE_S ")

        // Then
        verify(mockedCalculatorService).estimatedValues(fragment.wheel, VOLTAGE, KM)
        verify(mockedCalculatorService).percentage(fragment.wheel, VOLTAGE)

        verify(mockedTextBattery).text = PERCENTAGE_S
        verify(mockedTextRemainingRange).text = "$REMAINING_RANGE_S $LABEL_KM"
        verify(mockedTextTotalRange).text = "$TOTAL_RANGE_S $LABEL_KM"
    }

    private fun injectMocks() {
        fragment.buttonConnect = mockedButtonConnect
        fragment.editVoltage = mockedEditVoltage
        fragment.textBattery = mockedTextBattery
        fragment.textBtName = mockedTextBtName
        fragment.textMileage = mockedTextMileage
        fragment.textRemainingRange = mockedTextRemainingRange
        fragment.textTotalRange = mockedTextTotalRange
    }

    private fun mockKm(km: String) {
        fragment.editKm = mockedEditKm

        given(mockedWidgets.text(mockedEditKm))
            .willReturn(km.trim())
    }

    private fun mockVoltage(voltage: String) {
        fragment.editVoltage = mockedEditVoltage

        given(mockedWidgets.text(mockedEditVoltage))
            .willReturn(voltage.trim())
    }
}
