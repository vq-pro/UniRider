package quebec.virtualite.unirider.views

import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.BDDMockito.doNothing
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.commons.android.utils.ArrayListUtils.setList
import quebec.virtualite.commons.android.utils.NumberUtils.floatOf
import quebec.virtualite.commons.android.utils.NumberUtils.round
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.TestDomain.DEVICE_ADDR3
import quebec.virtualite.unirider.TestDomain.ID
import quebec.virtualite.unirider.TestDomain.KM
import quebec.virtualite.unirider.TestDomain.KM_NEW_RAW
import quebec.virtualite.unirider.TestDomain.MILEAGE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.NAME
import quebec.virtualite.unirider.TestDomain.S18_1
import quebec.virtualite.unirider.TestDomain.SHERMAN_MAX_3
import quebec.virtualite.unirider.TestDomain.TEMPERATURE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.VOLTAGE
import quebec.virtualite.unirider.TestDomain.VOLTAGE_FULL3
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MIN3
import quebec.virtualite.unirider.TestDomain.VOLTAGE_NEW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.WHS_PER_KM
import quebec.virtualite.unirider.TestDomain.WH_PER_KM_INDEX
import quebec.virtualite.unirider.TestDomain.WH_PER_KM_UP
import quebec.virtualite.unirider.TestDomain.WH_PER_KM_UP_INDEX
import quebec.virtualite.unirider.bluetooth.WheelInfo
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.services.CalculatorService.Companion.CHARGER_OFFSET
import quebec.virtualite.unirider.views.BaseFragment.Companion.PARAMETER_RATES
import quebec.virtualite.unirider.views.BaseFragment.Companion.PARAMETER_SELECTED_RATE
import quebec.virtualite.unirider.views.BaseFragment.Companion.PARAMETER_VOLTAGE
import quebec.virtualite.unirider.views.BaseFragment.Companion.PARAMETER_WHEEL_ID

@RunWith(MockitoJUnitRunner::class)
class WheelChargeFragmentTest : BaseFragmentTest(WheelChargeFragment::class.java) {

    @InjectMocks
    @Spy
    lateinit var fragment: WheelChargeFragment

    @Mock
    lateinit var mockedButtonConnect: Button

    @Mock
    lateinit var mockedCheckFullCharge: CheckBox

    @Mock
    lateinit var mockedCalculatorService: CalculatorService

    @Mock
    lateinit var mockedEditKm: EditText

    @Mock
    lateinit var mockedEditVoltageActual: EditText

    @Mock
    lateinit var mockedSpinnerRates: Spinner

    @Mock
    lateinit var mockedTextName: TextView

    @Mock
    lateinit var mockedTextRemainingTime: TextView

    @Mock
    lateinit var mockedTextVoltageRequired: TextView

    @Before
    fun before() {
        fragment.parmWheelId = ID
        fragment.parmVoltageDisconnectedFromCharger = VOLTAGE
        setList(fragment.parmRates, WHS_PER_KM)
        fragment.parmSelectedRate = WH_PER_KM_INDEX
        fragment.wheel = SHERMAN_MAX_3

        mockExternal()
        mockFields()
        mockFragments()
    }

    @Test
    fun blankEstimates() {
        // Given
        injectMocks()

        // When
        fragment.blankEstimates()

        // Then
        verify(mockedTextRemainingTime).text = ""
        verify(mockedTextVoltageRequired).text = ""
    }

    @Test
    fun onCreateView() {
        // Given
        mockArgument(fragment, PARAMETER_WHEEL_ID, ID)
        mockArgument(fragment, PARAMETER_RATES, WHS_PER_KM)
        mockArgument(fragment, PARAMETER_SELECTED_RATE, WH_PER_KM_INDEX)
        mockArgument(fragment, PARAMETER_VOLTAGE, VOLTAGE)

        // When
        fragment.onCreateView(mockedInflater, mockedContainer, SAVED_INSTANCE_STATE)

        // Then
        verifyInflate(R.layout.wheel_charge_fragment)

        assertThat(fragment.parmWheelId, equalTo(ID))
        assertThat(fragment.parmSelectedRate, equalTo(WH_PER_KM_INDEX))
        assertThat(fragment.parmVoltageDisconnectedFromCharger, equalTo(VOLTAGE))
    }

