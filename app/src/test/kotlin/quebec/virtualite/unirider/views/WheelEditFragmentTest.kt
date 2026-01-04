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
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.test.domain.TestConstants.CHARGE_RATE
import quebec.virtualite.unirider.test.domain.TestConstants.CHARGE_RATE_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.DEVICE_ADDR
import quebec.virtualite.unirider.test.domain.TestConstants.DEVICE_NAME
import quebec.virtualite.unirider.test.domain.TestConstants.DISTANCE_OFFSET
import quebec.virtualite.unirider.test.domain.TestConstants.DISTANCE_OFFSET_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.EMPTY_WHEEL
import quebec.virtualite.unirider.test.domain.TestConstants.MILEAGE
import quebec.virtualite.unirider.test.domain.TestConstants.MILEAGE_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.NAME
import quebec.virtualite.unirider.test.domain.TestConstants.NAME_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.PREMILEAGE
import quebec.virtualite.unirider.test.domain.TestConstants.PREMILEAGE_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.S18_1
import quebec.virtualite.unirider.test.domain.TestConstants.S18_DISCONNECTED
import quebec.virtualite.unirider.test.domain.TestConstants.SOLD
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_FULL
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_FULL_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_MAX
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_MAX_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_MIN
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_MIN_NEW
import quebec.virtualite.unirider.test.domain.TestConstants.WH
import quebec.virtualite.unirider.test.domain.TestConstants.WH_NEW

@RunWith(MockitoJUnitRunner::class)
class WheelEditFragmentTest : FragmentTestBase(WheelEditFragment::class.java) {

    @InjectMocks
    @Spy
    private lateinit var fragment: WheelEditFragment

    @Mock
    private lateinit var mockedButtonDelete: Button

    @Mock
    private lateinit var mockedButtonSave: Button

    @Mock
    private lateinit var mockedEditChargeRate: EditText

    @Mock
    private lateinit var mockedEditDistanceOffset: EditText

    @Mock
    private lateinit var mockedEditMileage: EditText

    @Mock
    private lateinit var mockedEditName: EditText

    @Mock
    private lateinit var mockedEditPreMileage: EditText

    @Mock
    private lateinit var mockedEditVoltageFull: EditText

    @Mock
    private lateinit var mockedEditVoltageMax: EditText

    @Mock
    private lateinit var mockedEditVoltageMin: EditText

    @Mock
    private lateinit var mockedEditWh: EditText

    @Mock
    private lateinit var mockedSwitchSold: SwitchMaterial

    @Mock
    private lateinit var mockedTextBluetoothAddr: TextView

    @Mock
    private lateinit var mockedTextBluetoothName: TextView

    @Mock
    private lateinit var mockedTextBluetoothNameLabel: TextView

    @Mock
    private lateinit var mockedWheelValidator: WheelValidator

    @Before
    fun before() {
        BaseFragment.wheel = S18_DISCONNECTED

        mockExternal()
        mockFields()
        mockFragments()
    }

    @Test
    fun onCreateView() {
        // When
        fragment.onCreateView(mockedInflater, mockedContainer, SAVED_INSTANCE_STATE)

        // Then
        verifyInflate(R.layout.wheel_edit_fragment)
    }

