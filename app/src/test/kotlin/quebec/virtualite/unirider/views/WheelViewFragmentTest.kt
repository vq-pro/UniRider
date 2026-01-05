package quebec.virtualite.unirider.views

import android.view.View.GONE
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.BDDMockito.doNothing
import org.mockito.BDDMockito.given
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.lenient
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.bluetooth.WheelInfo
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.services.CalculatorService.EstimatedValues
import quebec.virtualite.unirider.test.domain.TestConstants.DEVICE_ADDR
import quebec.virtualite.unirider.test.domain.TestConstants.DEVICE_NAME
import quebec.virtualite.unirider.test.domain.TestConstants.ITEM_SOLD
import quebec.virtualite.unirider.test.domain.TestConstants.KM
import quebec.virtualite.unirider.test.domain.TestConstants.KM_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.KM_NEW_RAW
import quebec.virtualite.unirider.test.domain.TestConstants.MILEAGE
import quebec.virtualite.unirider.test.domain.TestConstants.MILEAGE_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.MILEAGE_NEW_RAW
import quebec.virtualite.unirider.test.domain.TestConstants.NAME
import quebec.virtualite.unirider.test.domain.TestConstants.PERCENTAGE
import quebec.virtualite.unirider.test.domain.TestConstants.PREMILEAGE
import quebec.virtualite.unirider.test.domain.TestConstants.REMAINING_RANGE
import quebec.virtualite.unirider.test.domain.TestConstants.S18_1_CONNECTED
import quebec.virtualite.unirider.test.domain.TestConstants.S18_1_DISCONNECTED
import quebec.virtualite.unirider.test.domain.TestConstants.SHERMAN_MAX_3_SOLD
import quebec.virtualite.unirider.test.domain.TestConstants.TEMPERATURE_NEW_RAW
import quebec.virtualite.unirider.test.domain.TestConstants.TOTAL_RANGE
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_NEW5
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_NEW_RAW
import quebec.virtualite.unirider.views.BaseFragment.Companion.wheel

@RunWith(MockitoJUnitRunner::class)
class WheelViewFragmentTest : FragmentTestBase(WheelViewFragment::class.java) {

    private val INVALID_KM = null
    private val INVALID_VOLTAGE_ACTUAL = null

    private val INITIAL_WHEEL = S18_1_DISCONNECTED

    @InjectMocks
    @Spy
    private lateinit var fragment: WheelViewFragment

    @Captor
    private lateinit var captorExecution: ArgumentCaptor<() -> Unit>

    @Mock
    private lateinit var mockedButtonCharge: Button

    @Mock
    private lateinit var mockedButtonConnect: Button

    @Mock
    private lateinit var mockedButtonEdit: Button

    @Mock
    private lateinit var mockedCalculatorService: CalculatorService

    @Mock
    private lateinit var mockedEditKm: EditText

    @Mock
    private lateinit var mockedEditVoltageActual: EditText

    @Mock
    private lateinit var mockedLabelBattery: TextView

    @Mock
    private lateinit var mockedLabelBtName: TextView

    @Mock
    private lateinit var mockedLabelRemainingRange: TextView

    @Mock
    private lateinit var mockedLabelTotalRange: TextView

    @Mock
    private lateinit var mockedTextBattery: TextView

    @Mock
    private lateinit var mockedTextBtAddr: TextView

    @Mock
    private lateinit var mockedTextBtName: TextView

    @Mock
    private lateinit var mockedTextMileage: TextView

    @Mock
    private lateinit var mockedTextName: TextView

    @Mock
    private lateinit var mockedTextRemainingRange: TextView

    @Mock
    private lateinit var mockedTextTotalRange: TextView

    @Before
    fun before() {
        wheel = INITIAL_WHEEL

        mockExternal()
        mockFields()
        mockFragments()
        mockStrings()
    }

    @Test
    fun clearDisplay() {
        // Given
        injectMocks()

        doNothing().`when`(fragment).clearPercentage()
        doNothing().`when`(fragment).clearEstimates()

        // When
        fragment.clearDisplay()

        // Then
        verify(fragment).clearPercentage()
        verify(fragment).clearEstimates()
    }

    @Test
    fun clearEstimates() {
        // Given
        injectMocks()

        // When
        fragment.clearEstimates()

        // Then
        verify(mockedWidgets).hide(
            mockedTextRemainingRange, mockedLabelRemainingRange, mockedTextTotalRange, mockedLabelTotalRange
        )
        verify(mockedWidgets).disable(mockedButtonCharge)
    }

