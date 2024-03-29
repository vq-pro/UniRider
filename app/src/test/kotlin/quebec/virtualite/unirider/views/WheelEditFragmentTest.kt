package quebec.virtualite.unirider.views

import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
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
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.TestDomain.CHARGE_RATE
import quebec.virtualite.unirider.TestDomain.CHARGE_RATE_NEW
import quebec.virtualite.unirider.TestDomain.DEVICE_ADDR
import quebec.virtualite.unirider.TestDomain.DEVICE_NAME
import quebec.virtualite.unirider.TestDomain.ID
import quebec.virtualite.unirider.TestDomain.MILEAGE
import quebec.virtualite.unirider.TestDomain.MILEAGE_NEW
import quebec.virtualite.unirider.TestDomain.NAME
import quebec.virtualite.unirider.TestDomain.NAME_NEW
import quebec.virtualite.unirider.TestDomain.NOT_SOLD
import quebec.virtualite.unirider.TestDomain.PREMILEAGE
import quebec.virtualite.unirider.TestDomain.PREMILEAGE_NEW
import quebec.virtualite.unirider.TestDomain.S18_1
import quebec.virtualite.unirider.TestDomain.SOLD
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MAX
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MAX_NEW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MIN
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MIN_NEW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_RESERVE
import quebec.virtualite.unirider.TestDomain.VOLTAGE_RESERVE_NEW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_START
import quebec.virtualite.unirider.TestDomain.WH
import quebec.virtualite.unirider.TestDomain.WH_NEW
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.views.BaseFragment.Companion.PARAMETER_WHEEL_ID

@RunWith(MockitoJUnitRunner::class)
class WheelEditFragmentTest : BaseFragmentTest(WheelEditFragment::class.java) {

    @InjectMocks
    lateinit var fragment: WheelEditFragment

    @Mock
    lateinit var mockedButtonDelete: Button

    @Mock
    lateinit var mockedButtonSave: Button

    @Mock
    lateinit var mockedCheckSold: CheckBox

    @Mock
    lateinit var mockedEditChargeRate: EditText

    @Mock
    lateinit var mockedEditMileage: EditText

    @Mock
    lateinit var mockedEditName: EditText

    @Mock
    lateinit var mockedEditPreMileage: EditText

    @Mock
    lateinit var mockedEditVoltageMax: EditText

    @Mock
    lateinit var mockedEditVoltageMin: EditText

    @Mock
    lateinit var mockedEditVoltageReserve: EditText

    @Mock
    lateinit var mockedEditWh: EditText

    @Mock
    lateinit var mockedWheelValidator: WheelValidator

    @Before
    fun before() {
        fragment.parmWheelId = ID

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
        verifyInflate(R.layout.wheel_edit_fragment)
    }

    @Test
    fun onViewCreated() {
        // Given
        val wheel = definedWheel()
        given(mockedDb.getWheel(ID))
            .willReturn(wheel)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedDb).getWheel(ID)

        assertThat(fragment.initialWheel, equalTo(wheel))
        assertThat(fragment.updatedWheel, equalTo(wheel))

        verifyFieldAssignment(R.id.button_delete, fragment.buttonDelete, mockedButtonDelete)
        verifyFieldAssignment(R.id.button_save, fragment.buttonSave, mockedButtonSave)
        verifyFieldAssignment(R.id.check_sold, fragment.checkSold, mockedCheckSold)
        verifyFieldAssignment(R.id.edit_name, fragment.editChargeRate, mockedEditChargeRate)
        verifyFieldAssignment(R.id.edit_mileage, fragment.editMileage, mockedEditMileage)
        verifyFieldAssignment(R.id.edit_name, fragment.editName, mockedEditName)
        verifyFieldAssignment(R.id.edit_premileage, fragment.editPreMileage, mockedEditPreMileage)
        verifyFieldAssignment(R.id.edit_voltage_max, fragment.editVoltageMax, mockedEditVoltageMax)
        verifyFieldAssignment(R.id.edit_voltage_min, fragment.editVoltageMin, mockedEditVoltageMin)
        verifyFieldAssignment(R.id.edit_voltage_reserve, fragment.editVoltageReserve, mockedEditVoltageReserve)
        verifyFieldAssignment(R.id.edit_wh, fragment.editWh, mockedEditWh)