    @Test
    fun onViewCreated() {
        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        assertThat(fragment.initialWheel, equalTo(BaseFragment.wheel))
        assertThat(fragment.updatedWheel, equalTo(BaseFragment.wheel))

        verifyFieldAssignment(R.id.button_delete, fragment.buttonDelete, mockedButtonDelete)
        verifyFieldAssignment(R.id.button_save, fragment.buttonSave, mockedButtonSave)
        verifyFieldAssignment(R.id.check_sold, fragment.switchSold, mockedSwitchSold)
        verifyFieldAssignment(R.id.edit_name, fragment.editChargeRate, mockedEditChargeRate)
        verifyFieldAssignment(R.id.edit_distance_offset, fragment.editDistanceOffset, mockedEditDistanceOffset)
        verifyFieldAssignment(R.id.edit_mileage, fragment.editMileage, mockedEditMileage)
        verifyFieldAssignment(R.id.edit_name, fragment.editName, mockedEditName)
        verifyFieldAssignment(R.id.edit_premileage, fragment.editPreMileage, mockedEditPreMileage)
        verifyFieldAssignment(R.id.edit_voltage_full, fragment.editVoltageFull, mockedEditVoltageFull)
        verifyFieldAssignment(R.id.edit_voltage_max, fragment.editVoltageMax, mockedEditVoltageMax)
        verifyFieldAssignment(R.id.edit_voltage_min, fragment.editVoltageMin, mockedEditVoltageMin)
        verifyFieldAssignment(R.id.edit_wh, fragment.editWh, mockedEditWh)
        verifyFieldAssignment(R.id.view_bt_addr_on_edit, fragment.textBtAddr, mockedTextBluetoothAddr)
        verifyFieldAssignment(R.id.view_bt_name_on_edit, fragment.textBtName, mockedTextBluetoothName)
        verifyFieldAssignment(R.id.view_bt_name_on_edit_label, fragment.textBtNameLabel, mockedTextBluetoothNameLabel)

        verifyOnUpdateText(mockedEditChargeRate, "onUpdateChargeRate")
        verifyOnUpdateText(mockedEditDistanceOffset, "onUpdateDistanceOffset")
        verifyOnUpdateText(mockedEditName, "onUpdateName")
        verifyOnUpdateText(mockedEditPreMileage, "onUpdatePreMileage")
        verifyOnUpdateText(mockedEditMileage, "onUpdateMileage")
        verifyOnUpdateText(mockedEditVoltageFull, "onUpdateVoltageFull")
        verifyOnUpdateText(mockedEditVoltageMax, "onUpdateVoltageMax")
        verifyOnUpdateText(mockedEditVoltageMin, "onUpdateVoltageMin")
        verifyOnUpdateText(mockedEditWh, "onUpdateWh")
        verifyOnToggleSwitch(mockedSwitchSold, "onToggleSold")
        verifyOnLongClick(mockedButtonDelete, "onDelete")
        verifyOnClick(mockedButtonSave, "onSave")

        verify(mockedEditChargeRate).setText("$CHARGE_RATE")
        verify(mockedEditDistanceOffset).setText("$DISTANCE_OFFSET")
        verify(mockedEditName).setText(NAME)
        verify(mockedEditPreMileage).setText("$PREMILEAGE")
        verify(mockedEditMileage).setText("$MILEAGE")
        verify(mockedEditVoltageFull).setText("$VOLTAGE_FULL")
        verify(mockedEditVoltageMax).setText("$VOLTAGE_MAX")
        verify(mockedEditVoltageMin).setText("$VOLTAGE_MIN")
        verify(mockedEditWh).setText("$WH")
        verify(mockedSwitchSold).isChecked = BaseFragment.wheel!!.isSold
        verify(mockedWidgets).disable(mockedButtonSave)
    }

    @Test
    fun onViewCreated_whenAdding() {
        // Given
        BaseFragment.wheel = null

        val newWheel = EMPTY_WHEEL

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedDb, never()).getWheel(anyLong())
        verify(mockedWidgets).disable(mockedButtonSave)

        verify(mockedEditName, never()).setText(anyString())
        verify(mockedEditVoltageMax, never()).setText(anyString())
        verify(mockedEditVoltageMin, never()).setText(anyString())

