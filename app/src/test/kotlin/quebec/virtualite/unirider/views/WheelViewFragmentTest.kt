package quebec.virtualite.unirider.views

import android.view.View.GONE
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.doNothing
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verifyNoInteractions
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.commons.android.utils.ArrayListUtils.setList
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.TestDomain.CHARGER_OFFSET
import quebec.virtualite.unirider.TestDomain.CHARGE_RATE
import quebec.virtualite.unirider.TestDomain.DEVICE_ADDR
import quebec.virtualite.unirider.TestDomain.DEVICE_NAME
import quebec.virtualite.unirider.TestDomain.ID
import quebec.virtualite.unirider.TestDomain.ITEM_SOLD
import quebec.virtualite.unirider.TestDomain.KM
import quebec.virtualite.unirider.TestDomain.KM_NEW
import quebec.virtualite.unirider.TestDomain.KM_NEW_RAW
import quebec.virtualite.unirider.TestDomain.KM_STRING
import quebec.virtualite.unirider.TestDomain.MILEAGE
import quebec.virtualite.unirider.TestDomain.MILEAGE3
import quebec.virtualite.unirider.TestDomain.MILEAGE_NEW
import quebec.virtualite.unirider.TestDomain.MILEAGE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.NAME
import quebec.virtualite.unirider.TestDomain.NAME3
import quebec.virtualite.unirider.TestDomain.NOT_SOLD
import quebec.virtualite.unirider.TestDomain.PERCENTAGE
import quebec.virtualite.unirider.TestDomain.PREMILEAGE
import quebec.virtualite.unirider.TestDomain.PREMILEAGE3
import quebec.virtualite.unirider.TestDomain.REMAINING_RANGE
import quebec.virtualite.unirider.TestDomain.S18_1
import quebec.virtualite.unirider.TestDomain.S18_DISCONNECTED
import quebec.virtualite.unirider.TestDomain.SHERMAN_MAX_3
import quebec.virtualite.unirider.TestDomain.TEMPERATURE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.TOTAL_RANGE
import quebec.virtualite.unirider.TestDomain.VOLTAGE
import quebec.virtualite.unirider.TestDomain.VOLTAGE_FULL
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MAX
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MIN
import quebec.virtualite.unirider.TestDomain.VOLTAGE_NEW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_RESERVE
import quebec.virtualite.unirider.TestDomain.VOLTAGE_START
import quebec.virtualite.unirider.TestDomain.VOLTAGE_STRING
import quebec.virtualite.unirider.TestDomain.WH
import quebec.virtualite.unirider.TestDomain.WHS_PER_KM
import quebec.virtualite.unirider.TestDomain.WH_PER_KM
import quebec.virtualite.unirider.TestDomain.WH_PER_KM_INDEX
import quebec.virtualite.unirider.TestDomain.WH_PER_KM_UP
import quebec.virtualite.unirider.TestDomain.WH_PER_KM_UP_INDEX
import quebec.virtualite.unirider.bluetooth.WheelInfo
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.services.CalculatorService.EstimatedValues
import quebec.virtualite.unirider.views.BaseFragment.Companion.PARAMETER_RATES
import quebec.virtualite.unirider.views.BaseFragment.Companion.PARAMETER_SELECTED_RATE
import quebec.virtualite.unirider.views.BaseFragment.Companion.PARAMETER_VOLTAGE
import quebec.virtualite.unirider.views.BaseFragment.Companion.PARAMETER_WHEEL_ID

@RunWith(MockitoJUnitRunner::class)
class WheelViewFragmentTest : BaseFragmentTest(WheelViewFragment::class.java) {

    private val INVALID_KM = null
    private val INVALID_VOLTAGE_ACTUAL = null
    private val INVALID_VOLTAGE_START = null

    private val INITIAL_WHEEL = S18_1

    @InjectMocks
    @Spy
    lateinit var fragment: WheelViewFragment