    @Test
    fun clearPercentage() {
        // Given
        injectMocks()

        // When
        fragment.clearPercentage()

        // Then
        verify(mockedWidgets).hide(mockedTextBattery, mockedLabelBattery)
    }

    @Test
    fun onCreateView() {
        // When
        fragment.onCreateView(mockedInflater, mockedContainer, SAVED_INSTANCE_STATE)

        // Then
        verifyInflate(R.layout.wheel_view_fragment)
    }

    @Test
    fun onViewCreated() {
        // Given
        mockInitialDisplay()

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verifyFieldAssignment(R.id.button_charge, fragment.buttonCharge, mockedButtonCharge)
        verifyFieldAssignment(R.id.button_connect, fragment.buttonConnect, mockedButtonConnect)
        verifyFieldAssignment(R.id.button_edit, fragment.buttonEdit, mockedButtonEdit)
        verifyFieldAssignment(R.id.edit_km, fragment.editKm, mockedEditKm)
        verifyFieldAssignment(R.id.edit_voltage_actual, fragment.editVoltageActual, mockedEditVoltageActual)
        verifyFieldAssignment(R.id.label_battery, fragment.labelBattery, mockedLabelBattery)
        verifyFieldAssignment(R.id.label_bt_name, fragment.labelBtName, mockedLabelBtName)
        verifyFieldAssignment(R.id.label_remaining_range, fragment.labelRemainingRange, mockedLabelRemainingRange)
        verifyFieldAssignment(R.id.label_total_range, fragment.labelTotalRange, mockedLabelTotalRange)
        verifyFieldAssignment(R.id.view_battery, fragment.textBattery, mockedTextBattery)
        verifyFieldAssignment(R.id.view_bt_addr, fragment.textBtAddr, mockedTextBtAddr)
        verifyFieldAssignment(R.id.view_bt_name, fragment.textBtName, mockedTextBtName)
        verifyFieldAssignment(R.id.view_mileage, fragment.textMileage, mockedTextMileage)
        verifyFieldAssignment(R.id.view_name, fragment.textName, mockedTextName)
        verifyFieldAssignment(R.id.view_remaining_range, fragment.textRemainingRange, mockedTextRemainingRange)
        verifyFieldAssignment(R.id.view_total_range, fragment.textTotalRange, mockedTextTotalRange)

        verifyOnClick(mockedButtonCharge, "onCharge")
        verifyOnClick(mockedButtonConnect, "onConnect")
        verifyOnClick(mockedButtonEdit, "onEdit")
        verifyOnUpdateText(mockedEditKm, "onUpdateKm")
        verifyOnUpdateText(mockedEditVoltageActual, "onUpdateVoltageActual")
        verifyOnLongClick(mockedTextBtName, "onDisconnect")

        verify(fragment).initialDisplayWheel()
        verify(mockedTextMileage).text = "${PREMILEAGE + MILEAGE}"

        assertThat(fragment.buttonCharge, equalTo(mockedButtonCharge))
        assertThat(fragment.textBtName, equalTo(mockedTextBtName))
        assertThat(fragment.textMileage, equalTo(mockedTextMileage))
        assertThat(fragment.textName, equalTo(mockedTextName))
        assertThat(fragment.textRemainingRange, equalTo(mockedTextRemainingRange))
        assertThat(fragment.textTotalRange, equalTo(mockedTextTotalRange))
    }

    @Test
    fun onViewCreated_whenWheelIsSold() {
        // Given
        wheel = SHERMAN_MAX_3_SOLD

        injectMocks()

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(fragment).initialDisplaySoldWheel()
    }

    @Test
    fun onCharge_withConnectedWheel() {
        // Given
        wheel = S18_1_CONNECTED

        doNothing().`when`(fragment).startCharging()

        // When
        fragment.onCharge().invoke(mockedView)

        // Then
        verify(fragment).reconnect(captorExecution.capture())
        captorExecution.value.invoke()

        verify(fragment).startCharging()
    }

    @Test
    fun onCharge_withDisconnectedWheel() {
        // Given
        wheel = S18_1_DISCONNECTED

        doNothing().`when`(fragment).startCharging()

        // When
        fragment.onCharge().invoke(mockedView)

        // Then
        verify(fragment).startCharging()
        verify(fragment, never()).reconnect(any())
    }

    @Test
    fun onConnect_withConnectedWheel() {
        // Given
        wheel = S18_1_CONNECTED

        // When
        fragment.onConnect().invoke(mockedView)

        // Then
        verify(fragment).reconnect()
    }