        assertThat(fragment.initialWheel, equalTo(newWheel))
        assertThat(fragment.updatedWheel, equalTo(newWheel))
    }

    @Test
    fun onViewCreated_withBluetoothConnectedWheel() {
        // Given
        BaseFragment.wheel = S18_1

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(fragment).displayBluetoothSettings()
    }

    @Test
    fun onViewCreated_withZeroPreMileageAndMileage_emptyFields() {
        // Given
        BaseFragment.wheel = S18_1.copy(premileage = 0, mileage = 0)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedEditPreMileage, never()).setText(anyString())
        verify(mockedEditMileage, never()).setText(anyString())
    }

    @Test
    fun displayBluetoothSettings() {
        // Given
        BaseFragment.wheel = S18_1

        injectMocks()

        // When
        fragment.displayBluetoothSettings()

        // Then
        verify(mockedTextBluetoothAddr).setText(DEVICE_ADDR)
        verify(mockedTextBluetoothName).setText(DEVICE_NAME)

        verify(mockedWidgets).show(
            mockedTextBluetoothName,
            mockedTextBluetoothNameLabel,
            mockedTextBluetoothAddr
        )
    }

    @Test
    fun enableSaveIfChanged_whenChanged_enabled() {
        // Given
        changeCanBeSaved(true)
        injectMocks()

        // When
        fragment.enableSaveIfChanged()

        // Then
        verify(mockedDb).findDuplicate(definedWheel())
        verify(mockedWidgets).enable(mockedButtonSave)
    }

    @Test
    fun enableSaveIfChanged_whenChangedAndDuplicate_disabled() {
        // Given
        changeCanBeSaved(true)
        injectMocks()

        given(mockedDb.findDuplicate(any())).willReturn(true)

        // When
        fragment.enableSaveIfChanged()

        // Then
        verify(mockedDb).findDuplicate(definedWheel())
        verify(mockedWidgets).disable(mockedButtonSave)
    }

    @Test
    fun enableSaveIfChanged_whenNotChanged_disabled() {
        // Given
        changeCanBeSaved(false)
        injectMocks()

        // When
        fragment.enableSaveIfChanged()

        // Then
        verify(mockedDb, never()).findDuplicate(any())
        verify(mockedWidgets).disable(mockedButtonSave)
    }

    @Test
    fun onDelete() {
        // When
        fragment.onDelete().invoke(mockedView)

        // Then
        verify(mockedFragments).navigateTo(R.id.action_WheelEditFragment_to_WheelDeleteConfirmationFragment)
    }

    @Test
    fun onSave() {
        // Given
        BaseFragment.wheel = definedWheel()
        fragment.updatedWheel = BaseFragment.wheel!!.copy(premileage = 2)

        // When
        fragment.onSave().invoke(mockedView)

        // Then
        verify(mockedDb).saveWheel(fragment.updatedWheel)
        verify(mockedFragments).navigateBack()

        assertThat(BaseFragment.wheel, equalTo(fragment.updatedWheel))
    }

    @Test
    fun onToggleSold() {
        // Given
        initUpdate()

        // When
        fragment.onToggleSold().invoke(SOLD)

        // Then
        verify(fragment).enableSaveIfChanged()

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(isSold = SOLD)))
    }

    @Test
    fun onUpdateChargeRate() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateChargeRate().invoke("$CHARGE_RATE_NEW ")

        // Then
        verify(fragment).enableSaveIfChanged()

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(chargeRate = CHARGE_RATE_NEW)))
    }

    @Test
    fun onUpdateChargeRate_whenEmpty_zero() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateChargeRate().invoke(" ")

        // Then
        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(chargeRate = 0f)))
    }

    @Test
    fun onUpdateChargeRate_whenInvalid_zero() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateChargeRate().invoke("ab ")

        // Then
        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(chargeRate = 0f)))
    }

    @Test
    fun onUpdateChargeRate_withTooManyDecimals() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateChargeRate().invoke("${CHARGE_RATE_NEW + 0.001f} ")

        // Then
        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(chargeRate = CHARGE_RATE_NEW)))
    }

    @Test
    fun onUpdateDistanceOffset() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateDistanceOffset().invoke("$DISTANCE_OFFSET_NEW ")

        // Then
        verify(fragment).enableSaveIfChanged()

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(distanceOffset = DISTANCE_OFFSET_NEW)))
    }

    @Test
    fun onUpdateDistanceOffset_whenEmpty_zero() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateDistanceOffset().invoke(" ")

        // Then
        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(distanceOffset = 0f)))
    }

    @Test
    fun onUpdateDistanceOffset_whenInvalid_zero() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateDistanceOffset().invoke("ab ")

        // Then
        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(distanceOffset = 0f)))
    }

    @Test
    fun onUpdateDistanceOffset_withTooManyDecimals() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateDistanceOffset().invoke("${DISTANCE_OFFSET_NEW + 0.00001f} ")

        // Then
        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(distanceOffset = DISTANCE_OFFSET_NEW)))
    }

    @Test
    fun onUpdateMileage() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateMileage().invoke("$MILEAGE_NEW ")

        // Then
        verify(fragment).enableSaveIfChanged()

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(mileage = MILEAGE_NEW)))
    }

    @Test
    fun onUpdateMileage_whenEmpty_zero() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateMileage().invoke(" ")

        // Then
        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(mileage = 0)))
    }

    @Test
    fun onUpdateMileage_whenInvalid_zero() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateMileage().invoke("ab ")

        // Then
        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(mileage = 0)))
    }

    @Test
    fun onUpdateName() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateName().invoke("$NAME_NEW ")

        // Then
        verify(fragment).enableSaveIfChanged()

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(name = NAME_NEW)))
    }

    @Test
    fun onUpdatePreMileage() {
        // Given
        initUpdate()

        // When
        fragment.onUpdatePreMileage().invoke("$PREMILEAGE_NEW ")

        // Then
        verify(fragment).enableSaveIfChanged()

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(premileage = PREMILEAGE_NEW)))
    }

    @Test
    fun onUpdatePreMileage_whenEmpty_zero() {
        // Given
        initUpdate()

        // When
        fragment.onUpdatePreMileage().invoke(" ")

        // Then
        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(premileage = 0)))
    }

    @Test
    fun onUpdatePreMileage_whenInvalid_zero() {
        // Given
        initUpdate()

        // When
        fragment.onUpdatePreMileage().invoke("ab ")

        // Then
        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(premileage = 0)))
    }

    @Test
    fun onUpdateVoltageFull() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateVoltageFull().invoke("$VOLTAGE_FULL_NEW ")

        // Then
        verify(fragment).enableSaveIfChanged()

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(voltageFull = VOLTAGE_FULL_NEW)))
    }

    @Test
    fun onUpdateVoltageFull_whenEmpty_setToMaximum() {
        // Given
        initUpdate()
        mockVoltageMax()

        // When
        fragment.onUpdateVoltageFull().invoke(" ")

        // Then
        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(voltageFull = VOLTAGE_MAX)))
    }

    @Test
    fun onUpdateVoltageFull_whenInvalid_setToMaximum() {
        // Given
        initUpdate()
        mockVoltageMax()

        // When
        fragment.onUpdateVoltageFull().invoke("ab ")

        // Then
        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(voltageFull = VOLTAGE_MAX)))
    }

    @Test
    fun onUpdateVoltageFull_withTooManyDecimals() {
        // Given
        initUpdate()
        mockVoltageMax()

        // When
        fragment.onUpdateVoltageFull().invoke("${VOLTAGE_FULL_NEW + 0.001f} ")

        // Then
        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(voltageFull = VOLTAGE_FULL_NEW)))
    }

    @Test
    fun onUpdateVoltageMax() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateVoltageMax().invoke("$VOLTAGE_MAX_NEW ")

        // Then
        verify(fragment).enableSaveIfChanged()

        assertThat(
            fragment.updatedWheel, equalTo(S18_1.copy(voltageMax = VOLTAGE_MAX_NEW))
        )
    }

    @Test
    fun onUpdateVoltageMax_whenEmpty_zero() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateVoltageMax().invoke(" ")

        // Then
        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(voltageMax = 0f)))
    }

    @Test
    fun onUpdateVoltageMax_whenInvalid_zero() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateVoltageMax().invoke("ab ")

        // Then
        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(voltageMax = 0f)))
    }

    @Test
    fun onUpdateVoltageMax_withTooManyDecimals() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateVoltageMax().invoke("${VOLTAGE_MAX_NEW + 0.001f} ")

        // Then
        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(voltageMax = VOLTAGE_MAX_NEW)))
    }

    @Test
    fun onUpdateVoltageMin() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateVoltageMin().invoke("$VOLTAGE_MIN_NEW ")

        // Then
        verify(fragment).enableSaveIfChanged()

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(voltageMin = VOLTAGE_MIN_NEW)))
    }

    @Test
    fun onUpdateVoltageMin_whenEmpty_zero() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateVoltageMin().invoke(" ")

        // Then
        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(voltageMin = 0f)))
    }

    @Test
    fun onUpdateVoltageMin_whenInvalid_zero() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateVoltageMin().invoke("ab ")

        // Then
        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(voltageMin = 0f)))
    }

    @Test
    fun onUpdateVoltageMin_withTooManyDecimals() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateVoltageMin().invoke("${VOLTAGE_MIN_NEW + 0.001f} ")

        // Then
        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(voltageMin = VOLTAGE_MIN_NEW)))
    }

    @Test
    fun onUpdateWh() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateWh().invoke("$WH_NEW ")

        // Then
        verify(fragment).enableSaveIfChanged()

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(wh = WH_NEW)))
    }

    @Test
    fun onUpdateWh_whenEmpty_zero() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateWh().invoke(" ")

        // Then
        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(wh = 0)))
    }

    @Test
    fun onUpdateWh_whenInvalid_zero() {
        // Given
        initUpdate()

        // When
        fragment.onUpdateWh().invoke("ab ")

        // Then
        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(wh = 0)))
    }

    private fun changeCanBeSaved(canSave: Boolean) {
        fragment.initialWheel = definedWheel()
        fragment.updatedWheel = fragment.initialWheel

        given(mockedWheelValidator.canSave(any(), any())).willReturn(canSave)
    }

    private fun definedWheel() = S18_1

    private fun initUpdate() {
        injectMocks()

        doNothing().`when`(fragment).enableSaveIfChanged()
        fragment.updatedWheel = definedWheel()
    }

    private fun injectMocks() {
        fragment.buttonSave = mockedButtonSave
        fragment.textBtAddr = mockedTextBluetoothAddr
        fragment.textBtName = mockedTextBluetoothName
        fragment.textBtNameLabel = mockedTextBluetoothNameLabel
    }

    private fun mockFields() {
        mockField(R.id.button_delete, mockedButtonDelete)
        mockField(R.id.button_save, mockedButtonSave)
        mockField(R.id.check_sold, mockedSwitchSold)
        mockField(R.id.edit_charge_rate, mockedEditChargeRate)
        mockField(R.id.edit_distance_offset, mockedEditDistanceOffset)
        mockField(R.id.edit_name, mockedEditName)
        mockField(R.id.edit_premileage, mockedEditPreMileage)
        mockField(R.id.edit_mileage, mockedEditMileage)
        mockField(R.id.edit_voltage_full, mockedEditVoltageFull)
        mockField(R.id.edit_voltage_max, mockedEditVoltageMax)
        mockField(R.id.edit_voltage_min, mockedEditVoltageMin)
        mockField(R.id.edit_wh, mockedEditWh)
        mockField(R.id.view_bt_addr_on_edit, mockedTextBluetoothAddr)
        mockField(R.id.view_bt_name_on_edit, mockedTextBluetoothName)
        mockField(R.id.view_bt_name_on_edit_label, mockedTextBluetoothNameLabel)
    }

    private fun mockVoltageMax() {
        fragment.editVoltageMax = mockedEditVoltageMax
        given(mockedWidgets.getText(mockedEditVoltageMax)).willReturn("$VOLTAGE_MAX")
    }
}
