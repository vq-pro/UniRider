package quebec.virtualite.unirider.views

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.switchmaterial.SwitchMaterial
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.doNothing
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.never
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.commons.android.utils.DateUtils.Companion.simulateNow
import quebec.virtualite.commons.android.utils.NumberUtils.round
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.bluetooth.WheelInfo
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.test.domain.TestConstants.CHARGER_OFFSET
import quebec.virtualite.unirider.test.domain.TestConstants.KM
import quebec.virtualite.unirider.test.domain.TestConstants.KM_NEW_RAW
import quebec.virtualite.unirider.test.domain.TestConstants.KM_REQUESTED
import quebec.virtualite.unirider.test.domain.TestConstants.MILEAGE_NEW_RAW
import quebec.virtualite.unirider.test.domain.TestConstants.S18_1
import quebec.virtualite.unirider.test.domain.TestConstants.TEMPERATURE_NEW_RAW
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_FULL
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_NEW_RAW
import quebec.virtualite.unirider.views.BaseFragment.Companion.wheel

@RunWith(MockitoJUnitRunner::class)
class WheelChargeFragmentTest : FragmentTestBase(WheelChargeFragment::class.java) {

    @InjectMocks
    @Spy
    private lateinit var fragment: WheelChargeFragment

    @Mock
    private lateinit var mockedButtonConnect: Button

    @Mock
    private lateinit var mockedSwitchFullCharge: SwitchMaterial

    @Mock
    private lateinit var mockedCalculatorService: CalculatorService

    @Mock
    private lateinit var mockedEditKm: EditText

    @Mock
    private lateinit var mockedEditVoltageActual: EditText

    @Mock
    private lateinit var mockedEditVoltageTarget: EditText

    @Mock
    private lateinit var mockedTextEstimatedTime: TextView

    @Mock
    private lateinit var mockedTextChargeWarning: TextView

    @Mock
    private lateinit var mockedTextEstimatedDiff: TextView

    @Mock
    private lateinit var mockedTextName: TextView

    @Mock
    private lateinit var mockedTextVoltageRequired: TextView

    @Mock
    private lateinit var mockedTextVoltageRequiredDiff: TextView

    @Mock
    private lateinit var mockedTextVoltageTarget: TextView

    @Mock
    private lateinit var mockedTextVoltageTargetDiff: TextView

    private val WHEEL = S18_1

    @Before
    fun before() {
        wheel = WHEEL
        fragment.cacheVoltageActual = onCharge(VOLTAGE)

        simulateNow("11:45")
        mockExternal()
        mockFields()
        mockFragments()
    }

    @Test
    fun display_beforeStartingTheProcess() {
        // Given
        fragment.chargerOffset = null

        // When
        fragment.display()

        // Then
        verify(fragment, never()).getVoltageRequired()
    }

    @Test
    fun display_whenVoltageActualIsLowerThanRequired() {
        // Given
        fragment.cacheVoltageActual = 77.5f
        fragment.chargerOffset = CHARGER_OFFSET

        doReturn(80f).`when`(fragment).getVoltageRequired()
        doNothing().`when`(fragment).displayEstimates(2.5f, 0.625f)

        // When
        fragment.display()

        // Then
        verify(fragment).displayEstimates(2.5f, 0.625f)
    }

    @Test
    fun display_whenVoltageActualIsSameAsRequired() {
        // Given
        fragment.cacheVoltageActual = VOLTAGE
        fragment.chargerOffset = CHARGER_OFFSET

        doReturn(VOLTAGE).`when`(fragment).getVoltageRequired()
        doNothing().`when`(fragment).displayGo()

        // When
        fragment.display()

        // Then
        verify(fragment).displayGo()
    }

    @Test
    fun displayBlanks() {
        // Given
        injectMocks()

        // When
        fragment.displayBlanks()

        // Then
        verify(mockedTextVoltageRequiredDiff).text = ""
        verify(mockedTextEstimatedDiff).text = ""
        verify(mockedTextEstimatedTime).text = ""
        verify(mockedTextVoltageRequired).text = ""
        verify(mockedTextVoltageTarget).text = ""
        verify(mockedTextVoltageTargetDiff).text = ""
    }