    @Test
    fun onConnect_withDisconnectedWheel() {
        // Given
        wheel = S18_1_DISCONNECTED

        // When
        fragment.onConnect().invoke(mockedView)

        // Then
        verify(fragment).scan()
    }

    @Test
    fun onDisconnect() {
        // When
        fragment.onDisconnect().invoke(mockedView)

        // Then
        verify(mockedFragments).navigateTo(R.id.action_WheelViewFragment_to_WheelConfirmationDisconnectFragment)
    }

    @Test
    fun onEdit() {
        // When
        fragment.onEdit().invoke(mockedView)

        // Then
        verify(mockedFragments).navigateTo(R.id.action_WheelViewFragment_to_WheelEditFragment)
    }

    @Test
    fun onUpdateKm() {
        // Given
        doReturn(KM).`when`(fragment).parseKm("$KM")
        doReturn(VOLTAGE).`when`(fragment).readVoltageActual()

        doNothing().`when`(fragment).refreshDisplay(VOLTAGE, KM)

        // When
        fragment.onUpdateKm().invoke("$KM")

        // Then
        verify(fragment).parseKm("$KM")
        verify(fragment).readVoltageActual()

        verify(fragment).refreshDisplay(VOLTAGE, KM)
    }

    @Test
    fun onUpdateVoltageActual() {
        // Given
        injectMocks()

        doReturn(VOLTAGE_NEW).`when`(fragment).parseVoltage("$VOLTAGE_NEW_RAW ")
        doReturn(KM).`when`(fragment).readKm()
        doNothing().`when`(fragment).refreshDisplay(VOLTAGE_NEW, KM)

        // When
        fragment.onUpdateVoltageActual().invoke("$VOLTAGE_NEW_RAW ")

        // Then
        verify(fragment).parseVoltage("$VOLTAGE_NEW_RAW ")
        verify(fragment).readKm()

        verify(fragment).refreshDisplay(VOLTAGE_NEW, KM)
    }

    @Test
    fun initialDisplayBluetoothSettings() {
        // Given
        injectMocks()

        wheel = S18_1_CONNECTED

        // When
        fragment.initialDisplayBluetoothSettings()

        // Then
        verify(mockedWidgets).show(mockedLabelBtName)
        verify(mockedTextBtName).text = DEVICE_NAME
        verify(mockedTextBtAddr).text = DEVICE_ADDR
    }

    @Test
    fun initialDisplaySoldWheel() {
        // Given
        injectMocks()

        // When
        fragment.initialDisplaySoldWheel()

        // Then
        verify(mockedFragments).string(R.string.label_wheel_sold)

        verify(mockedTextName).text = "${wheel?.name} ($ITEM_SOLD)"

        verify(mockedButtonCharge).visibility = GONE
        verify(mockedButtonConnect).visibility = GONE

        verify(fragment, never()).refreshDisplay(anyFloat(), anyFloat())
    }

    @Test
    fun initialDisplayWheel_whenConnected() {
        // Given
        injectMocks()
        mockRefreshDisplay(VOLTAGE, KM)

        wheel = S18_1_CONNECTED

        // When
        fragment.initialDisplayWheel()

        // Then
        verify(mockedTextName).text = NAME
        verify(fragment).initialDisplayBluetoothSettings()

        verify(fragment).refreshDisplay(VOLTAGE, KM)
    }

    @Test
    fun initialDisplayWheel_whenDisconnected() {
        // Given
        injectMocks()
        mockRefreshDisplay(VOLTAGE, KM)

        wheel = S18_1_DISCONNECTED

        // When
        fragment.initialDisplayWheel()

        // Then
        verify(mockedTextName).text = NAME
        verify(fragment, never()).initialDisplayBluetoothSettings()

        verify(fragment).refreshDisplay(VOLTAGE, KM)
    }

    @Test
    fun parseKm() {
        // When
        val result = fragment.parseKm("$KM ")

        // Then
        assertThat(result, equalTo(KM))
    }

    @Test
    fun parseKm_whenEmpty() {
        // When
        val result = fragment.parseKm(" ")

        // Then
        assertThat(result, equalTo(null))
    }

    @Test
    fun parseKm_whenInvalid() {
        // When
        val result = fragment.parseKm("ab ")

        // Then
        assertThat(result, equalTo(null))
    }

    @Test
    fun parseKm_whenZero() {
        // When
        val result = fragment.parseKm("0.0 ")

        // Then
        assertThat(result, equalTo(null))
    }

    @Test
    fun parseVoltage() {
        // When
        val result = fragment.parseVoltage("$VOLTAGE ")

        // Then
        assertThat(result, equalTo(VOLTAGE))
    }

