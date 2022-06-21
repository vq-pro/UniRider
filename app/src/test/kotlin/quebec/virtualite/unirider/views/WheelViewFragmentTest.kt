package quebec.virtualite.unirider.views

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
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
import quebec.virtualite.unirider.TestDomain.KM_NEW
import quebec.virtualite.unirider.TestDomain.KM_NEW_RAW
import quebec.virtualite.unirider.TestDomain.LABEL_KM
import quebec.virtualite.unirider.TestDomain.LABEL_WH_PER_KM
import quebec.virtualite.unirider.TestDomain.MILEAGE
import quebec.virtualite.unirider.TestDomain.MILEAGE_NEW
import quebec.virtualite.unirider.TestDomain.MILEAGE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.NAME
import quebec.virtualite.unirider.TestDomain.PERCENTAGE
import quebec.virtualite.unirider.TestDomain.PREMILEAGE
import quebec.virtualite.unirider.TestDomain.REMAINING_RANGE
import quebec.virtualite.unirider.TestDomain.REMAINING_RANGE_ZERO
import quebec.virtualite.unirider.TestDomain.S18_1
import quebec.virtualite.unirider.TestDomain.S18_DISCONNECTED
import quebec.virtualite.unirider.TestDomain.TEMPERATURE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.TOTAL_RANGE
import quebec.virtualite.unirider.TestDomain.VOLTAGE
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MAX
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MIN
import quebec.virtualite.unirider.TestDomain.VOLTAGE_NEW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_RESERVE
import quebec.virtualite.unirider.TestDomain.VOLTAGE_START
import quebec.virtualite.unirider.TestDomain.WH
import quebec.virtualite.unirider.TestDomain.WH_PER_KM
import quebec.virtualite.unirider.bluetooth.WheelInfo
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.services.CalculatorService.EstimatedValues
import quebec.virtualite.unirider.views.BaseFragment.Companion.PARAMETER_WHEEL_ID

@RunWith(MockitoJUnitRunner::class)
class WheelViewFragmentTest : BaseFragmentTest(WheelViewFragment::class.java) {

    @InjectMocks
    lateinit var fragment: WheelViewFragment

    @Mock
    lateinit var mockedButtonConnect: Button

    @Mock
    lateinit var mockedButtonEdit: Button

    @Mock
    lateinit var mockedCalculatorService: CalculatorService

    @Mock
    lateinit var mockedEditKm: EditText

    @Mock
    lateinit var mockedEditVoltageActual: EditText

    @Mock
    lateinit var mockedEditVoltageStart: EditText

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

    @Mock
    lateinit var mockedTextWhPerKm: TextView

    @Before
    fun before() {
        fragment.parmWheelId = ID
        fragment.wheel = S18_1

        mockExternal()
        mockFields()
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
        given(mockedDb.getWheel(anyLong()))
            .willReturn(S18_1)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedDb).getWheel(ID)

        assertThat(fragment.wheel, equalTo(S18_1))

        assertThat(fragment.buttonConnect, equalTo(mockedButtonConnect))
        assertThat(fragment.buttonEdit, equalTo(mockedButtonEdit))
        assertThat(fragment.editKm, equalTo(mockedEditKm))
        assertThat(fragment.editVoltageActual, equalTo(mockedEditVoltageActual))
        assertThat(fragment.editVoltageStart, equalTo(mockedEditVoltageStart))
        assertThat(fragment.textBattery, equalTo(mockedTextBattery))
        assertThat(fragment.textBtName, equalTo(mockedTextBtName))
        assertThat(fragment.textMileage, equalTo(mockedTextMileage))
        assertThat(fragment.textName, equalTo(mockedTextName))
        assertThat(fragment.textRemainingRange, equalTo(mockedTextRemainingRange))
        assertThat(fragment.textTotalRange, equalTo(mockedTextTotalRange))
        assertThat(fragment.textWhPerKm, equalTo(mockedTextWhPerKm))