    @Test
    fun displayEstimates() {
        // Given
        injectMocks()

        val voltageDiff = 2.5f
        val rawHours = 0.2f

        val time = "time"
        val timeDiff = "timeDiff"

        given(fragment.estimatedTime(rawHours)).willReturn(time)
        given(fragment.estimatedDiff(rawHours)).willReturn(timeDiff)

        // When
        fragment.displayEstimates(voltageDiff, rawHours)

        // Then
        verify(mockedTextVoltageRequiredDiff).text = "V (+2.5)"
        verify(mockedTextEstimatedTime).text = time
        verify(mockedTextEstimatedDiff).text = timeDiff
    }

    @Test
    fun displayGo() {
        // Given
        injectMocks()

        // When
        fragment.displayGo()

        // Then
        verify(mockedTextVoltageRequiredDiff).text = "V"
        verify(mockedTextEstimatedTime).text = "Go!"
        verify(mockedTextEstimatedDiff).text = ""
    }

    @Test
    fun estimatedTime() {
        estimatedTime(0f, "11:45", "")
        estimatedTime(1f, "12:45", "(1h)")
        estimatedTime(2.75f, "14:30", "(2h45m)")
        estimatedTime(0.1625f, "11:55", "(10m)")
        estimatedTime(9f / 8f, "12:53", "(1h8m)")
        estimatedTime(0.08f, "11:50", "(5m)")
    }

    private fun estimatedTime(rawHours: Float, expectedEstimatedTime: String, expectedEstimatedTimeDiff: String) {
        // When
        val estimatedTime = fragment.estimatedTime(rawHours)
        val estimatedTimeDiff = fragment.estimatedDiff(rawHours)

        // Then
        assertThat(estimatedTime, equalTo(expectedEstimatedTime))
        assertThat(estimatedTimeDiff, equalTo(expectedEstimatedTimeDiff))
    }

    @Test
    fun getVoltageRequired_whenFullCharge() {
        // Given
        injectMocks()
        mockChargeContext(CHARGER_OFFSET)
        mockCheckFullCharge(true)
        mockEditVoltageTargetWith("")

        given(mockedCalculatorService.requiredVoltageFull(WHEEL))
            .willReturn(VOLTAGE_FULL)

        // When
        val result = fragment.getVoltageRequired()

        // Then
        verifyVoltage(VOLTAGE_FULL)

        assertThat(result, equalTo(VOLTAGE_FULL))
    }

    @Test
    fun getVoltageRequired_whenFullChargeAndVoltageRequired() {
        // Given
        injectMocks()
        mockChargeContext(CHARGER_OFFSET)
        mockCheckFullCharge(true)
        mockEditVoltageTargetWith("$VOLTAGE")

        given(mockedCalculatorService.requiredVoltageFull(WHEEL))
            .willReturn(VOLTAGE_FULL)

        // When
        val result = fragment.getVoltageRequired()

        // Then
        verifyVoltage(VOLTAGE_FULL)

        assertThat(result, equalTo(VOLTAGE_FULL))
    }

    @Test
    fun getVoltageRequired_whenKmIsSet() {
        // Given
        injectMocks()
        mockChargeContext(KM, VOLTAGE, CHARGER_OFFSET)
        mockCheckFullCharge(false)
        mockEditKmWith("$KM_REQUESTED")
        mockEditVoltageTargetWith("")

        val voltageRequired = VOLTAGE + 1
        given(mockedCalculatorService.requiredVoltageOffCharger(WHEEL, VOLTAGE, KM, KM_REQUESTED))
            .willReturn(voltageRequired)

        // When
        val result = fragment.getVoltageRequired()

        // Then
        verifyVoltage(onCharge(voltageRequired))

        assertThat(result, equalTo(onCharge(voltageRequired)))
    }

    @Test
    fun getVoltageRequired_whenKmIsSetToFull() {
        // Given
        injectMocks()
        mockChargeContext(KM, VOLTAGE, CHARGER_OFFSET)
        mockCheckFullCharge(false)
        mockEditKmWith("$KM_REQUESTED")
        mockEditVoltageTargetWith("")

        given(mockedCalculatorService.requiredVoltageOffCharger(WHEEL, VOLTAGE, KM, KM_REQUESTED))
            .willReturn(VOLTAGE_FULL)

        // When
        val result = fragment.getVoltageRequired()

        // Then
        verifyVoltage(VOLTAGE_FULL)

        assertThat(result, equalTo(VOLTAGE_FULL))
    }

    @Test
    fun getVoltageRequired_whenVoltageIsSetRequired() {
        // Given
        injectMocks()
        mockChargeContext(CHARGER_OFFSET)
        mockCheckFullCharge(false)
        mockEditVoltageTargetWith("$VOLTAGE_NEW_RAW")

        val expectedVoltageRequired = onCharge(VOLTAGE_NEW)

        // When
        val result = fragment.getVoltageRequired()

        // Then
        verifyNoInteractions(mockedCalculatorService)
        verifyVoltage(expectedVoltageRequired)

        assertThat(result, equalTo(expectedVoltageRequired))
    }