    @Mock
    lateinit var mockedButtonCharge: Button

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
    lateinit var mockedLabelBattery: TextView

    @Mock
    lateinit var mockedLabelRate: TextView

    @Mock
    lateinit var mockedLabelRemainingRange: TextView

    @Mock
    lateinit var mockedLabelTotalRange: TextView

    @Mock
    lateinit var mockedSpinnerRate: Spinner

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
        fragment.wheel = INITIAL_WHEEL

        mockExternal()
        mockFields()
        mockFragments()
        mockStrings()
    }

    @Test
    fun clearDisplay() {
        // Given
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
            mockedSpinnerRate, mockedLabelRate,
            mockedTextRemainingRange, mockedLabelRemainingRange,
            mockedTextTotalRange, mockedLabelTotalRange
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
        // Given
        mockArgument(fragment, PARAMETER_WHEEL_ID, ID)

        // When
        fragment.onCreateView(mockedInflater, mockedContainer, SAVED_INSTANCE_STATE)

        // Then
        verifyInflate(R.layout.wheel_view_fragment)

        assertThat(fragment.parmWheelId, equalTo(ID))
    }

    @Test
    fun onViewCreated() {
        // Given
        fragment.wheel = null
        given(mockedDb.getWheel(anyLong())).willReturn(INITIAL_WHEEL)

        doNothing().`when`(fragment).clearDisplay()

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedDb).getWheel(ID)

        assertThat(fragment.wheel, equalTo(INITIAL_WHEEL))

        verifyFieldAssignment(R.id.button_charge, fragment.buttonCharge, mockedButtonCharge)
        verifyFieldAssignment(R.id.button_connect_view, fragment.buttonConnect, mockedButtonConnect)
        verifyFieldAssignment(R.id.button_edit, fragment.buttonEdit, mockedButtonEdit)
        verifyFieldAssignment(R.id.edit_km, fragment.editKm, mockedEditKm)
        verifyFieldAssignment(R.id.edit_voltage_actual, fragment.editVoltageActual, mockedEditVoltageActual)
        verifyFieldAssignment(R.id.edit_voltage_start, fragment.editVoltageStart, mockedEditVoltageStart)
        verifyFieldAssignment(R.id.label_battery, fragment.labelBattery, mockedLabelBattery)
        verifyFieldAssignment(R.id.label_rate, fragment.labelRate, mockedLabelRate)
        verifyFieldAssignment(R.id.label_remaining_range, fragment.labelRemainingRange, mockedLabelRemainingRange)
        verifyFieldAssignment(R.id.label_total_range, fragment.labelTotalRange, mockedLabelTotalRange)
        verifyFieldAssignment(R.id.spinner_rate, fragment.spinnerRate, mockedSpinnerRate)
        verifyFieldAssignment(R.id.view_battery, fragment.textBattery, mockedTextBattery)
        verifyFieldAssignment(R.id.view_bt_name, fragment.textBtName, mockedTextBtName)
        verifyFieldAssignment(R.id.view_mileage, fragment.textMileage, mockedTextMileage)
        verifyFieldAssignment(R.id.view_name, fragment.textName, mockedTextName)
        verifyFieldAssignment(R.id.view_remaining_range, fragment.textRemainingRange, mockedTextRemainingRange)
        verifyFieldAssignment(R.id.view_total_range, fragment.textTotalRange, mockedTextTotalRange)

        assertThat(fragment.spinnerRate, equalTo(mockedSpinnerRate))
        assertThat(fragment.textBtName, equalTo(mockedTextBtName))
        assertThat(fragment.textMileage, equalTo(mockedTextMileage))
        assertThat(fragment.textName, equalTo(mockedTextName))
        assertThat(fragment.textRemainingRange, equalTo(mockedTextRemainingRange))
        assertThat(fragment.textTotalRange, equalTo(mockedTextTotalRange))

        verifyOnUpdateText(mockedEditVoltageStart, "onUpdateVoltageStart")
        verifyOnUpdateText(mockedEditKm, "onUpdateKm")
        verifyOnUpdateText(mockedEditVoltageActual, "onUpdateVoltageActual")
        verifyOnClick(mockedButtonCharge, "onCharge")
        verifyOnClick(mockedButtonConnect, "onConnect")
        verifyOnClick(mockedButtonEdit, "onEdit")
        verifyOnItemSelected(mockedSpinnerRate, "onChangeRate")
        verifyStringListAdapter(mockedSpinnerRate, emptyList())

        assertThat(fragment.listOfRates, equalTo(emptyList()))
        verify(mockedSpinnerRate, never()).setSelection(anyInt())

        verify(mockedTextName).text = NAME
        verify(mockedTextBtName).text = DEVICE_NAME
        verify(mockedEditVoltageStart).setText("${INITIAL_WHEEL.voltageStart}")
        verify(mockedTextMileage).text = "${PREMILEAGE + MILEAGE}"

        verify(fragment).clearDisplay()
    }

    @Test
    fun onViewCreated_whenWheelIsntFound() {
        // Given
        given(mockedDb.getWheel(anyLong())).willReturn(null)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verifyNoInteractions(mockedWidgets)
        verify(mockedTextName, never()).text = anyString()
        verify(mockedTextBtName, never()).text = anyString()
        verify(mockedTextMileage, never()).text = anyString()
    }

    @Test
    fun onViewCreated_whenWheelIsSold() {
        // Given
        fragment.wheel = null
        given(mockedDb.getWheel(anyLong())).willReturn(SHERMAN_MAX_3)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedFragments).string(R.string.label_wheel_sold)

        verify(mockedTextName).text = "$NAME3 ($ITEM_SOLD)"
        verify(mockedTextMileage).text = "${PREMILEAGE3 + MILEAGE3}"

        verify(mockedTextBtName, never()).text = anyString()
        verify(mockedEditVoltageStart, never()).setText(anyString())

        verify(mockedButtonCharge).visibility = GONE
        verify(mockedButtonConnect).visibility = GONE
    }

    @Test
    fun onChangeRate() {
        // Given
        injectMocks()

        fragment.selectedRate = -999
        setList(fragment.listOfRates, WHS_PER_KM)

        doReturn(KM).`when`(fragment).readKm()
        doReturn(VOLTAGE).`when`(fragment).readVoltageActual()
        doReturn(VOLTAGE_START).`when`(fragment).readVoltageStart()
        doNothing().`when`(fragment).refreshDisplay(VOLTAGE_START, VOLTAGE, KM)

        // When
        fragment.onChangeRate().invoke(mockedView, WH_PER_KM_UP_INDEX, WH_PER_KM_UP.toString())

        // Then
        verify(fragment).readVoltageActual()
        verify(fragment).readVoltageStart()
        verify(fragment).readKm()
        verify(fragment).refreshDisplay(VOLTAGE_START, VOLTAGE, KM)

        assertThat(fragment.selectedRate, equalTo(WH_PER_KM_UP_INDEX))
    }

    @Test
    fun onCharge() {
        // Given
        setList(fragment.listOfRates, WHS_PER_KM)
        fragment.selectedRate = WH_PER_KM_INDEX

        doReturn(VOLTAGE).`when`(fragment).readVoltageActual()

        // When
        fragment.onCharge().invoke(mockedView)

        // Then
        verify(fragment).readVoltageActual()
        verify(mockedFragments).navigateTo(
            R.id.action_WheelViewFragment_to_WheelChargeFragment,
            Pair(PARAMETER_RATES, WHS_PER_KM),
            Pair(PARAMETER_SELECTED_RATE, WH_PER_KM_INDEX),
            Pair(PARAMETER_WHEEL_ID, ID),
            Pair(PARAMETER_VOLTAGE, VOLTAGE)
        )
    }

    @Test
    fun onConnect_whenFirstTime_gotoScanFragment() {
        // Given
        injectMocks()

        fragment.wheel = S18_DISCONNECTED

        // When
        fragment.onConnect().invoke(mockedView)

        // Then
        verify(mockedFragments).navigateTo(
            R.id.action_WheelViewFragment_to_WheelScanFragment, Pair(PARAMETER_WHEEL_ID, ID)
        )
    }

    @Test
    fun onConnect_whenNotFirstTime_connectAndUpdate() {
        // Given
        injectMocks()

        val connectionPayload = WheelInfo(KM_NEW_RAW, MILEAGE_NEW_RAW, TEMPERATURE_NEW_RAW, VOLTAGE_NEW_RAW)

        // When
        fragment.onConnect().invoke(mockedView)

        // Then
        verifyRunWithWaitDialog()
        verifyConnectorGetDeviceInfo(DEVICE_ADDR, connectionPayload)
        verifyDoneWaiting(connectionPayload)

        verify(mockedDb).saveWheel(
            WheelEntity(
                ID, NAME, DEVICE_NAME, DEVICE_ADDR,
                PREMILEAGE, MILEAGE_NEW, WH,
                VOLTAGE_MAX, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_START,
                CHARGE_RATE, VOLTAGE_FULL, CHARGER_OFFSET,
                NOT_SOLD
            )
        )
        verify(mockedEditKm).setText("$KM_NEW")
        verify(mockedTextMileage).text = "${PREMILEAGE + MILEAGE_NEW}"
        verify(mockedEditVoltageActual).setText("$VOLTAGE_NEW")
    }

    @Test
    fun onConnect_whenNotFirstTimeAndKmIsZero_setStartingVoltageToActual() {
        // Given
        injectMocks()

        // When
        fragment.onConnect().invoke(mockedView)

        // Then
        val connectionPayload = WheelInfo(0f, MILEAGE_NEW_RAW, TEMPERATURE_NEW_RAW, VOLTAGE_NEW_RAW)

        verifyRunWithWaitDialog()
        verifyConnectorGetDeviceInfo(DEVICE_ADDR, connectionPayload)
        verifyDoneWaiting(connectionPayload)

        verify(mockedDb).saveWheel(
            WheelEntity(
                ID, NAME, DEVICE_NAME, DEVICE_ADDR,
                PREMILEAGE, MILEAGE_NEW, WH,
                VOLTAGE_MAX, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_NEW,
                CHARGE_RATE, VOLTAGE_FULL, CHARGER_OFFSET,
                NOT_SOLD
            )
        )
        verify(mockedEditKm).setText("0.0")
        verify(mockedTextMileage).text = "${PREMILEAGE + MILEAGE_NEW}"
        verify(mockedEditVoltageActual).setText("$VOLTAGE_NEW")
        verify(mockedEditVoltageStart).setText("$VOLTAGE_NEW")
    }

    @Test
    fun onEdit() {
        // Given
        fragment.selectedRate = WH_PER_KM_UP_INDEX

        // When
        fragment.onEdit().invoke(mockedView)

        // Then
        assertThat(fragment.selectedRate, equalTo(-1))
        verify(mockedFragments).navigateTo(
            R.id.action_WheelViewFragment_to_WheelEditFragment, Pair(PARAMETER_WHEEL_ID, ID)
        )
    }

    @Test
    fun onUpdateKm() {
        // Given
        val value = "$KM "
        doReturn(KM).`when`(fragment).parseKm(value)
        doReturn(VOLTAGE_START).`when`(fragment).readVoltageStart()
        doReturn(VOLTAGE).`when`(fragment).readVoltageActual()

        doNothing().`when`(fragment).refreshDisplay(VOLTAGE_START, VOLTAGE, KM)
        doNothing().`when`(fragment).refreshRates()

        // When
        fragment.onUpdateKm().invoke(value)

        // Then
        verify(fragment).parseKm(value)
        verify(fragment).readVoltageStart()
        verify(fragment).readVoltageActual()

        verify(fragment).refreshDisplay(VOLTAGE_START, VOLTAGE, KM)
        verify(fragment).refreshRates()
    }

    @Test
    fun onUpdateVoltageActual() {
        // Given
        val value = "$VOLTAGE "
        doReturn(VOLTAGE).`when`(fragment).parseVoltage(value)
        doReturn(KM).`when`(fragment).readKm()
        doReturn(VOLTAGE_START).`when`(fragment).readVoltageStart()
        doNothing().`when`(fragment).refreshRates()

        doNothing().`when`(fragment).refreshDisplay(VOLTAGE_START, VOLTAGE, KM)

        // When
        fragment.onUpdateVoltageActual().invoke(value)

        // Then
        verify(fragment).parseVoltage(value)
        verify(fragment).readVoltageStart()
        verify(fragment).readKm()

        verify(fragment).refreshDisplay(VOLTAGE_START, VOLTAGE, KM)
        verify(fragment).refreshRates()
    }

    @Test
    fun onUpdateVoltageStart() {
        // Given
        injectMocks()

        val value = "$VOLTAGE_START "
        doReturn(VOLTAGE_START).`when`(fragment).parseVoltage(value)
        doReturn(VOLTAGE).`when`(fragment).readVoltageActual()
        doReturn(KM).`when`(fragment).readKm()

        doNothing().`when`(fragment).refreshDisplay(VOLTAGE_START, VOLTAGE, KM)
        doNothing().`when`(fragment).refreshRates()

        // When
        fragment.onUpdateVoltageStart().invoke(value)

        // Then
        verify(fragment).parseVoltage(value)
        verify(fragment).readVoltageActual()
        verify(fragment).readKm()

        verify(fragment).refreshDisplay(VOLTAGE_START, VOLTAGE, KM)
        verify(fragment).refreshRates()

        verify(mockedDb).saveWheel(fragment.wheel!!.copy(voltageStart = VOLTAGE_START))
        assertThat(fragment.wheel!!.voltageStart, equalTo(VOLTAGE_START))
    }

    @Test
    fun onUpdateVoltageStart_whenInvalid() {
        // Given
        injectMocks()

        val value = "ab "
        doReturn(INVALID_VOLTAGE_START).`when`(fragment).parseVoltage(value)
        doReturn(VOLTAGE).`when`(fragment).readVoltageActual()
        doReturn(KM).`when`(fragment).readKm()

        doNothing().`when`(fragment).refreshDisplay(INVALID_VOLTAGE_START, VOLTAGE, KM)

        // When
        fragment.onUpdateVoltageStart().invoke(value)

        // Then
        verify(fragment).parseVoltage(value)
        verify(fragment).readVoltageActual()
        verify(fragment).readKm()

        verify(fragment).refreshDisplay(INVALID_VOLTAGE_START, VOLTAGE, KM)

        verify(mockedDb, never()).saveWheel(any())
        assertThat(fragment.wheel!!.voltageStart, equalTo(INITIAL_WHEEL.voltageStart))
    }

    @Test
    fun onUpdateVoltageStart_whenLowerThanMinimum() {
        // Given
        injectMocks()

        val value = "lower than min"
        val lowerThanMin = fragment.wheel!!.voltageMin - 0.1f
        doReturn(lowerThanMin).`when`(fragment).parseVoltage(value)
        doReturn(VOLTAGE).`when`(fragment).readVoltageActual()
        doReturn(KM).`when`(fragment).readKm()

        doNothing().`when`(fragment).refreshDisplay(INVALID_VOLTAGE_START, VOLTAGE, KM)

        // When
        fragment.onUpdateVoltageStart().invoke(value)

        // Then
        verify(fragment).parseVoltage(value)
        verify(fragment).readVoltageActual()
        verify(fragment).readKm()

        verify(fragment).refreshDisplay(INVALID_VOLTAGE_START, VOLTAGE, KM)

        verify(mockedDb, never()).saveWheel(any())

        assertThat(fragment.wheel!!.voltageStart, equalTo(INITIAL_WHEEL.voltageStart))
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
    fun readKm() {
        // Given
        injectMocks()

        doReturn(KM_STRING).`when`(mockedWidgets).getText(mockedEditKm)
        doReturn(KM).`when`(fragment).parseKm(KM_STRING)

        // When
        val result = fragment.readKm()

        // Then
        verify(mockedWidgets).getText(mockedEditKm)
        verify(fragment).parseKm(KM_STRING)

        assertThat(result, equalTo(KM))
    }

    @Test
    fun readVoltageActual() {
        // Given
        injectMocks()

        doReturn(VOLTAGE_STRING).`when`(mockedWidgets).getText(mockedEditVoltageActual)
        doReturn(VOLTAGE).`when`(fragment).parseVoltage(VOLTAGE_STRING)

        // When
        val result = fragment.readVoltageActual()

        // Then
        verify(mockedWidgets).getText(mockedEditVoltageActual)
        verify(fragment).parseVoltage(VOLTAGE_STRING)

        assertThat(result, equalTo(VOLTAGE))
    }

    @Test
    fun readVoltageStart() {
        // Given
        injectMocks()

        doReturn(VOLTAGE_STRING).`when`(mockedWidgets).getText(mockedEditVoltageStart)
        doReturn(VOLTAGE).`when`(fragment).parseVoltage(VOLTAGE_STRING)

        // When
        val result = fragment.readVoltageStart()

        // Then
        verify(mockedWidgets).getText(mockedEditVoltageStart)
        verify(fragment).parseVoltage(VOLTAGE_STRING)

        assertThat(result, equalTo(VOLTAGE))
    }

    @Test
    fun refreshDisplay() {
        // Given
        injectMocks()

        doReturn(PERCENTAGE).`when`(mockedCalculatorService).percentage(fragment.wheel, VOLTAGE)
        doNothing().`when`(fragment).refreshEstimates(VOLTAGE, KM)

        // When
        fragment.refreshDisplay(VOLTAGE_START, VOLTAGE, KM)

        // Then
        verify(fragment).updatePercentageFor(VOLTAGE)
        verify(fragment).refreshEstimates(VOLTAGE, KM)
    }

    @Test
    fun refreshDisplay_whenInvalidKm() {
        // Given
        injectMocks()

        doNothing().`when`(fragment).updatePercentageFor(VOLTAGE)
        doNothing().`when`(fragment).clearEstimates()

        // When
        fragment.refreshDisplay(VOLTAGE_START, VOLTAGE, INVALID_KM)

        // Then
        verify(fragment).updatePercentageFor(VOLTAGE)
        verify(fragment).clearEstimates()
    }

    @Test
    fun refreshDisplay_whenInvalidVoltageActual() {
        // Given
        injectMocks()

        // When
        fragment.refreshDisplay(VOLTAGE_START, INVALID_VOLTAGE_ACTUAL, KM)

        // Then
        verify(fragment).clearPercentage()
        verify(fragment).clearEstimates()
    }

    @Test
    fun refreshDisplay_whenInvalidVoltageStart() {
        // Given
        injectMocks()

        doNothing().`when`(fragment).updatePercentageFor(VOLTAGE)
        doNothing().`when`(fragment).clearEstimates()

        // When
        fragment.refreshDisplay(INVALID_VOLTAGE_START, VOLTAGE, KM)

        // Then
        verify(fragment).updatePercentageFor(VOLTAGE)
        verify(fragment).clearEstimates()
    }

    @Test
    fun refreshEstimates() {
        // Given
        injectMocks()

        fragment.selectedRate = WH_PER_KM_INDEX
        fragment.listOfRates.addAll(WHS_PER_KM)

        val estimates = EstimatedValues(REMAINING_RANGE, TOTAL_RANGE, WH_PER_KM)
        doReturn(estimates).`when`(mockedCalculatorService).estimatedValues(fragment.wheel, VOLTAGE, KM, WH_PER_KM)

        // When
        fragment.refreshEstimates(VOLTAGE, KM)

        // Then
        verify(mockedCalculatorService).estimatedValues(fragment.wheel, VOLTAGE, KM, WH_PER_KM)

        verify(mockedTextRemainingRange).text = "$REMAINING_RANGE"
        verify(mockedTextTotalRange).text = "$TOTAL_RANGE"

        verify(mockedWidgets).show(
            mockedSpinnerRate, mockedLabelRate,
            mockedTextRemainingRange, mockedLabelRemainingRange,
            mockedTextTotalRange, mockedLabelTotalRange
        )
        verify(mockedWidgets).enable(mockedButtonCharge)

        assertThat(fragment.estimates, equalTo(estimates))
    }

    @Test
    fun refreshPercentageFor() {
        // Given
        injectMocks()

        doReturn(PERCENTAGE).`when`(mockedCalculatorService).percentage(fragment.wheel, VOLTAGE)

        // When
        fragment.updatePercentageFor(VOLTAGE)

        // Then
        verify(mockedCalculatorService).percentage(fragment.wheel, VOLTAGE)

        verify(mockedTextBattery).text = "$PERCENTAGE"
        verify(mockedWidgets).show(mockedTextBattery, mockedLabelBattery)
    }

    @Test
    fun refreshRates() {
        // Given
        fragment.estimates = EstimatedValues(REMAINING_RANGE, TOTAL_RANGE, WH_PER_KM)

        // When
        fragment.refreshRates()

        // Then
        assertThat(fragment.listOfRates, equalTo(WHS_PER_KM))
        assertThat(fragment.selectedRate, equalTo(WH_PER_KM_INDEX))

        verify(mockedWidgets).setSelection(mockedSpinnerRate, fragment.selectedRate)
    }

    private fun injectMocks() {
        fragment.buttonCharge = mockedButtonCharge
        fragment.buttonConnect = mockedButtonConnect
        fragment.editKm = mockedEditKm
        fragment.editVoltageActual = mockedEditVoltageActual
        fragment.editVoltageStart = mockedEditVoltageStart
        fragment.labelBattery = mockedLabelBattery
        fragment.labelRate = mockedLabelRate
        fragment.labelRemainingRange = mockedLabelRemainingRange
        fragment.labelTotalRange = mockedLabelTotalRange
        fragment.spinnerRate = mockedSpinnerRate
        fragment.textBattery = mockedTextBattery
        fragment.textBtName = mockedTextBtName
        fragment.textMileage = mockedTextMileage
        fragment.textRemainingRange = mockedTextRemainingRange
        fragment.textTotalRange = mockedTextTotalRange

        fragment.wheel = INITIAL_WHEEL
    }

    private fun mockFields() {
        mockField(R.id.button_charge, mockedButtonCharge)
        mockField(R.id.button_connect_view, mockedButtonConnect)
        mockField(R.id.button_edit, mockedButtonEdit)
        mockField(R.id.edit_km, mockedEditKm)
        mockField(R.id.edit_voltage_actual, mockedEditVoltageActual)
        mockField(R.id.edit_voltage_start, mockedEditVoltageStart)
        mockField(R.id.label_battery, mockedLabelBattery)
        mockField(R.id.label_rate, mockedLabelRate)
        mockField(R.id.label_remaining_range, mockedLabelRemainingRange)
        mockField(R.id.label_total_range, mockedLabelTotalRange)
        mockField(R.id.spinner_rate, mockedSpinnerRate)
        mockField(R.id.view_battery, mockedTextBattery)
        mockField(R.id.view_bt_name, mockedTextBtName)
        mockField(R.id.view_mileage, mockedTextMileage)
        mockField(R.id.view_name, mockedTextName)
        mockField(R.id.view_remaining_range, mockedTextRemainingRange)
        mockField(R.id.view_total_range, mockedTextTotalRange)
    }
}
