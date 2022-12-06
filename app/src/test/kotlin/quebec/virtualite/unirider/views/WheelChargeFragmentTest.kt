package quebec.virtualite.unirider.views

import android.widget.Button
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
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verifyNoInteractions
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.commons.android.utils.ArrayListUtils.setList
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
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MAX3
import quebec.virtualite.unirider.TestDomain.VOLTAGE_NEW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_REQUIRED
import quebec.virtualite.unirider.TestDomain.WHS_PER_KM
import quebec.virtualite.unirider.TestDomain.WH_PER_KM
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
    lateinit var fragment: WheelChargeFragment

    @Mock
    lateinit var mockedButtonConnect: Button

    @Mock
    lateinit var mockedCalculatorService: CalculatorService

    @Mock
    lateinit var mockedEditKm: EditText

    @Mock
    lateinit var mockedListRates: Spinner

    @Mock
    lateinit var mockedTextName: TextView

    @Mock
    lateinit var mockedTextRemainingTime: TextView

    @Mock
    lateinit var mockedTextVoltageActual: TextView

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
        verifyFieldAssignment(R.id.edit_km, fragment.editKm, mockedEditKm)
        verifyFieldAssignment(R.id.view_wh_per_km, fragment.listRates, mockedListRates)
        verifyFieldAssignment(R.id.view_name, fragment.textName, mockedTextName)
        verifyFieldAssignment(R.id.view_remaining_time, fragment.textRemainingTime, mockedTextRemainingTime)
        verifyFieldAssignment(R.id.view_voltage_actual, fragment.textVoltageActual, mockedTextVoltageActual)
        verifyFieldAssignment(R.id.view_voltage_required, fragment.textVoltageRequired, mockedTextVoltageRequired)

        assertThat(fragment.listRates, equalTo(mockedListRates))

        verifyOnClick(mockedButtonConnect, "onConnect")
        verifyOnUpdateText(mockedEditKm, "onUpdateKm")
        verifyOnItemSelected(mockedListRates, "onChangeRate")
        verifyStringListAdapter(mockedListRates, WHS_PER_KM)

        verify(mockedTextName).text = NAME
        verify(mockedTextVoltageActual).text = "${VOLTAGE + CHARGER_OFFSET}"
        verify(mockedWidgets).setSelection(mockedListRates, WH_PER_KM_INDEX)
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
        mockKm("$KM ")

        given(mockedCalculatorService.requiredVoltage(any(), anyFloat(), anyFloat()))
            .willReturn(VOLTAGE_REQUIRED)

        fragment.parmSelectedRate = WH_PER_KM_INDEX

        // When
        fragment.onChangeRate().invoke(mockedView, WH_PER_KM_UP_INDEX, WH_PER_KM_UP.toString())

        // Then
        assertThat(fragment.parmSelectedRate, equalTo(WH_PER_KM_UP_INDEX))
        verify(mockedCalculatorService).requiredVoltage(fragment.wheel, WH_PER_KM_UP, KM)
        verifyVoltageRequired(VOLTAGE_REQUIRED)
    }

    @Test
    fun onConnect() {
        // Given
        injectMocks()

        given(mockedCalculatorService.requiredVoltage(any(), anyFloat(), anyFloat()))
            .willReturn(VOLTAGE_REQUIRED)

        given(mockedWidgets.text(mockedEditKm))
            .willReturn("$KM")

        val connectionPayload = WheelInfo(KM_NEW_RAW, MILEAGE_NEW_RAW, TEMPERATURE_NEW_RAW, VOLTAGE_NEW_RAW)

        // When
        fragment.onConnect().invoke(mockedView)

        // Then
        verifyRunWithWaitDialog()
        verifyConnectorGetDeviceInfo(DEVICE_ADDR3, connectionPayload)
        verifyDoneWaiting(connectionPayload)

        assertThat(fragment.parmVoltageDisconnectedFromCharger, equalTo(VOLTAGE_NEW - CHARGER_OFFSET))

        val diff = round(VOLTAGE_REQUIRED - VOLTAGE_NEW, 1)
        verify(mockedTextVoltageActual).text = "$VOLTAGE_NEW"
        verify(mockedTextVoltageRequired).text = "${VOLTAGE_REQUIRED}V (+$diff)"
        verifyDisplayTime(diff)
    }

    @Test
    fun onUpdateKm() {
        // Given
        injectMocks()

        given(mockedCalculatorService.requiredVoltage(any(), anyFloat(), anyFloat()))
            .willReturn(VOLTAGE_REQUIRED)

        // When
        fragment.onUpdateKm().invoke("$KM ")

        // Then
        verify(mockedCalculatorService).requiredVoltage(fragment.wheel, WH_PER_KM, KM)
        verifyVoltageRequired(VOLTAGE_REQUIRED)
    }

    @Test
    fun onUpdateKm_whenEmpty_noDisplay() {
        // Given
        injectMocks()

        // When
        fragment.onUpdateKm().invoke(" ")

        // Then
        verifyNoInteractions(mockedCalculatorService)
        verifyNoDisplay()
    }

    @Test
    fun onUpdateKm_whenFullChargeIsNeeded_displayValue() {
        // Given
        injectMocks()

        val maxVoltageBeforeBalancing = VOLTAGE_MAX3 - CHARGER_OFFSET
        given(mockedCalculatorService.requiredVoltage(any(), anyFloat(), anyFloat()))
            .willReturn(maxVoltageBeforeBalancing)

        // When
        fragment.onUpdateKm().invoke("$KM ")

        // Then
        verifyVoltageRequired(maxVoltageBeforeBalancing)
    }

    @Test
    fun onUpdateKm_whenVoltageIsEnough_displayGo() {
        // Given
        injectMocks()

        given(mockedCalculatorService.requiredVoltage(any(), anyFloat(), anyFloat()))
            .willReturn(VOLTAGE + CHARGER_OFFSET - 0.1f)

        // When
        fragment.onUpdateKm().invoke("$KM ")

        // Then
        verifyDisplayGo()
    }

    @Test
    fun onUpdateKm_whenVoltageIsLimit_displayGo() {
        // Given
        injectMocks()

        given(mockedCalculatorService.requiredVoltage(any(), anyFloat(), anyFloat()))
            .willReturn(VOLTAGE + CHARGER_OFFSET)

        // When
        fragment.onUpdateKm().invoke("$KM ")

        // Then
        verifyDisplayGo()
    }

    @Test
    fun onUpdateKm_whenVoltageIsNotEnough_displayValue() {
        // Given
        injectMocks()

        given(mockedCalculatorService.requiredVoltage(any(), anyFloat(), anyFloat()))
            .willReturn(VOLTAGE + CHARGER_OFFSET + 0.1f)

        // When
        fragment.onUpdateKm().invoke("$KM ")

        // Then
        verify(mockedTextVoltageRequired).text = "${VOLTAGE + CHARGER_OFFSET + 0.1f}V (+0.1)"
        verify(mockedTextRemainingTime).text = "1m"
    }

    @Test
    fun onUpdateKm_withNegativeKm_noDisplay() {
        // Given
        injectMocks()

        // When
        fragment.onUpdateKm().invoke("-$KM ")

        // Then
        verifyNoInteractions(mockedCalculatorService)
        verifyNoDisplay()
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

    private fun injectMocks() {
        fragment.textRemainingTime = mockedTextRemainingTime
        fragment.textVoltageActual = mockedTextVoltageActual
        fragment.textVoltageRequired = mockedTextVoltageRequired
    }

    private fun mockFields() {
        mockField(R.id.button_connect_charge, mockedButtonConnect)
        mockField(R.id.edit_km, mockedEditKm)
        mockField(R.id.view_wh_per_km, mockedListRates)
        mockField(R.id.view_name, mockedTextName)
        mockField(R.id.view_remaining_time, mockedTextRemainingTime)
        mockField(R.id.view_voltage_actual, mockedTextVoltageActual)
        mockField(R.id.view_voltage_required, mockedTextVoltageRequired)
    }

    private fun mockKm(km: String) {
        fragment.editKm = mockedEditKm

        given(mockedWidgets.text(mockedEditKm))
            .willReturn(km.trim())
    }

    private fun verifyDisplayGo() {
        verify(mockedTextVoltageRequired).text = "Go!"
        verify(mockedTextRemainingTime).text = ""
    }

    private fun verifyDisplayTime(voltageDiff: Float) {
        val rawHours = voltageDiff / fragment.wheel!!.chargeRate
        verify(mockedTextRemainingTime).text = fragment.timeDisplay(rawHours)
    }

    private fun verifyNoDisplay() {
        verify(mockedTextVoltageRequired).text = ""
        verify(mockedTextRemainingTime).text = ""
    }

    private fun verifyVoltageRequired(voltageRequired: Float) {
        val diff = round(voltageRequired - (VOLTAGE + CHARGER_OFFSET), 1)
        verify(mockedTextVoltageRequired).text = "${voltageRequired}V (+$diff)"
        verifyDisplayTime(diff)
    }
}