    @Test
    fun onCreateView() {
        // When
        fragment.onCreateView(mockedInflater, mockedContainer, SAVED_INSTANCE_STATE)

        // Then
        verifyInflate(R.layout.wheel_charge_fragment)
    }

    @Test
    fun onViewCreated() {
        // Given
        mockChargeContext(KM, VOLTAGE)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verifyFieldAssignment(R.id.button_connect_charge, fragment.buttonConnect, mockedButtonConnect)
        verifyFieldAssignment(R.id.check_full_charge, fragment.switchFullCharge, mockedSwitchFullCharge)
        verifyFieldAssignment(R.id.edit_km, fragment.editKm, mockedEditKm)
        verifyFieldAssignment(R.id.edit_voltage_actual, fragment.editVoltageActual, mockedEditVoltageActual)
        verifyFieldAssignment(R.id.edit_voltage_target, fragment.editVoltageTarget, mockedEditVoltageTarget)
        verifyFieldAssignment(R.id.view_charge_warning, fragment.textChargeWarning, mockedTextChargeWarning)
        verifyFieldAssignment(R.id.view_estimated_diff, fragment.textEstimatedDiff, mockedTextEstimatedDiff)
        verifyFieldAssignment(R.id.view_estimated_time, fragment.textEstimatedTime, mockedTextEstimatedTime)
        verifyFieldAssignment(R.id.view_name, fragment.textName, mockedTextName)
        verifyFieldAssignment(R.id.view_voltage_required, fragment.textVoltageRequired, mockedTextVoltageRequired)
        verifyFieldAssignment(R.id.view_voltage_required_diff, fragment.textVoltageRequiredDiff, mockedTextVoltageRequiredDiff)
        verifyFieldAssignment(R.id.view_voltage_target, fragment.textVoltageTarget, mockedTextVoltageTarget)
        verifyFieldAssignment(R.id.view_voltage_target_diff, fragment.textVoltageTargetDiff, mockedTextVoltageTargetDiff)

        verifyOnClick(mockedButtonConnect, "onConnect")
        verifyOnUpdateText(mockedEditKm, "onUpdateKm")
        verifyOnUpdateText(mockedEditVoltageActual, "onUpdateVoltageActual")
        verifyOnUpdateText(mockedEditVoltageTarget, "onUpdateVoltageTarget")
        verifyOnToggleSwitch(mockedSwitchFullCharge, "onToggleFullCharge")

        verify(mockedTextName).text = WHEEL.name
        verify(mockedSwitchFullCharge).isChecked = true
        verify(mockedEditVoltageActual, never()).setText(anyString())
        verify(mockedEditVoltageTarget, never()).setText(anyString())

        assertThat(fragment.cacheVoltageActual, equalTo(VOLTAGE))
        assertThat(fragment.chargerOffset, nullValue())
    }