    @Test
    fun onViewCreated() {
        // Given
        fragment.wheel = null
        given(mockedDb.getWheel(anyLong()))
            .willReturn(S18_1)

        setList(fragment.parmRates, WHS_PER_KM)
        fragment.parmSelectedRate = WH_PER_KM_INDEX

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedDb).getWheel(ID)

        assertThat(fragment.wheel, equalTo(S18_1))

        verifyFieldAssignment(R.id.button_connect_charge, fragment.buttonConnect, mockedButtonConnect)
        verifyFieldAssignment(R.id.check_full_charge, fragment.checkFullCharge, mockedCheckFullCharge)
        verifyFieldAssignment(R.id.edit_km, fragment.editKm, mockedEditKm)
        verifyFieldAssignment(R.id.edit_voltage_actual, fragment.editVoltageActual, mockedEditVoltageActual)
        verifyFieldAssignment(R.id.spinner_wh_per_km, fragment.spinnerRates, mockedSpinnerRates)
        verifyFieldAssignment(R.id.view_name, fragment.textName, mockedTextName)
        verifyFieldAssignment(R.id.view_remaining_time, fragment.textRemainingTime, mockedTextRemainingTime)
        verifyFieldAssignment(R.id.view_voltage_required, fragment.textVoltageRequired, mockedTextVoltageRequired)

        assertThat(fragment.spinnerRates, equalTo(mockedSpinnerRates))

        verifyOnClick(mockedButtonConnect, "onConnect")
        verifyOnToggleCheckbox(mockedCheckFullCharge, "onToggleFullCharge")
        verifyOnUpdateText(mockedEditKm, "onUpdateKm")
        verifyOnUpdateText(mockedEditVoltageActual, "onUpdateVoltageActual")
        verifyOnItemSelected(mockedSpinnerRates, "onChangeRate")
        verifyStringListAdapter(mockedSpinnerRates, WHS_PER_KM)