        verify(mockedTextName).text = NAME
        verify(mockedTextBtName).text = DEVICE_NAME
        verify(mockedEditVoltageStart).setText("$VOLTAGE_START")
        verify(mockedTextMileage).text = "${PREMILEAGE + MILEAGE}"

        verifyOnUpdateText(mockedEditVoltageStart, "onUpdateVoltageStart")
        verifyOnUpdateText(mockedEditKm, "onUpdateKm")
        verifyOnUpdateText(mockedEditVoltageActual, "onUpdateVoltageActual")
        verifyOnClick(mockedButtonConnect, "onConnect")
        verifyOnClick(mockedButtonEdit, "onEdit")
    }

    @Test
    fun onViewCreated_whenKmAndVoltageAreSet_updateEstimatedValues() {
        // Given
        fragment.wheel = null

        mockKm("$KM ")
        mockVoltageActual("$VOLTAGE ")

        given(mockedDb.getWheel(anyLong()))
            .willReturn(S18_1)

        given(mockedCalculatorService.percentage(any(), anyFloat()))
            .willReturn(PERCENTAGE)

        given(mockedCalculatorService.estimatedValues(any(), anyFloat(), anyFloat()))
            .willReturn(EstimatedValues(REMAINING_RANGE, TOTAL_RANGE, WH_PER_KM))

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verifyUpdatePercentage()
        verifyUpdateEstimatedValues(VOLTAGE, "$REMAINING_RANGE")
    }

    @Test
    fun onViewCreated_whenVoltageStartWasNeverSaved_useVoltageMax() {
        // Given
        fragment.wheel = null

        mockKm("$KM ")
        mockVoltageActual("$VOLTAGE ")

        given(mockedDb.getWheel(anyLong()))
            .willReturn(S18_1.copy(voltageStart = null))

        given(mockedCalculatorService.percentage(any(), anyFloat()))
            .willReturn(PERCENTAGE)

        given(mockedCalculatorService.estimatedValues(any(), anyFloat(), anyFloat()))
            .willReturn(EstimatedValues(REMAINING_RANGE, TOTAL_RANGE, WH_PER_KM))

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verifyUpdatePercentage()
        verifyUpdateEstimatedValues(VOLTAGE, "$REMAINING_RANGE")
    }

    @Test
    fun onViewCreated_whenWheelIsntFound() {
        // Given
        given(mockedDb.getWheel(anyLong()))
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
        val connectionPayload = WheelInfo(KM_NEW_RAW, MILEAGE_NEW_RAW, TEMPERATURE_NEW_RAW, VOLTAGE_NEW_RAW)

        verifyRunWithWaitDialog()
        verifyConnectorGetDeviceInfo(DEVICE_ADDR, connectionPayload)
        verifyDoneWaiting(connectionPayload)

        verify(mockedDb).saveWheel(
            WheelEntity(
                ID, NAME, DEVICE_NAME, DEVICE_ADDR,
                PREMILEAGE, MILEAGE_NEW, WH,
                VOLTAGE_MAX, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_START
            )
        )
        verify(mockedEditKm).setText("$KM_NEW")
        verify(mockedTextMileage).text = "${PREMILEAGE + MILEAGE_NEW}"
        verify(mockedEditVoltageActual).setText("$VOLTAGE_NEW")
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
        mockVoltageActual(" ")

        // When
        fragment.onUpdateKm().invoke("$KM ")

        // Then
        verifyClearEstimatedValues()
    }

    @Test
    fun onUpdateKm_whenBlank_noDisplay() {
        // Given
        injectMocks()
        mockVoltageActual("$VOLTAGE")

        // When
        fragment.onUpdateKm().invoke(" ")

        // Then
        verifyClearEstimatedValues()
    }

    @Test
    fun onUpdateKm_whenNonNumeric_noDisplay() {
        // Given
        injectMocks()
        mockVoltageActual("$VOLTAGE")

        // When
        fragment.onUpdateKm().invoke("a ")

        // Then
        verifyClearEstimatedValues()
    }

    @Test
    fun onUpdateKm_whenZero_noDisplay() {
        // Given
        injectMocks()
        mockVoltageActual("$VOLTAGE")

        // When
        fragment.onUpdateKm().invoke("0.0 ")

        // Then
        verifyClearEstimatedValues()
    }

    @Test
    fun onUpdateKm_withVoltage_updateEstimatedValues() {
        // Given
        injectMocks()
        mockVoltageActual("$VOLTAGE")

        given(mockedCalculatorService.estimatedValues(any(), anyFloat(), anyFloat()))
            .willReturn(EstimatedValues(REMAINING_RANGE, TOTAL_RANGE, WH_PER_KM))

        // When
        fragment.onUpdateKm().invoke("$KM ")

        // Then
        verifyUpdateEstimatedValues(VOLTAGE, "$REMAINING_RANGE")
    }

    @Test
    fun onUpdateKm_withVoltageLowerThanReserve_updateEstimatedValues() {
        // Given
        val voltage = VOLTAGE_RESERVE - 0.1f

        injectMocks()
        mockVoltageActual("$voltage")

        given(mockedCalculatorService.estimatedValues(any(), anyFloat(), anyFloat()))
            .willReturn(EstimatedValues(0f, TOTAL_RANGE, WH_PER_KM))

        // When
        fragment.onUpdateKm().invoke("$KM ")

        // Then
        verifyUpdateEstimatedValues(voltage, "$REMAINING_RANGE_ZERO")
    }

    @Test
    fun onUpdateVoltageActual() {
        // Given
        injectMocks()
        mockKm(" ")

        given(mockedCalculatorService.percentage(any(), anyFloat()))
            .willReturn(PERCENTAGE)

        // When
        fragment.onUpdateVoltageActual().invoke("$VOLTAGE ")

        // Then
        verifyUpdatePercentage()
        verifyClearEstimatedValues()
    }

    @Test
    fun onUpdateVoltageActual_whenBlank_noDisplay() {
        // Given
        injectMocks()
        mockKm(" ")

        // When
        fragment.onUpdateVoltageActual().invoke(" ")

        // Then
        verifyClearPercentage()
        verifyClearEstimatedValues()
    }

    @Test
    fun onUpdateVoltageActual_whenLowVoltage_noDisplay() {
        // Given
        injectMocks()
        mockKm("$KM")

        // When
        fragment.onUpdateVoltageActual().invoke("${fragment.wheel!!.voltageMin - 0.1f} ")

        // Then
        verifyClearPercentage()
        verifyClearEstimatedValues()
    }

    @Test
    fun onUpdateVoltageActual_whenHighVoltage_noDisplay() {
        // Given
        injectMocks()
        mockKm("$KM")

        // When
        fragment.onUpdateVoltageActual().invoke("${fragment.wheel!!.voltageMax + 0.1f} ")

        // Then
        verifyClearPercentage()
        verifyClearEstimatedValues()
    }

    @Test
    fun onUpdateVoltageActual_whenNonNumeric_noDisplay() {
        // Given
        injectMocks()
        mockKm(" ")

        // When
        fragment.onUpdateVoltageActual().invoke("ab ")

        // Then
        verifyClearPercentage()
        verifyClearEstimatedValues()
    }

    @Test
    fun onUpdateVoltageActual_withKm_updateEstimatedValues() {
        // Given
        injectMocks()
        mockKm("$KM ")

        given(mockedCalculatorService.estimatedValues(any(), anyFloat(), anyFloat()))
            .willReturn(EstimatedValues(REMAINING_RANGE, TOTAL_RANGE, WH_PER_KM))

        given(mockedCalculatorService.percentage(any(), anyFloat()))
            .willReturn(PERCENTAGE)

        // When
        fragment.onUpdateVoltageActual().invoke("$VOLTAGE ")

        // Then
        verifyUpdatePercentage()
        verifyUpdateEstimatedValues(VOLTAGE, "$REMAINING_RANGE")
    }

    @Test
    fun onUpdateVoltageStart() {
        // Given
        injectMocks()
        mockKm("$KM ")

        given(mockedCalculatorService.estimatedValues(any(), anyFloat(), anyFloat()))
            .willReturn(EstimatedValues(REMAINING_RANGE, TOTAL_RANGE, WH_PER_KM))

        given(mockedCalculatorService.percentage(any(), anyFloat()))
            .willReturn(PERCENTAGE)

        // When
        fragment.onUpdateVoltageStart().invoke("$VOLTAGE_START ")

        // Then
        verifyUpdateEstimatedValues(VOLTAGE_START, "$REMAINING_RANGE")

        verify(mockedDb).saveWheel(S18_1.copy(voltageStart = VOLTAGE_START))
    }

    @Test
    fun onUpdateVoltageStart_whenBlank_noDisplay() {
        // Given
        injectMocks()
        mockKm(" ")

        // When
        fragment.onUpdateVoltageStart().invoke(" ")

        // Then
        verifyClearPercentage()
        verifyClearEstimatedValues()

        verify(mockedDb, never()).saveWheel(any())
    }

    private fun injectMocks() {
        fragment.buttonConnect = mockedButtonConnect
        fragment.editKm = mockedEditKm
        fragment.editVoltageActual = mockedEditVoltageActual
        fragment.editVoltageStart = mockedEditVoltageStart
        fragment.textBattery = mockedTextBattery
        fragment.textBtName = mockedTextBtName
        fragment.textMileage = mockedTextMileage
        fragment.textRemainingRange = mockedTextRemainingRange
        fragment.textTotalRange = mockedTextTotalRange
        fragment.textWhPerKm = mockedTextWhPerKm
    }

    private fun mockFields() {
        mockField(R.id.button_connect, mockedButtonConnect)
        mockField(R.id.button_edit, mockedButtonEdit)
        mockField(R.id.edit_km, mockedEditKm)
        mockField(R.id.edit_voltage_actual, mockedEditVoltageActual)
        mockField(R.id.edit_voltage_start, mockedEditVoltageStart)
        mockField(R.id.view_battery, mockedTextBattery)
        mockField(R.id.view_bt_name, mockedTextBtName)
        mockField(R.id.view_mileage, mockedTextMileage)
        mockField(R.id.view_name, mockedTextName)
        mockField(R.id.view_remaining_range, mockedTextRemainingRange)
        mockField(R.id.view_total_range, mockedTextTotalRange)
        mockField(R.id.view_wh_per_km, mockedTextWhPerKm)
    }

    private fun mockKm(km: String) {
        fragment.editKm = mockedEditKm

        given(mockedWidgets.text(mockedEditKm))
            .willReturn(km.trim())
    }

    private fun mockVoltageActual(voltage: String) {
        fragment.editVoltageActual = mockedEditVoltageActual

        given(mockedWidgets.text(mockedEditVoltageActual))
            .willReturn(voltage.trim())
    }

    private fun verifyClearEstimatedValues() {
        verify(mockedCalculatorService, never()).estimatedValues(any(), anyFloat(), anyFloat())

        verify(mockedTextRemainingRange).text = ""
        verify(mockedTextTotalRange).text = ""
        verify(mockedTextWhPerKm).text = ""
    }

    private fun verifyClearPercentage() {
        verify(mockedCalculatorService, never()).percentage(any(), anyFloat())
        verify(mockedTextBattery).text = ""
    }

    private fun verifyUpdateEstimatedValues(voltage: Float, remainingRange: String) {
        verify(mockedCalculatorService).estimatedValues(fragment.wheel, voltage, KM)

        verify(mockedTextRemainingRange).text = "$remainingRange $LABEL_KM"
        verify(mockedTextTotalRange).text = "$TOTAL_RANGE $LABEL_KM"
        verify(mockedTextWhPerKm).text = "$WH_PER_KM $LABEL_WH_PER_KM"
    }

    private fun verifyUpdatePercentage() {
        verify(mockedCalculatorService).percentage(fragment.wheel, VOLTAGE)
        verify(mockedTextBattery).text = "$PERCENTAGE%"
    }
}
