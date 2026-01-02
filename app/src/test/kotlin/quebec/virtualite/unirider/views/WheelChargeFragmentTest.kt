package quebec.virtualite.unirider.views

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.switchmaterial.SwitchMaterial
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.doNothing
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.commons.android.utils.DateUtils.Companion.simulateNow
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.TestDomain.KM
import quebec.virtualite.unirider.TestDomain.KM_NEW_RAW
import quebec.virtualite.unirider.TestDomain.KM_REQUESTED
import quebec.virtualite.unirider.TestDomain.MILEAGE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.S18_1
import quebec.virtualite.unirider.TestDomain.TEMPERATURE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.VOLTAGE
import quebec.virtualite.unirider.TestDomain.VOLTAGE_FULL
import quebec.virtualite.unirider.TestDomain.VOLTAGE_NEW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_NEW_RAW
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
    lateinit var mockedEditVoltageRequired: EditText

    @Mock
    lateinit var mockedEditVoltageActual: EditText

    @Mock
    lateinit var mockedTextVoltageRequiredDiff: TextView

    @Mock
    lateinit var mockedTextEstimatedTime: TextView

    @Mock
    lateinit var mockedTextEstimatedDiff: TextView

    @Mock
    lateinit var mockedTextName: TextView

    @Mock
    lateinit var mockedTextVoltageRequired: TextView

    private val WHEEL = S18_1

    @Before
    fun before() {
        BaseFragment.wheel = WHEEL
        fragment.cacheVoltageActual = VOLTAGE + WHEEL.chargerOffset

        simulateNow("11:45")
        mockExternal()
        mockFields()
        mockFragments()
    }

    @Test
    fun display_whenVoltageActualIsLowerThanRequired() {
        // Given
        injectMocks()

        fragment.cacheVoltageActual = 77.5f
        doReturn(80f).`when`(fragment).getVoltageRequired()

        // When
        fragment.display()

        // Then
        verify(fragment).displayEstimates(2.5f, 0.625f)
    }

    @Test
    fun display_whenVoltageActualIsSameAsRequired() {
        // Given
        injectMocks()

        fragment.cacheVoltageActual = VOLTAGE
        doReturn(VOLTAGE).`when`(fragment).getVoltageRequired()

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
        verify(mockedTextVoltageRequiredDiff).text = "(+2.5)"
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
        verify(mockedTextVoltageRequiredDiff).text = "Go!"
        verify(mockedTextEstimatedDiff).text = ""
        verify(mockedTextEstimatedTime).text = ""
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
        mockCheckFullCharge(true)
        mockEditVoltageRequiredWith("")

        given(mockedCalculatorService.requiredVoltageFull(WHEEL))
            .willReturn(VOLTAGE_FULL)

        // When
        val result = fragment.getVoltageRequired()

        // Then
        verify(mockedTextVoltageRequired).setText("$VOLTAGE_FULL")

        assertThat(result, equalTo(VOLTAGE_FULL))
    }

    @Test
    fun getVoltageRequired_whenFullChargeAndVoltageRequired() {
        // Given
        injectMocks()
        mockCheckFullCharge(true)
        mockEditVoltageRequiredWith("$VOLTAGE")

        given(mockedCalculatorService.requiredVoltageFull(WHEEL))
            .willReturn(VOLTAGE_FULL)

        // When
        val result = fragment.getVoltageRequired()

        // Then
        verify(mockedTextVoltageRequired).setText("$VOLTAGE_FULL")

        assertThat(result, equalTo(VOLTAGE_FULL))
    }

    @Test
    fun getVoltageRequired_whenKmIsSet() {
        // Given
        injectMocks()
        mockChargeContext(KM, 71f)
        mockCheckFullCharge(false)
        mockEditKmWith("$KM_REQUESTED")
        mockEditVoltageRequiredWith("")

        given(mockedCalculatorService.requiredVoltageOffCharger(WHEEL, 71f, KM, KM_REQUESTED))
            .willReturn(75f)
        val expectedVoltageRequired = 75f + WHEEL.chargerOffset

        // When
        val result = fragment.getVoltageRequired()

        // Then
        verify(mockedTextVoltageRequired).setText("$expectedVoltageRequired")

        assertThat(result, equalTo(expectedVoltageRequired))
    }

    @Test
    fun getVoltageRequired_whenKmIsSetToFull() {
        // Given
        injectMocks()
        mockChargeContext(KM, VOLTAGE)
        mockCheckFullCharge(false)
        mockEditKmWith("$KM_REQUESTED")
        mockEditVoltageRequiredWith("")

        given(mockedCalculatorService.requiredVoltageOffCharger(WHEEL, VOLTAGE, KM, KM_REQUESTED))
            .willReturn(VOLTAGE_FULL)

        // When
        val result = fragment.getVoltageRequired()

        // Then
        verify(mockedTextVoltageRequired).setText("$VOLTAGE_FULL")

        assertThat(result, equalTo(VOLTAGE_FULL))
    }

    @Test
    fun getVoltageRequired_whenVoltageIsSetRequired() {
        // Given
        injectMocks()
        mockCheckFullCharge(false)
        mockEditVoltageRequiredWith("$VOLTAGE_NEW")

        // When
        val result = fragment.getVoltageRequired()

        // Then
        verifyNoInteractions(mockedCalculatorService)
        verify(mockedTextVoltageRequired).setText("$VOLTAGE_NEW")

        assertThat(result, equalTo(VOLTAGE_NEW))
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
        BaseFragment.chargeContext.voltage = VOLTAGE
        val voltageActual = VOLTAGE + WHEEL.chargerOffset

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verifyFieldAssignment(R.id.button_connect_charge, fragment.buttonConnect, mockedButtonConnect)
        verifyFieldAssignment(R.id.check_full_charge, fragment.switchFullCharge, mockedSwitchFullCharge)
        verifyFieldAssignment(R.id.edit_km, fragment.editKm, mockedEditKm)
        verifyFieldAssignment(R.id.edit_voltage_actual, fragment.editVoltageActual, mockedEditVoltageActual)
        verifyFieldAssignment(R.id.edit_voltage_required, fragment.editVoltageRequired, mockedEditVoltageRequired)
        verifyFieldAssignment(R.id.view_estimated_diff, fragment.textEstimatedDiff, mockedTextEstimatedDiff)
        verifyFieldAssignment(R.id.view_estimated_time, fragment.textEstimatedTime, mockedTextEstimatedTime)
        verifyFieldAssignment(R.id.view_name, fragment.textName, mockedTextName)
        verifyFieldAssignment(R.id.view_voltage_required, fragment.textVoltageRequired, mockedTextVoltageRequired)
        verifyFieldAssignment(R.id.view_voltage_required_diff, fragment.textVoltageRequiredDiff, mockedTextVoltageRequiredDiff)

        verifyOnClick(mockedButtonConnect, "onConnect")
        verifyOnUpdateText(mockedEditKm, "onUpdateKm")
        verifyOnUpdateText(mockedEditVoltageActual, "onUpdateVoltageActual")
        verifyOnUpdateText(mockedEditVoltageRequired, "onUpdateVoltageRequired")
        verifyOnToggleSwitch(mockedSwitchFullCharge, "onToggleFullCharge")

        verify(mockedEditVoltageActual).setText("$voltageActual")
        verify(mockedSwitchFullCharge).isChecked = true
        verify(mockedTextName).text = WHEEL.name

        assertThat(fragment.cacheVoltageActual, equalTo(voltageActual))
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

        assertThat(fragment.cacheVoltageActual, equalTo(VOLTAGE_NEW))
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
    fun onUpdateVoltageActual_whenFullChargeIsEnabled() {
        // Given
        injectMocks()
        mockCheckFullCharge(true)

        doNothing().`when`(fragment).display()

        // When
        fragment.onUpdateVoltageActual().invoke("$VOLTAGE_NEW_RAW ")

        // Then
        verify(fragment).display()

        assertThat(fragment.cacheVoltageActual, equalTo(VOLTAGE_NEW_RAW))
    }

    @Test
    fun onUpdateVoltageActual_withNoKmAndNoVoltageRequired_displayBlanks() {
        // Given
        injectMocks()
        mockCheckFullCharge(false)
        mockEditKmWith("")
        mockEditVoltageRequiredWith("")

        doNothing().`when`(fragment).displayBlanks()

        // When
        fragment.onUpdateVoltageActual().invoke("$VOLTAGE_NEW_RAW ")

        // Then
        verify(fragment).displayBlanks()

        assertThat(fragment.cacheVoltageActual, equalTo(VOLTAGE_NEW_RAW))
    }

    @Test
    fun onUpdateVoltageActual_withKmAndNoVoltageRequired_displayBlanks() {
        // Given
        injectMocks()
        mockCheckFullCharge(false)
        mockEditKmWith("$KM")
        mockEditVoltageRequiredWith("")

        doNothing().`when`(fragment).display()

        // When
        fragment.onUpdateVoltageActual().invoke("$VOLTAGE_NEW_RAW ")

        // Then
        verify(fragment).display()

        assertThat(fragment.cacheVoltageActual, equalTo(VOLTAGE_NEW_RAW))
    }

    @Test
    fun onUpdateVoltageActual_withVoltageRequired_displayBlanks() {
        // Given
        injectMocks()
        mockCheckFullCharge(false)
        mockEditKmWith("$KM")
        mockEditVoltageRequiredWith("$VOLTAGE_NEW_RAW")

        doNothing().`when`(fragment).display()

        // When
        fragment.onUpdateVoltageActual().invoke("$VOLTAGE ")

        // Then
        verify(fragment).display()

        assertThat(fragment.cacheVoltageActual, equalTo(VOLTAGE))
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

        val oldVoltageActual = fragment.cacheVoltageActual

        // When
        fragment.onUpdateVoltageActual().invoke(voltage)

        // Then
        verify(fragment).displayBlanks()

        assertThat(fragment.cacheVoltageActual, equalTo(oldVoltageActual))
    }

    @Test
    fun onUpdateVoltageRequired() {
        // Given
        injectMocks()
        doNothing().`when`(fragment).display()

        // When
        fragment.onUpdateVoltageRequired().invoke("$VOLTAGE_NEW_RAW ")

        // Then
        verify(mockedSwitchFullCharge).isChecked = false
        verify(mockedEditKm).setText("")
        verify(fragment).display()
    }

    @Test
    fun onUpdateVoltageRequired_whenInvalid_displayBlanks() {
        onUpdateVoltageRequired_whenInvalid_displayBlanks(" ")
        onUpdateVoltageRequired_whenInvalid_displayBlanks("-$VOLTAGE ")
        onUpdateVoltageRequired_whenInvalid_displayBlanks("a ")
    }

    private fun onUpdateVoltageRequired_whenInvalid_displayBlanks(voltage: String) {
        // Given
        reset(fragment)
        injectMocks()

        // When
        fragment.onUpdateVoltageRequired().invoke(voltage)

        // Then
        verify(fragment).displayBlanks()
    }

    private fun injectMocks() {
        fragment.editKm = mockedEditKm
        fragment.editVoltageActual = mockedEditVoltageActual
        fragment.editVoltageRequired = mockedEditVoltageRequired
        fragment.textVoltageRequiredDiff = mockedTextVoltageRequiredDiff
        fragment.textEstimatedDiff = mockedTextEstimatedDiff
        fragment.textEstimatedTime = mockedTextEstimatedTime
        fragment.textVoltageRequired = mockedTextVoltageRequired
    }

    private fun mockChargeContext(km: Float, voltage: Float) {
        BaseFragment.chargeContext.km = km
        BaseFragment.chargeContext.voltage = voltage
    }

    private fun mockFields() {
        mockField(R.id.button_connect_charge, mockedButtonConnect)
        mockField(R.id.check_full_charge, mockedSwitchFullCharge)
        mockField(R.id.edit_km, mockedEditKm)
        mockField(R.id.edit_voltage_actual, mockedEditVoltageActual)
        mockField(R.id.edit_voltage_required, mockedEditVoltageRequired)
        mockField(R.id.view_estimated_diff, mockedTextEstimatedDiff)
        mockField(R.id.view_estimated_time, mockedTextEstimatedTime)
        mockField(R.id.view_name, mockedTextName)
        mockField(R.id.view_voltage_required, mockedTextVoltageRequired)
        mockField(R.id.view_voltage_required_diff, mockedTextVoltageRequiredDiff)
    }

    private fun mockCheckFullCharge(value: Boolean) {
        given(mockedSwitchFullCharge.isChecked).willReturn(value)
    }

    private fun mockEditKmWith(km: String) {
        given(mockedWidgets.getText(mockedEditKm)).willReturn(km)
    }

    private fun mockEditVoltageRequiredWith(voltage: String) {
        given(mockedWidgets.getText(mockedEditVoltageRequired)).willReturn(voltage)
    }
}