        verify(mockedCheckFullCharge).isChecked = true
        verify(mockedEditVoltageActual).setText("${VOLTAGE + CHARGER_OFFSET}")
        verify(mockedWidgets).setSelection(mockedSpinnerRates, WH_PER_KM_INDEX)
        verify(mockedTextName).text = NAME
    }

    @Test
    fun onViewCreated_whenWheelHasNeverBeenConnected_disableConnectButton() {
        // Given
        fragment.wheel = null
        given(mockedDb.getWheel(anyLong()))
            .willReturn(S18_1.copy(btName = null, btAddr = null))

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedWidgets).disable(mockedButtonConnect)
    }

    @Test
    fun onChangeRate() {
        // Given
        injectMocks()
        mockUpdateEstimates()

        fragment.parmSelectedRate = WH_PER_KM_INDEX

        // When
        fragment.onChangeRate().invoke(mockedView, WH_PER_KM_UP_INDEX, WH_PER_KM_UP.toString())

        // Then
        assertThat(fragment.parmSelectedRate, equalTo(WH_PER_KM_UP_INDEX))

        verify(fragment).updateEstimates()
    }

    @Test
    fun onConnect() {
        // Given
        injectMocks()
        mockUpdateEstimates()

        val connectionPayload = WheelInfo(KM_NEW_RAW, MILEAGE_NEW_RAW, TEMPERATURE_NEW_RAW, VOLTAGE_NEW_RAW)

        // When
        fragment.onConnect().invoke(mockedView)

        // Then
        verifyRunWithWaitDialog()
        verifyConnectorGetDeviceInfo(DEVICE_ADDR3, connectionPayload)
        verifyDoneWaiting(connectionPayload)

        assertThat(fragment.parmVoltageDisconnectedFromCharger, equalTo(VOLTAGE_NEW - CHARGER_OFFSET))

        verify(mockedEditVoltageActual).setText("$VOLTAGE_NEW")
        verify(fragment).updateEstimates()
    }

    @Test
    fun onToggleFullCharge_whenChecked() {
        // Given
        injectMocks()
        mockUpdateEstimates()

        // When
        fragment.onToggleFullCharge().invoke(false)

        // Then
        verify(mockedWidgets).show(mockedEditKm)
        verify(mockedWidgets).show(mockedSpinnerRates)

        verify(fragment).updateEstimates()
    }

    @Test
    fun onToggleFullCharge_whenUnchecked() {
        // Given
        injectMocks()
        mockUpdateEstimates()

        // When
        fragment.onToggleFullCharge().invoke(true)

        // Then
        verify(mockedWidgets).hide(mockedEditKm)
        verify(mockedWidgets).hide(mockedSpinnerRates)

        verify(fragment).updateEstimates()
    }

    @Test
    fun onUpdateKm() {
        // Given
        injectMocks()
        mockUpdateEstimates()

        // When
        fragment.onUpdateKm().invoke("$KM ")

        // Then
        verify(fragment).updateEstimates()
    }

    @Test
    fun onUpdateKm_whenInvalid_noDisplay() {
        onUpdateKm_whenInvalid_noDisplay(" ")
        onUpdateKm_whenInvalid_noDisplay("-$KM ")
        onUpdateKm_whenInvalid_noDisplay("a ")
    }

    private fun onUpdateKm_whenInvalid_noDisplay(km: String) {
        // Given
        reset(fragment)
        injectMocks()

        // When
        fragment.onUpdateKm().invoke(km)

        // Then
        verify(fragment).blankEstimates()
    }

    @Test
    fun onUpdateVoltageActual() {
        // Given
        injectMocks()
        mockUpdateEstimates()

        // When
        fragment.onUpdateVoltageActual().invoke("$VOLTAGE_NEW ")

        // Then
        assertThat(
            "Voltage actual not reset",
            fragment.parmVoltageDisconnectedFromCharger, equalTo(VOLTAGE_NEW - CHARGER_OFFSET)
        )

        verify(fragment).updateEstimates()
    }

    @Test
    fun onUpdateVoltageActual_whenInvalid_noDisplay() {
        onUpdateVoltageActual_whenInvalid_noDisplay(" ")
        onUpdateVoltageActual_whenInvalid_noDisplay("-$VOLTAGE_NEW ")
        onUpdateVoltageActual_whenInvalid_noDisplay("${VOLTAGE_MIN3 - 0.1f} ")
        onUpdateVoltageActual_whenInvalid_noDisplay("a ")
    }

    private fun onUpdateVoltageActual_whenInvalid_noDisplay(voltage: String) {
        // Given
        reset(fragment)
        injectMocks()

        // When
        fragment.onUpdateVoltageActual().invoke(voltage)

        // Then
        verify(fragment).blankEstimates()
    }

    @Test
    fun timeDisplay() {
        timeDisplay(2.75f, "2h45")
        timeDisplay(0.1625f, "10m")
        timeDisplay(9f / 8f, "1h08")
        timeDisplay(0.08f, "5m")
    }

    private fun timeDisplay(rawHours: Float, expectedDisplay: String) {
        // When
        val display = fragment.timeDisplay(rawHours)

        // Then
        assertThat(display, equalTo(expectedDisplay))
    }

    @Test
    fun updateEstimates_whenFullCharge_displayValue() {
        // Given
        injectMocks()
        mockCheckFullCharge(true)

        given(mockedCalculatorService.requiredFullVoltage(any())).willReturn(VOLTAGE_FULL3)

        // When
        fragment.updateEstimates()

        // Then
        verify(mockedCalculatorService).requiredFullVoltage(SHERMAN_MAX_3)
        verifyVoltageRequired(SHERMAN_MAX_3.voltageFull)
    }

    @Test
    fun updateEstimates_whenFullChargeIsNeeded_displayValue() {
        // Given
        injectMocks()
        mockCheckFullCharge(false)
        mockEditKmWith(KM)

        given(mockedCalculatorService.requiredVoltage(any(), anyFloat(), anyFloat()))
            .willReturn(SHERMAN_MAX_3.voltageFull)

        // When
        fragment.updateEstimates()

        // Then
        val whPerKm = floatOf(WHS_PER_KM[WH_PER_KM_INDEX])

        verify(mockedCalculatorService).requiredVoltage(SHERMAN_MAX_3, whPerKm, KM)
        verifyVoltageRequired(SHERMAN_MAX_3.voltageFull)
    }

    @Test
    fun updateEstimates_whenVoltageIsEnough_displayValue() {
        // Given
        injectMocks()
        mockCheckFullCharge(false)
        mockEditKmWith(KM)

        given(mockedCalculatorService.requiredVoltage(any(), anyFloat(), anyFloat()))
            .willReturn(VOLTAGE + CHARGER_OFFSET - 0.1f)

        // When
        fragment.updateEstimates()

        // Then
        val whPerKm = floatOf(WHS_PER_KM[WH_PER_KM_INDEX])

        verify(mockedCalculatorService).requiredVoltage(SHERMAN_MAX_3, whPerKm, KM)
        verifyDisplayGo()
    }

    @Test
    fun updateEstimates_whenVoltageIsLimit_displayValue() {
        // Given
        injectMocks()
        mockCheckFullCharge(false)
        mockEditKmWith(KM)

        given(mockedCalculatorService.requiredVoltage(any(), anyFloat(), anyFloat()))
            .willReturn(VOLTAGE + CHARGER_OFFSET)

        // When
        fragment.updateEstimates()

        // Then
        val whPerKm = floatOf(WHS_PER_KM[WH_PER_KM_INDEX])

        verify(mockedCalculatorService).requiredVoltage(SHERMAN_MAX_3, whPerKm, KM)
        verifyDisplayGo()
    }

    @Test
    fun updateEstimates_whenVoltageIsNotEnough_displayValue() {
        // Given
        injectMocks()
        mockCheckFullCharge(false)
        mockEditKmWith(KM)

        val voltageRequired = VOLTAGE + CHARGER_OFFSET + 0.1f
        given(mockedCalculatorService.requiredVoltage(any(), anyFloat(), anyFloat()))
            .willReturn(voltageRequired)

        // When
        fragment.updateEstimates()

        // Then
        val whPerKm = floatOf(WHS_PER_KM[WH_PER_KM_INDEX])

        verify(mockedCalculatorService).requiredVoltage(SHERMAN_MAX_3, whPerKm, KM)
        verifyVoltageRequired(voltageRequired)
    }

    @Test
    fun updateEstimates_withZeroKm_noDisplay() {
        // Given
        injectMocks()
        mockCheckFullCharge(false)
        mockEditKmWith(0f)

        // When
        fragment.updateEstimates()

        // Then
        verify(fragment).blankEstimates()
    }

    private fun injectMocks() {
        fragment.editKm = mockedEditKm
        fragment.editVoltageActual = mockedEditVoltageActual
        fragment.spinnerRates = mockedSpinnerRates
        fragment.textRemainingTime = mockedTextRemainingTime
        fragment.textVoltageRequired = mockedTextVoltageRequired
    }

    private fun mockFields() {
        mockField(R.id.button_connect_charge, mockedButtonConnect)
        mockField(R.id.check_full_charge, mockedCheckFullCharge)
        mockField(R.id.edit_km, mockedEditKm)
        mockField(R.id.edit_voltage_actual, mockedEditVoltageActual)
        mockField(R.id.spinner_wh_per_km, mockedSpinnerRates)
        mockField(R.id.view_name, mockedTextName)
        mockField(R.id.view_remaining_time, mockedTextRemainingTime)
        mockField(R.id.view_voltage_required, mockedTextVoltageRequired)
    }

    private fun mockCheckFullCharge(value: Boolean) {
        given(mockedCheckFullCharge.isChecked).willReturn(value)
    }

    private fun mockEditKmWith(km: Float) {
        given(mockedWidgets.getText(mockedEditKm)).willReturn(km.toString())
    }

    private fun mockUpdateEstimates() {
        doNothing().`when`(fragment).updateEstimates()
    }

    private fun verifyDisplayGo() {
        verify(mockedTextVoltageRequired).text = "Go!"
        verify(mockedTextRemainingTime).text = ""
    }

    private fun verifyVoltageRequired(voltageRequired: Float) {
        val diff = round(voltageRequired - (VOLTAGE + CHARGER_OFFSET), 1)
        verify(mockedTextVoltageRequired).text = "${voltageRequired}V (+$diff)"

        val rawHours = diff / fragment.wheel!!.chargeRate
        verify(mockedTextRemainingTime).text = fragment.timeDisplay(rawHours)
    }
}