    @Test
    fun onViewCreated_whenWheelHasNeverBeenConnected_disableConnectButton() {
        // Given
        wheel = WHEEL.copy(btName = null, btAddr = null)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedWidgets).disable(mockedButtonConnect)
    }

    @Test
    fun onConnect() {
        // Given
        injectMocks()

        doNothing().`when`(fragment).display()
        val connectionPayload = WheelInfo(KM_NEW_RAW, MILEAGE_NEW_RAW, TEMPERATURE_NEW_RAW, VOLTAGE_NEW_RAW)

        // When
        fragment.onConnect().invoke(mockedView)

        // Then
        verifyRunWithWaitDialog()
        verifyConnectorGetDeviceInfo(WHEEL.btAddr!!, connectionPayload)
        verifyDoneWaiting(connectionPayload)

        verify(mockedEditVoltageActual).setText("$VOLTAGE_NEW")
        verify(fragment).display()
    }

    @Test
    fun onToggleFullCharge_whenChecked() {
        // Given
        doNothing().`when`(fragment).display()

        // When
        fragment.onToggleFullCharge().invoke(false)

        // Then
        verify(fragment).display()
    }

    @Test
    fun onToggleFullCharge_whenUnchecked() {
        // Given
        doNothing().`when`(fragment).display()

        // When
        fragment.onToggleFullCharge().invoke(true)

        // Then
        verify(fragment).display()
    }

    @Test
    fun onUpdateKm() {
        // Given
        injectMocks()
        doNothing().`when`(fragment).display()

        // When
        fragment.onUpdateKm().invoke("$KM ")

        // Then
        verify(mockedSwitchFullCharge).isChecked = false
        verify(fragment).display()
    }

    @Test
    fun onUpdateKm_whenInvalid_displayBlanks() {
        onUpdateKm_whenInvalid_displayBlanks(" ")
        onUpdateKm_whenInvalid_displayBlanks("-$KM ")
        onUpdateKm_whenInvalid_displayBlanks("a ")
    }

    private fun onUpdateKm_whenInvalid_displayBlanks(km: String) {
        // Given
        reset(fragment)
        injectMocks()

        // When
        fragment.onUpdateKm().invoke(km)

        // Then
        verify(fragment).displayBlanks()
    }

    @Test
    fun onUpdateVoltageActual_whenFullChargeIsEnabled_update() {
        // Given
        injectMocks()
        mockCheckFullCharge(true)

        doNothing().`when`(fragment).display()

        // When
        fragment.onUpdateVoltageActual().invoke("$VOLTAGE_NEW ")

        // Then
        verify(fragment).display()
        verify(fragment).updateVoltageActual(VOLTAGE_NEW)
    }

    @Test
    fun onUpdateVoltageActual_withNoKmAndNoVoltageRequired_displayBlanks() {
        // Given
        injectMocks()
        mockCheckFullCharge(false)
        mockEditKmWith("")
        mockEditVoltageTargetWith("")

        doNothing().`when`(fragment).displayBlanks()

        // When
        fragment.onUpdateVoltageActual().invoke("$VOLTAGE_NEW ")

        // Then
        verify(fragment).displayBlanks()
        verify(fragment).updateVoltageActual(VOLTAGE_NEW)
    }

    @Test
    fun onUpdateVoltageActual_withKmAndNoVoltageRequired_update() {
        // Given
        injectMocks()
        mockCheckFullCharge(false)
        mockEditKmWith("$KM")
        mockEditVoltageTargetWith("")

        doNothing().`when`(fragment).display()

        // When
        fragment.onUpdateVoltageActual().invoke("$VOLTAGE_NEW ")

        // Then
        verify(fragment).display()
        verify(fragment).updateVoltageActual(VOLTAGE_NEW)
    }

    @Test
    fun onUpdateVoltageActual_withVoltageRequired_update() {
        // Given
        injectMocks()
        mockCheckFullCharge(false)
        mockEditKmWith("$KM")
        mockEditVoltageTargetWith("$VOLTAGE_NEW_RAW")

        doNothing().`when`(fragment).display()

        // When
        fragment.onUpdateVoltageActual().invoke("$VOLTAGE ")

        // Then
        verify(fragment).display()
        verify(fragment).updateVoltageActual(VOLTAGE)
    }

    @Test
    fun onUpdateVoltageActual_whenInvalid_displayBlanks() {
        onUpdateVoltageActual_whenInvalid_displayBlanks(" ")
        onUpdateVoltageActual_whenInvalid_displayBlanks("-$VOLTAGE_NEW ")
        onUpdateVoltageActual_whenInvalid_displayBlanks("${WHEEL.voltageMin - 0.1f} ")
        onUpdateVoltageActual_whenInvalid_displayBlanks("a ")
    }

    private fun onUpdateVoltageActual_whenInvalid_displayBlanks(voltage: String) {
        // Given
        reset(fragment)
        injectMocks()

        // When
        fragment.onUpdateVoltageActual().invoke(voltage)

        // Then
        verify(fragment).displayBlanks()
        verify(fragment, never()).updateVoltageActual(anyFloat())
    }

    @Test
    fun onUpdateVoltageTarget() {
        // Given
        injectMocks()
        doNothing().`when`(fragment).display()

        // When
        fragment.onUpdateVoltageTarget().invoke("$VOLTAGE_NEW_RAW ")

        // Then
        verify(mockedSwitchFullCharge).isChecked = false
        verify(mockedEditKm).setText("")
        verify(fragment).display()
    }

    @Test
    fun onUpdateVoltageTarget_whenInvalid_displayBlanks() {
        onUpdateVoltageTarget_whenInvalid_displayBlanks(" ")
        onUpdateVoltageTarget_whenInvalid_displayBlanks("-$VOLTAGE ")
        onUpdateVoltageTarget_whenInvalid_displayBlanks("a ")
    }

    private fun onUpdateVoltageTarget_whenInvalid_displayBlanks(voltage: String) {
        // Given
        reset(fragment)
        injectMocks()

        // When
        fragment.onUpdateVoltageTarget().invoke(voltage)

        // Then
        verify(fragment).displayBlanks()
    }

    @Test
    fun updateVoltageActual_whenFirstTime() {
        // Given
        injectMocks()
        mockChargeContext(KM, VOLTAGE)

        fragment.cacheVoltageActual = null
        fragment.chargerOffset = null

        // When
        fragment.updateVoltageActual(VOLTAGE_NEW)

        // Then
        verify(mockedWidgets).hide(mockedTextChargeWarning)

        assertThat(fragment.cacheVoltageActual, equalTo(VOLTAGE_NEW))
        assertThat(fragment.chargerOffset, equalTo(round(VOLTAGE_NEW - VOLTAGE)))
    }

    @Test
    fun updateVoltageActual_whenNotFirstTime() {
        // Given
        val value = 90000f

        fragment.cacheVoltageActual = VOLTAGE
        fragment.chargerOffset = value

        // When
        fragment.updateVoltageActual(VOLTAGE_NEW)

        // Then
        assertThat(fragment.cacheVoltageActual, equalTo(VOLTAGE_NEW))
        assertThat(fragment.chargerOffset, equalTo(value))
    }

    private fun injectMocks() {
        fragment.editKm = mockedEditKm
        fragment.editVoltageActual = mockedEditVoltageActual
        fragment.editVoltageTarget = mockedEditVoltageTarget
        fragment.textChargeWarning = mockedTextChargeWarning
        fragment.textEstimatedDiff = mockedTextEstimatedDiff
        fragment.textEstimatedTime = mockedTextEstimatedTime
        fragment.textVoltageRequired = mockedTextVoltageRequired
        fragment.textVoltageRequiredDiff = mockedTextVoltageRequiredDiff
        fragment.textVoltageTarget = mockedTextVoltageTarget
        fragment.textVoltageTargetDiff = mockedTextVoltageTargetDiff
    }

    private fun mockChargeContext(km: Float, voltage: Float) {
        BaseFragment.chargeContext.km = km
        BaseFragment.chargeContext.voltage = voltage
    }

    private fun mockChargeContext(chargerOffset: Float) {
        fragment.chargerOffset = chargerOffset
    }

    private fun mockChargeContext(km: Float, voltage: Float, chargerOffset: Float) {
        mockChargeContext(km, voltage)
        mockChargeContext(chargerOffset)
    }

    private fun mockFields() {
        mockField(R.id.button_connect_charge, mockedButtonConnect)
        mockField(R.id.check_full_charge, mockedSwitchFullCharge)
        mockField(R.id.edit_km, mockedEditKm)
        mockField(R.id.edit_voltage_actual, mockedEditVoltageActual)
        mockField(R.id.edit_voltage_target, mockedEditVoltageTarget)
        mockField(R.id.view_charge_warning, mockedTextChargeWarning)
        mockField(R.id.view_estimated_diff, mockedTextEstimatedDiff)
        mockField(R.id.view_estimated_time, mockedTextEstimatedTime)
        mockField(R.id.view_name, mockedTextName)
        mockField(R.id.view_voltage_required, mockedTextVoltageRequired)
        mockField(R.id.view_voltage_required_diff, mockedTextVoltageRequiredDiff)
        mockField(R.id.view_voltage_target, mockedTextVoltageTarget)
        mockField(R.id.view_voltage_target_diff, mockedTextVoltageTargetDiff)
    }

    private fun mockCheckFullCharge(value: Boolean) {
        given(mockedSwitchFullCharge.isChecked).willReturn(value)
    }

    private fun mockEditKmWith(km: String) {
        given(mockedWidgets.getText(mockedEditKm)).willReturn(km)
    }

    private fun mockEditVoltageTargetWith(voltage: String) {
        given(mockedWidgets.getText(mockedEditVoltageTarget)).willReturn(voltage)
    }

    private fun offCharge(voltage: Float) = round(voltage - CHARGER_OFFSET)

    private fun onCharge(voltage: Float) = round(voltage + CHARGER_OFFSET)

    private fun verifyVoltage(voltage: Float) {
        verify(mockedTextVoltageRequired).setText("$voltage")
        verify(mockedTextVoltageTarget).setText("${offCharge(voltage)}")
        verify(mockedTextVoltageTargetDiff).setText("V (-$CHARGER_OFFSET)")
    }
}