    @Test
    fun parseVoltage_whenInvalid() {
        // When
        val result = fragment.parseVoltage("ab ")

        // Then
        assertThat(result, equalTo(null))
    }

    @Test
    fun parseVoltage_whenLowerThanMinimum() {
        // Given
        injectMocks()

        // When
        val result = fragment.parseVoltage("${INITIAL_WHEEL.voltageMin - 0.1f} ")

        // Then
        assertThat(result, equalTo(null))
    }

    @Test
    fun parseVoltage_withTooManyDecimals() {
        // Given
        injectMocks()

        // When
        val result = fragment.parseVoltage("${VOLTAGE + 0.0001f} ")

        // Then
        assertThat(result, equalTo(VOLTAGE))
    }

    @Test
    fun readKm() {
        // Given
        injectMocks()

        doReturn("$KM").`when`(mockedWidgets).getText(mockedEditKm)
        doReturn(KM).`when`(fragment).parseKm("$KM")

        // When
        val result = fragment.readKm()

        // Then
        verify(mockedWidgets).getText(mockedEditKm)
        verify(fragment).parseKm("$KM")

        assertThat(result, equalTo(KM))
    }

    @Test
    fun readVoltageActual() {
        // Given
        injectMocks()

        doReturn("$VOLTAGE").`when`(mockedWidgets).getText(mockedEditVoltageActual)
        doReturn(VOLTAGE).`when`(fragment).parseVoltage("$VOLTAGE")

        // When
        val result = fragment.readVoltageActual()

        // Then
        verify(mockedWidgets).getText(mockedEditVoltageActual)
        verify(fragment).parseVoltage("$VOLTAGE")

        assertThat(result, equalTo(VOLTAGE))
    }

    @Test
    fun reconnect() {
        // Given
        injectMocks()

        wheel = S18_1_CONNECTED

        val connectionPayload = WheelInfo(KM_NEW_RAW, MILEAGE_NEW_RAW, TEMPERATURE_NEW_RAW, VOLTAGE_NEW_RAW)
        var executed = false

        // When
        fragment.reconnect { executed = true }

        // Then
        verifyRunWithWaitDialog()
        verifyConnectorGetDeviceInfo(DEVICE_ADDR, connectionPayload)
        verifyDoneWaiting(connectionPayload)

        verify(mockedDb).saveWheel(S18_1_CONNECTED.copy(mileage = MILEAGE_NEW))
        verify(mockedEditKm).setText("$KM_NEW")
        verify(mockedEditVoltageActual).setText("$VOLTAGE_NEW")
        verify(mockedTextMileage).text = "${PREMILEAGE + MILEAGE_NEW}"

        assertThat(executed, equalTo(true))
    }

    @Test
    fun refreshDisplay() {
        // Given
        injectMocks()

        doReturn(PERCENTAGE).`when`(mockedCalculatorService).percentage(BaseFragment.wheel, VOLTAGE)
        doNothing().`when`(fragment).refreshEstimates(VOLTAGE, KM)

        // When
        fragment.refreshDisplay(VOLTAGE, KM)

        // Then
        verify(fragment).refreshEstimates(VOLTAGE, KM)
        verify(fragment).updatePercentageFor(VOLTAGE)
    }

    @Test
    fun refreshDisplay_whenInvalidKm() {
        // Given
        injectMocks()

        doNothing().`when`(fragment).clearEstimates()
        doNothing().`when`(fragment).updatePercentageFor(VOLTAGE)

        // When
        fragment.refreshDisplay(VOLTAGE, INVALID_KM)

        // Then
        verify(fragment).clearEstimates()
        verify(fragment).updatePercentageFor(VOLTAGE)
    }

    @Test
    fun refreshDisplay_whenInvalidVoltageActual() {
        // Given
        injectMocks()

        // When
        fragment.refreshDisplay(INVALID_VOLTAGE_ACTUAL, KM)

        // Then
        verify(fragment).clearPercentage()
        verify(fragment).clearEstimates()
    }

