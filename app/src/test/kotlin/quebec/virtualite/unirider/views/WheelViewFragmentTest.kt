package quebec.virtualite.unirider.views

import android.view.View.GONE
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verifyNoInteractions
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.commons.android.utils.ArrayListUtils.setList
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.TestDomain.CHARGER_OFFSET
import quebec.virtualite.unirider.TestDomain.CHARGE_RATE
import quebec.virtualite.unirider.TestDomain.DEVICE_ADDR
import quebec.virtualite.unirider.TestDomain.DEVICE_NAME
import quebec.virtualite.unirider.TestDomain.ID
import quebec.virtualite.unirider.TestDomain.ID3
import quebec.virtualite.unirider.TestDomain.ITEM_SOLD
import quebec.virtualite.unirider.TestDomain.KM
import quebec.virtualite.unirider.TestDomain.KM_NEW
import quebec.virtualite.unirider.TestDomain.KM_NEW_RAW
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
import quebec.virtualite.unirider.TestDomain.REMAINING_RANGE_UP
import quebec.virtualite.unirider.TestDomain.REMAINING_RANGE_ZERO
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
import quebec.virtualite.unirider.TestDomain.VOLTAGE_START_NEW
import quebec.virtualite.unirider.TestDomain.WH
import quebec.virtualite.unirider.TestDomain.WHS_PER_KM
import quebec.virtualite.unirider.TestDomain.WHS_PER_KM_SMALL
import quebec.virtualite.unirider.TestDomain.WH_PER_KM
import quebec.virtualite.unirider.TestDomain.WH_PER_KM_INDEX
import quebec.virtualite.unirider.TestDomain.WH_PER_KM_SMALL
import quebec.virtualite.unirider.TestDomain.WH_PER_KM_SMALL_INDEX
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

    @InjectMocks
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
        fragment.wheel = S18_1

        mockExternal()
        mockFields()
        mockFragments()
        mockStrings()
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
        given(mockedDb.getWheel(anyLong()))
            .willReturn(S18_1)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedDb).getWheel(ID)

        assertThat(fragment.wheel, equalTo(S18_1))

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
        verify(mockedEditVoltageStart).setText("$VOLTAGE_START")
        verify(mockedTextMileage).text = "${PREMILEAGE + MILEAGE}"

        verify(mockedDb, never()).saveWheel(any())
    }

    @Test
    fun onViewCreated_whenVoltageStartAndVoltageActualAndKmAreFilled_updateCalculatedValues() {
        // Given
        fragment.wheel = null

        mockKm("$KM ")
        mockVoltageActual("$VOLTAGE ")
        mockVoltageStart("$VOLTAGE_START ")

        given(mockedDb.getWheel(anyLong()))
            .willReturn(S18_1.copy(voltageStart = VOLTAGE_MAX))

        given(mockedCalculatorService.estimatedValues(any(), anyFloat(), anyFloat(), eq(null)))
            .willReturn(EstimatedValues(REMAINING_RANGE, TOTAL_RANGE, WH_PER_KM))

        given(mockedCalculatorService.percentage(any(), anyFloat()))
            .willReturn(PERCENTAGE)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verifyUpdatePercentage()
        verifyUpdateEstimatedValues(VOLTAGE, "$REMAINING_RANGE")
        verifyUpdateRate()
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
    fun onViewCreated_whenWheelIsSold() {
        // Given
        fragment.wheel = null
        given(mockedDb.getWheel(anyLong()))
            .willReturn(SHERMAN_MAX_3)

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
        mockKm("$KM ")
        mockVoltageActual("$VOLTAGE ")
        mockVoltageStart("$VOLTAGE_START ")

        given(mockedCalculatorService.estimatedValues(any(), anyFloat(), anyFloat(), anyFloat()))
            .willReturn(EstimatedValues(REMAINING_RANGE_UP, TOTAL_RANGE, WH_PER_KM))

        setList(fragment.listOfRates, WHS_PER_KM)

        // When
        fragment.onChangeRate().invoke(mockedView, WH_PER_KM_UP_INDEX, WH_PER_KM_UP.toString())

        // Then
        assertThat(fragment.selectedRate, equalTo(WH_PER_KM_UP_INDEX))
        verifyUpdateEstimatedValues(VOLTAGE, "$REMAINING_RANGE_UP", WH_PER_KM_UP)
    }

    @Test
    fun onCharge() {
        // Given
        setList(fragment.listOfRates, WHS_PER_KM)
        fragment.selectedRate = WH_PER_KM_INDEX

        mockVoltageActual("$VOLTAGE ")

        // When
        fragment.onCharge().invoke(mockedView)

        // Then
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
                CHARGE_RATE, VOLTAGE_FULL, CHARGER_OFFSET, NOT_SOLD
            )
        )
        verify(mockedEditKm).setText("$KM_NEW")
        verify(mockedTextMileage).text = "${PREMILEAGE + MILEAGE_NEW}"
        verify(mockedEditVoltageActual).setText("$VOLTAGE_NEW")
        verifyNoInteractions(mockedEditVoltageStart)
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
                CHARGE_RATE, VOLTAGE_FULL, CHARGER_OFFSET, NOT_SOLD
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
            R.id.action_WheelViewFragment_to_WheelEditFragment,
            Pair(PARAMETER_WHEEL_ID, ID)
        )
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
        mockVoltageActual("$VOLTAGE ")
        mockVoltageStart("$VOLTAGE_START ")

        fragment.selectedRate = WH_PER_KM_UP_INDEX

        given(mockedCalculatorService.estimatedValues(any(), anyFloat(), anyFloat(), eq(null)))
            .willReturn(EstimatedValues(REMAINING_RANGE, TOTAL_RANGE, WH_PER_KM))

        // When
        fragment.onUpdateKm().invoke("$KM ")

        // Then
        assertThat(fragment.selectedRate, equalTo(WH_PER_KM_INDEX))
        verifyUpdateEstimatedValues(VOLTAGE, "$REMAINING_RANGE")
        verifyUpdateRate()
    }

    @Test
    fun onUpdateKm_withVoltageLowerThanReserve_updateEstimatedValues() {
        // Given
        val voltage = VOLTAGE_RESERVE - 0.1f

        injectMocks()
        mockVoltageActual("$voltage ")
        mockVoltageStart("$VOLTAGE_START ")

        given(mockedCalculatorService.estimatedValues(any(), anyFloat(), anyFloat(), eq(null)))
            .willReturn(EstimatedValues(0f, TOTAL_RANGE, WH_PER_KM))

        // When
        fragment.onUpdateKm().invoke("$KM ")

        // Then
        verifyUpdateEstimatedValues(voltage, "$REMAINING_RANGE_ZERO")
        verifyUpdateRate()
    }

    @Test
    fun onUpdateKm_withVoltageHigherThanMax_updateEstimatedValues() {
        // Given
        val voltageActual = VOLTAGE_MAX + 0.5f

        injectMocks()
        mockVoltageActual("$voltageActual ")
        mockVoltageStart("${voltageActual + 0.2f} ")

        given(mockedCalculatorService.estimatedValues(any(), anyFloat(), anyFloat(), eq(null)))
            .willReturn(EstimatedValues(REMAINING_RANGE, TOTAL_RANGE, WH_PER_KM))

        // When
        fragment.onUpdateKm().invoke("$KM ")

        // Then
        verifyUpdateEstimatedValues(voltageActual, "$REMAINING_RANGE")
        verifyUpdateRate()
    }

    @Test
    fun onUpdateKm_withVoltageStartHigherThanMax_updateEstimatedValues() {
        // Given
        val voltageStart = VOLTAGE_MAX + 0.5f

        injectMocks()
        mockVoltageActual("$VOLTAGE ")
        mockVoltageStart("$voltageStart ")

        given(mockedCalculatorService.estimatedValues(any(), anyFloat(), anyFloat(), eq(null)))
            .willReturn(EstimatedValues(REMAINING_RANGE, TOTAL_RANGE, WH_PER_KM))

        // When
        fragment.onUpdateKm().invoke("$KM ")

        // Then
        verifyUpdateEstimatedValues(VOLTAGE, "$REMAINING_RANGE")
        verifyUpdateRate()
    }

    @Test
    fun onUpdateKm_withoutVoltageSet_noDisplay() {
        // Given
        injectMocks()
        mockVoltageActual(" ")
        mockVoltageStart(" ")

        // When
        fragment.onUpdateKm().invoke("$KM ")

        // Then
        verifyClearEstimatedValues()
    }

    @Test
    fun onUpdateVoltageActual() {
        // Given
        injectMocks()
        mockKm("$KM ")
        mockVoltageStart("$VOLTAGE_START ")

        fragment.selectedRate = WH_PER_KM_UP_INDEX

        given(mockedCalculatorService.estimatedValues(any(), anyFloat(), anyFloat(), eq(null)))
            .willReturn(EstimatedValues(REMAINING_RANGE, TOTAL_RANGE, WH_PER_KM))

        given(mockedCalculatorService.percentage(any(), anyFloat()))
            .willReturn(PERCENTAGE)

        // When
        fragment.onUpdateVoltageActual().invoke("$VOLTAGE ")

        // Then
        assertThat(fragment.selectedRate, equalTo(WH_PER_KM_INDEX))
        verifyUpdatePercentage()
        verifyUpdateEstimatedValues(VOLTAGE, "$REMAINING_RANGE")
        verifyUpdateRate()
    }

    @Test
    fun onUpdateVoltageActual_withSmallRate() {
        // Given
        injectMocks()
        mockKm("$KM ")
        mockVoltageStart("$VOLTAGE_START ")

        fragment.selectedRate = WH_PER_KM_INDEX

        given(mockedCalculatorService.estimatedValues(any(), anyFloat(), anyFloat(), eq(null)))
            .willReturn(EstimatedValues(REMAINING_RANGE, TOTAL_RANGE, WH_PER_KM_SMALL))

        // When
        fragment.onUpdateVoltageActual().invoke("$VOLTAGE ")

        // Then
        assertThat(fragment.listOfRates, equalTo(WHS_PER_KM_SMALL))
        assertThat(fragment.selectedRate, equalTo(WH_PER_KM_SMALL_INDEX))
    }

    @Test
    fun onUpdateVoltageActual_withDifferentVoltageStart() {
        // Given
        injectMocks()
        mockKm("$KM ")
        mockVoltageStart("$VOLTAGE_START ")

        given(mockedCalculatorService.estimatedValues(any(), anyFloat(), anyFloat(), eq(null)))
            .willReturn(EstimatedValues(REMAINING_RANGE, TOTAL_RANGE, WH_PER_KM))

        given(mockedCalculatorService.percentage(any(), anyFloat()))
            .willReturn(PERCENTAGE)

        // When
        fragment.onUpdateVoltageActual().invoke("$VOLTAGE ")

        // Then
        verifyUpdatePercentage()
        verifyUpdateEstimatedValues(VOLTAGE, "$REMAINING_RANGE")
        verifyUpdateRate()
    }

    @Test
    fun onUpdateVoltageActual_whenBlank_noDisplay() {
        // Given
        injectMocks()
        mockKm("$KM ")
        mockVoltageStart("$VOLTAGE_START ")

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
    fun onUpdateVoltageActual_whenVoltageActualHigherThanStart_noDisplay() {
        // Given
        fragment.parmWheelId = ID3
        fragment.wheel = SHERMAN_MAX_3

        injectMocks()
        mockKm("$KM")
        mockVoltageStart("99.5")

        // When
        fragment.onUpdateVoltageActual().invoke("100.2")

        // Then
        verifyClearEstimatedValues()
    }

    @Test
    fun onUpdateVoltageActual_whenWhPerKmLessThan5_noDisplay() {
        // Given
        injectMocks()
        mockKm("$KM")
        mockVoltageStart("83.4")

        given(mockedCalculatorService.estimatedValues(any(), anyFloat(), anyFloat(), eq(null)))
            .willReturn(EstimatedValues(REMAINING_RANGE, TOTAL_RANGE, 4.9f))

        // When
        fragment.onUpdateVoltageActual().invoke("83.3")

        // Then
        verifyEstimateButClearEstimatedValues()
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

    @Ignore
    @Test
    fun onUpdateVoltageActual_withNoKm_updateBatteryPercentageOnly() {
        // Given
        injectMocks()
        mockKm(" ")
        mockVoltageStart("$VOLTAGE_START ")

        given(mockedCalculatorService.percentage(any(), anyFloat()))
            .willReturn(PERCENTAGE)

        // When
        fragment.onUpdateVoltageActual().invoke("$VOLTAGE ")

        // Then
        verifyUpdatePercentage()
        verifyClearEstimatedValues()
        verifyUpdateRate()
    }

    @Test
    fun onUpdateVoltageStart() {
        // Given
        injectMocks()
        mockKm("$KM ")
        mockVoltageActual("$VOLTAGE ")

        given(mockedCalculatorService.estimatedValues(any(), anyFloat(), anyFloat(), eq(null)))
            .willReturn(EstimatedValues(REMAINING_RANGE, TOTAL_RANGE, WH_PER_KM))

        // When
        fragment.onUpdateVoltageStart().invoke("$VOLTAGE_START_NEW ")

        // Then
        verifyUpdateEstimatedValues(VOLTAGE, "$REMAINING_RANGE")
        verifyUpdateRate()

        verify(mockedDb).saveWheel(S18_1.copy(voltageStart = VOLTAGE_START_NEW))
    }

    @Test
    fun onUpdateVoltageStart_whenBlank_noDisplay() {
        // Given
        injectMocks()
        mockKm("$KM ")
        mockVoltageActual("$VOLTAGE ")

        // When
        fragment.onUpdateVoltageStart().invoke(" ")

        // Then
        verifyClearEstimatedValues()

        verify(mockedDb, never()).saveWheel(any())
    }

    @Test
    fun onUpdateVoltageStart_whenHigherThanMax_updateEstimatedValues() {
        // Given
        val voltageStart = VOLTAGE_MAX + 0.5f

        injectMocks()
        mockKm("$KM ")
        mockVoltageActual("$VOLTAGE ")

        given(mockedCalculatorService.estimatedValues(any(), anyFloat(), anyFloat(), eq(null)))
            .willReturn(EstimatedValues(REMAINING_RANGE, TOTAL_RANGE, WH_PER_KM))

        // When
        fragment.onUpdateVoltageStart().invoke("$voltageStart ")

        // Then
        verifyUpdateEstimatedValues(VOLTAGE, "$REMAINING_RANGE")
        verifyUpdateRate()

        verify(mockedDb).saveWheel(S18_1.copy(voltageStart = voltageStart))
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

    private fun mockKm(km: String) {
        fragment.editKm = mockedEditKm

        given(mockedWidgets.getText(mockedEditKm))
            .willReturn(km.trim())
    }

    private fun mockVoltageActual(voltage: String) {
        fragment.editVoltageActual = mockedEditVoltageActual

        given(mockedWidgets.getText(mockedEditVoltageActual))
            .willReturn(voltage.trim())
    }

    private fun mockVoltageStart(voltage: String) {
        fragment.editVoltageStart = mockedEditVoltageStart

        given(mockedWidgets.getText(mockedEditVoltageStart))
            .willReturn(voltage.trim())
    }

    private fun verifyClearEstimatedValues() {
        verify(mockedCalculatorService, never()).estimatedValues(any(), anyFloat(), anyFloat(), anyFloat())
        verifyEstimateButClearEstimatedValues()
    }

    private fun verifyClearPercentage() {
        verify(mockedCalculatorService, never()).percentage(any(), anyFloat())
        verify(mockedTextBattery).text = ""
        verify(mockedWidgets).show(mockedLabelBattery, false)
    }

    private fun verifyEstimateButClearEstimatedValues() {
        assertThat(fragment.listOfRates, equalTo(emptyList()))
        verify(mockedSpinnerRate, never()).setSelection(anyInt())
        verify(mockedWidgets).clearSelection(mockedSpinnerRate)

        verify(mockedTextRemainingRange).text = ""
        verify(mockedTextTotalRange).text = ""

        verify(mockedWidgets).enable(mockedButtonCharge, false)
        verify(mockedWidgets).show(mockedLabelRate, false)
        verify(mockedWidgets).show(mockedLabelRemainingRange, false)
        verify(mockedWidgets).show(mockedLabelTotalRange, false)
        verify(mockedWidgets).show(mockedSpinnerRate, false)

        assertThat(fragment.estimates, equalTo(null))
    }

    private fun verifyUpdateEstimatedValues(voltage: Float, remainingRange: String) {
        verifyUpdateEstimatedValues(voltage, remainingRange, null)
    }

    private fun verifyUpdateEstimatedValues(voltage: Float, remainingRange: String, whPerKmOverride: Float?) {
        verify(mockedCalculatorService).estimatedValues(fragment.wheel, voltage, KM, whPerKmOverride)

        verify(mockedTextRemainingRange).text = remainingRange
        verify(mockedTextTotalRange).text = "$TOTAL_RANGE"

        verify(mockedWidgets).enable(mockedButtonCharge, true)
        verify(mockedWidgets).show(mockedLabelRate, true)
        verify(mockedWidgets).show(mockedLabelRemainingRange, true)
        verify(mockedWidgets).show(mockedLabelTotalRange, true)
        verify(mockedWidgets).show(mockedSpinnerRate, true)

        assertThat(fragment.estimates, not(equalTo(null)))
    }

    private fun verifyUpdatePercentage() {
        verify(mockedCalculatorService).percentage(fragment.wheel, VOLTAGE)
        verify(mockedTextBattery).text = "$PERCENTAGE"
        verify(mockedWidgets).show(mockedLabelBattery, true)
    }

    private fun verifyUpdateRate() {
        assertThat(fragment.listOfRates, equalTo(WHS_PER_KM))
        verify(mockedWidgets).setSelection(mockedSpinnerRate, WH_PER_KM_INDEX)
    }
}