        verifyOnUpdateCheckbox(mockedCheckSold, "onUpdateSold")
        verifyOnUpdateText(mockedEditChargeRate, "onUpdateChargeRate")
        verifyOnUpdateText(mockedEditName, "onUpdateName")
        verifyOnUpdateText(mockedEditPreMileage, "onUpdatePreMileage")
        verifyOnUpdateText(mockedEditMileage, "onUpdateMileage")
        verifyOnUpdateText(mockedEditVoltageMax, "onUpdateVoltageMax")
        verifyOnUpdateText(mockedEditVoltageMin, "onUpdateVoltageMin")
        verifyOnUpdateText(mockedEditVoltageReserve, "onUpdateVoltageReserve")
        verifyOnUpdateText(mockedEditWh, "onUpdateWh")
        verifyOnLongClick(mockedButtonDelete, "onDelete")
        verifyOnClick(mockedButtonSave, "onSave")

        verify(mockedCheckSold).setChecked(wheel.isSold)
        verify(mockedEditChargeRate).setText("$CHARGE_RATE")
        verify(mockedEditName).setText(NAME)
        verify(mockedEditPreMileage).setText("$PREMILEAGE")
        verify(mockedEditMileage).setText("$MILEAGE")
        verify(mockedEditVoltageMax).setText("$VOLTAGE_MAX")
        verify(mockedEditVoltageReserve).setText("$VOLTAGE_RESERVE")
        verify(mockedEditVoltageMin).setText("$VOLTAGE_MIN")
        verify(mockedEditWh).setText("$WH")
        verify(mockedWidgets).disable(mockedButtonSave)
    }

    @Test
    fun onViewCreated_withZeroPreMileageAndMileage_emptyFields() {
        // Given
        val wheel =
            WheelEntity(
                ID,
                NAME,
                DEVICE_NAME,
                DEVICE_ADDR,
                0,
                0,
                WH,
                VOLTAGE_MAX,
                VOLTAGE_MIN,
                VOLTAGE_RESERVE,
                VOLTAGE_START,
                CHARGE_RATE,
                NOT_SOLD
            )
        given(mockedDb.getWheel(ID))
            .willReturn(wheel)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedEditPreMileage, never()).setText(anyString())
        verify(mockedEditMileage, never()).setText(anyString())
    }

    @Test
    fun onViewCreated_whenAdding() {
        // Given
        fragment.parmWheelId = 0L

        val newWheel = WheelEntity(0L, "", null, null, 0, 0, 0, 0f, 0f, 0f, 0f, 0f, false)

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
    fun enableSaveIfChanged_whenChanged_enabled() {
        // Given
        initForUpdates(true)
        injectMocks()

        // When
        fragment.enableSaveIfChanged()

        // Then
        verify(mockedDb).findDuplicate(
            definedWheel()
        )
        verify(mockedWidgets).enable(mockedButtonSave)
    }

    @Test
    fun enableSaveIfChanged_whenChangedAndDuplicate_disabled() {
        // Given
        initForUpdates(true)
        injectMocks()

        given(mockedDb.findDuplicate(any()))
            .willReturn(true)

        // When
        fragment.enableSaveIfChanged()

        // Then
        verify(mockedDb).findDuplicate(
            definedWheel()
        )
        verify(mockedWidgets).disable(mockedButtonSave)
    }

    @Test
    fun enableSaveIfChanged_whenNotChanged_disabled() {
        // Given
        initForUpdates(false)
        injectMocks()

        // When
        fragment.enableSaveIfChanged()

        // Then
        verify(mockedDb, never()).findDuplicate(any())
        verify(mockedWidgets).disable(mockedButtonSave)
    }

    @Test
    fun onDelete() {
        // Given
        fragment.updatedWheel = definedWheel()

        // When
        fragment.onDelete().invoke(mockedView)

        // Then
        verify(mockedFragments).navigateTo(
            R.id.action_WheelEditFragment_to_WheelDeleteConfirmationFragment,
            Pair(PARAMETER_WHEEL_ID, ID)
        )
    }

    @Test
    fun onSave() {
        // Given
        fragment.updatedWheel = definedWheel()

        // When
        fragment.onSave().invoke(mockedView)

        // Then
        verify(mockedDb).saveWheel(fragment.updatedWheel)
        verify(mockedFragments).navigateBack()
    }

    @Test
    fun onUpdateChargeRate() {
        // Given
        initForUpdates(true)
        injectMocks()

        // When
        fragment.onUpdateChargeRate().invoke("$CHARGE_RATE_NEW ")

        // Then
        verify(mockedDb, never()).saveWheels(any())
        verify(mockedWheelValidator).canSave(any(), any())
        verify(mockedWidgets).enable(mockedButtonSave)

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(chargeRate = CHARGE_RATE_NEW)))
    }

    @Test
    fun onUpdateChargeRate_whenEmpty_zero() {
        // Given
        initForUpdates(false)
        injectMocks()

        // When
        fragment.onUpdateChargeRate().invoke(" ")

        // Then
        verify(mockedWheelValidator).canSave(any(), any())
        verify(mockedWidgets).disable(mockedButtonSave)

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(chargeRate = 0f)))
    }

    @Test
    fun onUpdateMileage() {
        // Given
        initForUpdates(true)
        injectMocks()

        // When
        fragment.onUpdateMileage().invoke("$MILEAGE_NEW ")

        // Then
        verify(mockedDb, never()).saveWheels(any())
        verify(mockedWheelValidator).canSave(any(), any())
        verify(mockedWidgets).enable(mockedButtonSave)

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(mileage = MILEAGE_NEW)))
    }

    @Test
    fun onUpdateMileage_whenEmpty_zero() {
        // Given
        initForUpdates(false)
        injectMocks()

        // When
        fragment.onUpdateMileage().invoke(" ")

        // Then
        verify(mockedWheelValidator).canSave(any(), any())
        verify(mockedWidgets).disable(mockedButtonSave)

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(mileage = 0)))
    }

    @Test
    fun onUpdatePreMileage() {
        // Given
        initForUpdates(true)
        injectMocks()

        // When
        fragment.onUpdatePreMileage().invoke("$PREMILEAGE_NEW ")

        // Then
        verify(mockedDb, never()).saveWheels(any())
        verify(mockedWheelValidator).canSave(any(), any())
        verify(mockedWidgets).enable(mockedButtonSave)

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(premileage = PREMILEAGE_NEW)))
    }

    @Test
    fun onUpdatePreMileage_whenEmpty_zero() {
        // Given
        initForUpdates(false)
        injectMocks()

        // When
        fragment.onUpdatePreMileage().invoke(" ")

        // Then
        verify(mockedWheelValidator).canSave(any(), any())
        verify(mockedWidgets).disable(mockedButtonSave)

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(premileage = 0)))
    }

    @Test
    fun onUpdateName() {
        // Given
        initForUpdates(true)
        injectMocks()

        // When
        fragment.onUpdateName().invoke("$NAME_NEW ")

        // Then
        verify(mockedDb, never()).saveWheels(any())
        verify(mockedWheelValidator).canSave(any(), any())
        verify(mockedWidgets).enable(mockedButtonSave)

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(name = NAME_NEW)))
    }

    @Test
    fun onUpdateSold() {
        // Given
        initForUpdates(true)
        injectMocks()

        // When
        fragment.onUpdateSold().invoke(SOLD)

        // Then
        verify(mockedDb, never()).saveWheels(any())
        verify(mockedWheelValidator).canSave(any(), any())
        verify(mockedWidgets).enable(mockedButtonSave)

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(isSold = SOLD)))
    }

    @Test
    fun onUpdateVoltageMax() {
        // Given
        initForUpdates(true)
        injectMocks()

        // When
        fragment.onUpdateVoltageMax().invoke("$VOLTAGE_MAX_NEW ")

        // Then
        verify(mockedDb, never()).saveWheels(any())
        verify(mockedWheelValidator).canSave(any(), any())
        verify(mockedWidgets).enable(mockedButtonSave)

        assertThat(
            fragment.updatedWheel,
            equalTo(S18_1.copy(voltageMax = VOLTAGE_MAX_NEW, voltageStart = VOLTAGE_MAX_NEW))
        )
    }

    @Test
    fun onUpdateVoltageMax_whenEmpty_zero() {
        // Given
        initForUpdates(false)
        injectMocks()

        // When
        fragment.onUpdateVoltageMax().invoke(" ")

        // Then
        verify(mockedDb, never()).saveWheels(any())
        verify(mockedWheelValidator).canSave(any(), any())
        verify(mockedWidgets).disable(mockedButtonSave)

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(voltageMax = 0f, voltageStart = 0f)))
    }

    @Test
    fun onUpdateVoltageMin() {
        // Given
        initForUpdates(true)
        injectMocks()

        // When
        fragment.onUpdateVoltageMin().invoke("$VOLTAGE_MIN_NEW ")

        // Then
        verify(mockedDb, never()).saveWheels(any())
        verify(mockedWheelValidator).canSave(any(), any())
        verify(mockedWidgets).enable(mockedButtonSave)

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(voltageMin = VOLTAGE_MIN_NEW)))
    }

    @Test
    fun onUpdateVoltageMin_whenEmpty_zero() {
        // Given
        initForUpdates(false)
        injectMocks()

        // When
        fragment.onUpdateVoltageMin().invoke(" ")

        // Then
        verify(mockedDb, never()).saveWheels(any())
        verify(mockedWheelValidator).canSave(any(), any())
        verify(mockedWidgets).disable(mockedButtonSave)

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(voltageMin = 0f)))
    }

    @Test
    fun onUpdateVoltageReserve() {
        // Given
        initForUpdates(true)
        injectMocks()

        // When
        fragment.onUpdateVoltageReserve().invoke("$VOLTAGE_RESERVE_NEW ")

        // Then
        verify(mockedDb, never()).saveWheels(any())
        verify(mockedWheelValidator).canSave(any(), any())
        verify(mockedWidgets).enable(mockedButtonSave)

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(voltageReserve = VOLTAGE_RESERVE_NEW)))
    }

    @Test
    fun onUpdateVoltageReserve_whenEmpty_setToMinimum() {
        // Given
        initForUpdates(false)
        injectMocks()

        fragment.editVoltageMin = mockedEditVoltageMin
        given(mockedWidgets.text(mockedEditVoltageMin))
            .willReturn("$VOLTAGE_MIN")

        // When
        fragment.onUpdateVoltageReserve().invoke(" ")

        // Then
        verify(mockedDb, never()).saveWheels(any())
        verify(mockedWheelValidator).canSave(any(), any())
        verify(mockedWidgets).disable(mockedButtonSave)

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(voltageReserve = VOLTAGE_MIN)))
    }

    @Test
    fun onUpdateWh() {
        // Given
        initForUpdates(true)
        injectMocks()

        // When
        fragment.onUpdateWh().invoke("$WH_NEW ")

        // Then
        verify(mockedDb, never()).saveWheels(any())
        verify(mockedWheelValidator).canSave(any(), any())
        verify(mockedWidgets).enable(mockedButtonSave)

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(wh = WH_NEW)))
    }

    @Test
    fun onUpdateWh_whenEmpty_zero() {
        // Given
        initForUpdates(false)
        injectMocks()

        // When
        fragment.onUpdateWh().invoke(" ")

        // Then
        verify(mockedDb, never()).saveWheels(any())
        verify(mockedWheelValidator).canSave(any(), any())
        verify(mockedWidgets).disable(mockedButtonSave)

        assertThat(fragment.updatedWheel, equalTo(S18_1.copy(wh = 0)))
    }

    private fun definedWheel() = WheelEntity(
        ID, NAME, DEVICE_NAME, DEVICE_ADDR,
        PREMILEAGE, MILEAGE, WH,
        VOLTAGE_MAX, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_START,
        CHARGE_RATE, NOT_SOLD
    )

    private fun initForUpdates(canSave: Boolean) {
        fragment.initialWheel = definedWheel()
        fragment.updatedWheel = fragment.initialWheel

        given(mockedWheelValidator.canSave(any(), any()))
            .willReturn(canSave)
    }

    private fun injectMocks() {
        fragment.buttonSave = mockedButtonSave
    }

    private fun mockFields() {
        mockField(R.id.button_delete, mockedButtonDelete)
        mockField(R.id.button_save, mockedButtonSave)
        mockField(R.id.check_sold, mockedCheckSold)
        mockField(R.id.edit_charge_rate, mockedEditChargeRate)
        mockField(R.id.edit_name, mockedEditName)
        mockField(R.id.edit_premileage, mockedEditPreMileage)
        mockField(R.id.edit_mileage, mockedEditMileage)
        mockField(R.id.edit_voltage_max, mockedEditVoltageMax)
        mockField(R.id.edit_voltage_min, mockedEditVoltageMin)
        mockField(R.id.edit_voltage_reserve, mockedEditVoltageReserve)
        mockField(R.id.edit_wh, mockedEditWh)
    }
}
