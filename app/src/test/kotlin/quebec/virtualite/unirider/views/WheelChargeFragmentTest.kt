package quebec.virtualite.unirider.views

import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.google.android.material.switchmaterial.SwitchMaterial
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.BDDMockito.doNothing
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.commons.android.utils.CollectionUtils.setList
import quebec.virtualite.commons.android.utils.DateUtils
import quebec.virtualite.commons.android.utils.NumberUtils.floatOf
import quebec.virtualite.commons.android.utils.NumberUtils.round
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.TestDomain.KM
import quebec.virtualite.unirider.TestDomain.KM_NEW_RAW
import quebec.virtualite.unirider.TestDomain.MILEAGE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.SHERMAN_MAX_3
import quebec.virtualite.unirider.TestDomain.TEMPERATURE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.VOLTAGE
import quebec.virtualite.unirider.TestDomain.VOLTAGE_NEW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.WHS_PER_KM
import quebec.virtualite.unirider.TestDomain.WH_PER_KM_INDEX
import quebec.virtualite.unirider.TestDomain.WH_PER_KM_UP
import quebec.virtualite.unirider.TestDomain.WH_PER_KM_UP_INDEX
import quebec.virtualite.unirider.bluetooth.WheelInfo
import quebec.virtualite.unirider.services.CalculatorService

@RunWith(MockitoJUnitRunner::class)
class WheelChargeFragmentTest : FragmentTestBase(WheelChargeFragment::class.java) {

    @InjectMocks
    @Spy
    lateinit var fragment: WheelChargeFragment

    @Mock
    lateinit var mockedButtonConnect: Button

    @Mock
    lateinit var mockedSwitchFullCharge: SwitchMaterial

    @Mock
    lateinit var mockedCalculatorService: CalculatorService

    @Mock
    lateinit var mockedEditKm: EditText

    @Mock
    lateinit var mockedEditVoltageActual: EditText

    @Mock
    lateinit var mockedSpinnerRate: Spinner

    @Mock
    lateinit var mockedTextName: TextView

    @Mock
    lateinit var mockedTextRemainingTime: TextView

    @Mock
    lateinit var mockedTextVoltageRequired: TextView

    private val WHEEL = SHERMAN_MAX_3