    @Test
    fun refreshEstimates() {
        // Given
        injectMocks()

        val estimates = EstimatedValues(REMAINING_RANGE, TOTAL_RANGE)
        doReturn(estimates).`when`(mockedCalculatorService).estimatedValues(BaseFragment.wheel!!, VOLTAGE, KM)

        // When
        fragment.refreshEstimates(VOLTAGE, KM)

        // Then
        verify(mockedCalculatorService).estimatedValues(BaseFragment.wheel!!, VOLTAGE, KM)

        verify(mockedTextRemainingRange).text = "$REMAINING_RANGE"
        verify(mockedTextTotalRange).text = "$TOTAL_RANGE"

        verify(mockedWidgets).show(
            mockedTextRemainingRange, mockedLabelRemainingRange, mockedTextTotalRange, mockedLabelTotalRange
        )
        verify(mockedWidgets).enable(mockedButtonCharge)

        assertThat(fragment.estimates, equalTo(estimates))
    }

    @Test
    fun refreshPercentageFor() {
        // Given
        injectMocks()

        doReturn(PERCENTAGE).`when`(mockedCalculatorService).percentage(BaseFragment.wheel, VOLTAGE)

        // When
        fragment.updatePercentageFor(VOLTAGE)

        // Then
        verify(mockedCalculatorService).percentage(BaseFragment.wheel, VOLTAGE)

        verify(mockedTextBattery).text = "$PERCENTAGE"
        verify(mockedWidgets).show(mockedTextBattery, mockedLabelBattery)
    }

    @Test
    fun scan() {
        // When
        fragment.scan()

        // Then
        verify(mockedFragments).navigateTo(R.id.action_WheelViewFragment_to_WheelScanFragment)
    }

    @Test
    fun startCharging() {
        // Given
        injectMocks()

        BaseFragment.chargeContext.km = -1f
        BaseFragment.chargeContext.voltage = -1f

        given(mockedWidgets.getText(mockedEditKm)).willReturn(KM_NEW.toString())
        given(mockedWidgets.getText(mockedEditVoltageActual)).willReturn(VOLTAGE_NEW5.toString())

        // When
        fragment.startCharging()

        // Then
        verify(mockedFragments).navigateTo(R.id.action_WheelViewFragment_to_WheelChargeFragment)

        assertThat(BaseFragment.chargeContext.km, equalTo(KM_NEW))
        assertThat(BaseFragment.chargeContext.voltage, equalTo(VOLTAGE_NEW5))
    }

    private fun injectMocks() {
        fragment.buttonCharge = mockedButtonCharge
        fragment.buttonConnect = mockedButtonConnect
        fragment.editKm = mockedEditKm
        fragment.editVoltageActual = mockedEditVoltageActual
        fragment.labelBattery = mockedLabelBattery
        fragment.labelBtName = mockedLabelBtName
        fragment.labelRemainingRange = mockedLabelRemainingRange
        fragment.labelTotalRange = mockedLabelTotalRange
        fragment.textBattery = mockedTextBattery
        fragment.textBtAddr = mockedTextBtAddr
        fragment.textBtName = mockedTextBtName
        fragment.textMileage = mockedTextMileage
        fragment.textName = mockedTextName
        fragment.textRemainingRange = mockedTextRemainingRange
        fragment.textTotalRange = mockedTextTotalRange
    }

    private fun mockFields() {
        mockField(R.id.button_charge, mockedButtonCharge)
        mockField(R.id.button_connect, mockedButtonConnect)
        mockField(R.id.button_edit, mockedButtonEdit)
        mockField(R.id.edit_km, mockedEditKm)
        mockField(R.id.edit_voltage_actual, mockedEditVoltageActual)
        mockField(R.id.label_battery, mockedLabelBattery)
        mockField(R.id.label_bt_name, mockedLabelBtName)
        mockField(R.id.label_remaining_range, mockedLabelRemainingRange)
        mockField(R.id.label_total_range, mockedLabelTotalRange)
        mockField(R.id.view_battery, mockedTextBattery)
        mockField(R.id.view_bt_addr, mockedTextBtAddr)
        mockField(R.id.view_bt_name, mockedTextBtName)
        mockField(R.id.view_mileage, mockedTextMileage)
        mockField(R.id.view_name, mockedTextName)
        mockField(R.id.view_remaining_range, mockedTextRemainingRange)
        mockField(R.id.view_total_range, mockedTextTotalRange)
    }

    private fun mockInitialDisplay() {
        lenient().doNothing().`when`(fragment).initialDisplayBluetoothSettings()
        lenient().doNothing().`when`(fragment).initialDisplaySoldWheel()
        lenient().doNothing().`when`(fragment).initialDisplayWheel()
    }

    private fun mockRefreshDisplay(voltage: Float, km: Float) {
        doReturn(KM).`when`(fragment).readKm()
        doReturn(VOLTAGE).`when`(fragment).readVoltageActual()

        doNothing().`when`(fragment).refreshDisplay(VOLTAGE, KM)
    }
}