    @Before
    fun before() {
        BaseFragment.wheel = WHEEL
        fragment.parmVoltageDisconnectedFromCharger = VOLTAGE
        setList(fragment.parmRates, WHS_PER_KM)
        fragment.parmSelectedRate = WH_PER_KM_INDEX

        DateUtils.simulateNow("11:45")
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
    fun displayVoltageActual() {
        // Given
        injectMocks()

        fragment.parmVoltageDisconnectedFromCharger = VOLTAGE_NEW_RAW

        // When
        fragment.displayVoltageActual()

        // Then
        verify(mockedEditVoltageActual).setText("${VOLTAGE_NEW + WHEEL.chargerOffset}")
    }

    @Test
    fun onCreateView() {
        // Given
        setList(BaseFragment.chargeContext.rates, WHS_PER_KM)
        BaseFragment.chargeContext.selectedRate = WH_PER_KM_INDEX
        BaseFragment.chargeContext.voltage = VOLTAGE

        // When
        fragment.onCreateView(mockedInflater, mockedContainer, SAVED_INSTANCE_STATE)

        // Then
        verifyInflate(R.layout.wheel_charge_fragment)

        assertThat(fragment.parmRates, equalTo(WHS_PER_KM))
        assertThat(fragment.parmSelectedRate, equalTo(WH_PER_KM_INDEX))
        assertThat(fragment.parmVoltageDisconnectedFromCharger, equalTo(VOLTAGE))
    }

    @Test
    fun onViewCreated() {
        // Given
        setList(fragment.parmRates, WHS_PER_KM)
        fragment.parmSelectedRate = WH_PER_KM_INDEX

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verifyFieldAssignment(R.id.button_connect_charge, fragment.buttonConnect, mockedButtonConnect)
        verifyFieldAssignment(R.id.check_full_charge, fragment.switchFullCharge, mockedSwitchFullCharge)
        verifyFieldAssignment(R.id.edit_km, fragment.editKm, mockedEditKm)
        verifyFieldAssignment(R.id.edit_voltage_actual, fragment.editVoltageActual, mockedEditVoltageActual)
        verifyFieldAssignment(R.id.spinner_rate, fragment.spinnerRate, mockedSpinnerRate)
        verifyFieldAssignment(R.id.view_name, fragment.textName, mockedTextName)
        verifyFieldAssignment(R.id.view_remaining_time, fragment.textRemainingTime, mockedTextRemainingTime)
        verifyFieldAssignment(R.id.view_voltage_required, fragment.textVoltageRequired, mockedTextVoltageRequired)

        assertThat(fragment.spinnerRate, equalTo(mockedSpinnerRate))

        verifyOnClick(mockedButtonConnect, "onConnect")
        verifyOnUpdateText(mockedEditKm, "onUpdateKm")
        verifyOnUpdateText(mockedEditVoltageActual, "onUpdateVoltageActual")
        verifyOnItemSelected(mockedSpinnerRate, "onChangeRate")
        verifyStringListAdapter(mockedSpinnerRate, WHS_PER_KM)
        verifyOnToggleSwitch(mockedSwitchFullCharge, "onToggleFullCharge")

        verify(mockedEditVoltageActual).setText("${VOLTAGE + WHEEL.chargerOffset}")
        verify(mockedSwitchFullCharge).isChecked = true
        verify(mockedWidgets).setSelection(mockedSpinnerRate, WH_PER_KM_INDEX)
        verify(mockedTextName).text = WHEEL.name
    }

    @Test
    fun onViewCreated_whenWheelHasNeverBeenConnected_disableConnectButton() {
        // Given
        BaseFragment.wheel = WHEEL.copy(btName = null, btAddr = null)

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
        verifyConnectorGetDeviceInfo(WHEEL.btAddr!!, connectionPayload)
        verifyDoneWaiting(connectionPayload)

        assertThat(fragment.parmVoltageDisconnectedFromCharger, equalTo(VOLTAGE_NEW - WHEEL.chargerOffset))

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
        verify(mockedWidgets).enable(mockedEditKm, mockedSpinnerRate)

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
        verify(mockedWidgets).disable(mockedEditKm, mockedSpinnerRate)

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
        fragment.onUpdateVoltageActual().invoke("$VOLTAGE_NEW_RAW ")

        // Then
        assertThat(
            "Voltage actual not reset",
            fragment.parmVoltageDisconnectedFromCharger, equalTo(VOLTAGE_NEW - WHEEL.chargerOffset)
        )

        verify(fragment).updateEstimates()
    }

    @Test
    fun onUpdateVoltageActual_whenInvalid_noDisplay() {
        onUpdateVoltageActual_whenInvalid_noDisplay(" ")
        onUpdateVoltageActual_whenInvalid_noDisplay("-$VOLTAGE_NEW ")
        onUpdateVoltageActual_whenInvalid_noDisplay("${WHEEL.voltageMin - 0.1f} ")
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
        timeDisplay(0f, "11:45")
        timeDisplay(1f, "12:45")
        timeDisplay(2.75f, "14:30")
        timeDisplay(0.1625f, "11:55")
        timeDisplay(9f / 8f, "12:53")
        timeDisplay(0.08f, "11:50")
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

        given(mockedCalculatorService.requiredFullVoltage(any())).willReturn(WHEEL.voltageFull)

        // When
        fragment.updateEstimates()

        // Then
        verify(mockedCalculatorService).requiredFullVoltage(WHEEL)
        verifyVoltageRequired(WHEEL.voltageFull)
    }

    @Test
    fun updateEstimates_whenFullChargeIsNeeded_displayValue() {
        // Given
        injectMocks()
        mockCheckFullCharge(false)
        mockEditKmWith(KM)

        given(mockedCalculatorService.requiredVoltage(any(), anyFloat(), anyFloat()))
            .willReturn(WHEEL.voltageFull)

        // When
        fragment.updateEstimates()

        // Then
        val whPerKm = floatOf(WHS_PER_KM[WH_PER_KM_INDEX])

        verify(mockedCalculatorService).requiredVoltage(WHEEL, whPerKm, KM)
        verifyVoltageRequired(WHEEL.voltageFull)
    }

    @Test
    fun updateEstimates_whenVoltageIsEnough_displayValue() {
        // Given
        injectMocks()
        mockCheckFullCharge(false)
        mockEditKmWith(KM)

        given(mockedCalculatorService.requiredVoltage(any(), anyFloat(), anyFloat()))
            .willReturn(VOLTAGE + WHEEL.chargerOffset - 0.1f)

        // When
        fragment.updateEstimates()

        // Then
        val whPerKm = floatOf(WHS_PER_KM[WH_PER_KM_INDEX])

        verify(mockedCalculatorService).requiredVoltage(WHEEL, whPerKm, KM)
        verifyDisplayGo()
    }

    @Test
    fun updateEstimates_whenVoltageIsLimit_displayValue() {
        // Given
        injectMocks()
        mockCheckFullCharge(false)
        mockEditKmWith(KM)

        given(mockedCalculatorService.requiredVoltage(any(), anyFloat(), anyFloat()))
            .willReturn(VOLTAGE + WHEEL.chargerOffset)

        // When
        fragment.updateEstimates()

        // Then
        val whPerKm = floatOf(WHS_PER_KM[WH_PER_KM_INDEX])

        verify(mockedCalculatorService).requiredVoltage(WHEEL, whPerKm, KM)
        verifyDisplayGo()
    }

    @Test
    fun updateEstimates_whenVoltageIsNotEnough_displayValue() {
        // Given
        injectMocks()
        mockCheckFullCharge(false)
        mockEditKmWith(KM)

        val voltageRequired = VOLTAGE + WHEEL.chargerOffset + 0.1f
        given(mockedCalculatorService.requiredVoltage(any(), anyFloat(), anyFloat()))
            .willReturn(voltageRequired)

        // When
        fragment.updateEstimates()

        // Then
        val whPerKm = floatOf(WHS_PER_KM[WH_PER_KM_INDEX])

        verify(mockedCalculatorService).requiredVoltage(WHEEL, whPerKm, KM)
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
        fragment.spinnerRate = mockedSpinnerRate
        fragment.textRemainingTime = mockedTextRemainingTime
        fragment.textVoltageRequired = mockedTextVoltageRequired
    }

    private fun mockFields() {
        mockField(R.id.button_connect_charge, mockedButtonConnect)
        mockField(R.id.check_full_charge, mockedSwitchFullCharge)
        mockField(R.id.edit_km, mockedEditKm)
        mockField(R.id.edit_voltage_actual, mockedEditVoltageActual)
        mockField(R.id.spinner_rate, mockedSpinnerRate)
        mockField(R.id.view_name, mockedTextName)
        mockField(R.id.view_remaining_time, mockedTextRemainingTime)
        mockField(R.id.view_voltage_required, mockedTextVoltageRequired)
    }

    private fun mockCheckFullCharge(value: Boolean) {
        given(mockedSwitchFullCharge.isChecked).willReturn(value)
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
        val diff = round(voltageRequired - (VOLTAGE + WHEEL.chargerOffset), 1)
        verify(mockedTextVoltageRequired).text = "${voltageRequired}V (+$diff)"

        val rawHours = diff / BaseFragment.wheel!!.chargeRate
        verify(mockedTextRemainingTime).text = fragment.timeDisplay(rawHours)
    }
}
