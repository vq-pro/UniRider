package quebec.virtualite.unirider.views

import android.widget.Button
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
import quebec.virtualite.unirider.TestDomain.DEVICE_ADDR
import quebec.virtualite.unirider.TestDomain.DEVICE_NAME
import quebec.virtualite.unirider.TestDomain.ID
import quebec.virtualite.unirider.TestDomain.MILEAGE
import quebec.virtualite.unirider.TestDomain.MILEAGE_NEW
import quebec.virtualite.unirider.TestDomain.NAME
import quebec.virtualite.unirider.TestDomain.NAME_NEW
import quebec.virtualite.unirider.TestDomain.PREMILEAGE
import quebec.virtualite.unirider.TestDomain.PREMILEAGE_NEW
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
    lateinit var mockedSaveComparator: SaveComparator

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

        assertThat(fragment.editName, equalTo(mockedEditName))
        assertThat(fragment.editPreMileage, equalTo(mockedEditPreMileage))
        assertThat(fragment.editMileage, equalTo(mockedEditMileage))
        assertThat(fragment.editVoltageMax, equalTo(mockedEditVoltageMax))
        assertThat(fragment.editVoltageReserve, equalTo(mockedEditVoltageReserve))
        assertThat(fragment.editVoltageMin, equalTo(mockedEditVoltageMin))
        assertThat(fragment.editWh, equalTo(mockedEditWh))
        assertThat(fragment.buttonDelete, equalTo(mockedButtonDelete))
        assertThat(fragment.buttonSave, equalTo(mockedButtonSave))

        verify(mockedEditName).setText(NAME)
        verify(mockedEditPreMileage).setText("$PREMILEAGE")
        verify(mockedEditMileage).setText("$MILEAGE")
        verify(mockedEditVoltageMax).setText("$VOLTAGE_MAX")
        verify(mockedEditVoltageReserve).setText("$VOLTAGE_RESERVE")
        verify(mockedEditVoltageMin).setText("$VOLTAGE_MIN")
        verify(mockedEditWh).setText("$WH")
        verify(mockedWidgets).disable(mockedButtonSave)

        verifyOnUpdateText(mockedEditName, "onUpdateName")
        verifyOnUpdateText(mockedEditPreMileage, "onUpdatePreMileage")
        verifyOnUpdateText(mockedEditMileage, "onUpdateMileage")
        verifyOnUpdateText(mockedEditVoltageMax, "onUpdateVoltageMax")
        verifyOnUpdateText(mockedEditVoltageMin, "onUpdateVoltageMin")
        verifyOnUpdateText(mockedEditVoltageReserve, "onUpdateVoltageReserve")
        verifyOnUpdateText(mockedEditWh, "onUpdateWh")
        verifyOnLongClick(mockedButtonDelete, "onDelete")
        verifyOnClick(mockedButtonSave, "onSave")
    }

    @Test
    fun onViewCreated_withZeroPreMileageAndMileage_emptyFields() {
        // Given
        val wheel = WheelEntity(ID, NAME, DEVICE_NAME, DEVICE_ADDR, 0, 0, WH, VOLTAGE_MAX, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_START)
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

        val newWheel = WheelEntity(0L, "", null, null, 0, 0, 0, 0f, 0f, 0f, 0f)

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
    fun onUpdateMileage() {
        // Given
        initForUpdates(true)
        injectMocks()

        // When
        fragment.onUpdateMileage().invoke("$MILEAGE_NEW ")

        // Then
        verify(mockedDb, never()).saveWheels(any())
        verify(mockedSaveComparator).canSave(any(), any())
        verify(mockedWidgets).enable(mockedButtonSave)

        assertThat(
            fragment.updatedWheel,
            equalTo(
                WheelEntity(
                    ID,
                    NAME,
                    DEVICE_NAME,
                    DEVICE_ADDR,
                    PREMILEAGE,
                    MILEAGE_NEW,
                    WH,
                    VOLTAGE_MAX,
                    VOLTAGE_MIN,
                    VOLTAGE_RESERVE,
                    VOLTAGE_START
                )
            )
        )
    }

    @Test
    fun onUpdateMileage_whenEmpty_zero() {
        // Given
        initForUpdates(false)
        injectMocks()

        // When
        fragment.onUpdateMileage().invoke(" ")

        // Then
        verify(mockedSaveComparator).canSave(any(), any())
        verify(mockedWidgets).disable(mockedButtonSave)

        assertThat(
            fragment.updatedWheel,
            equalTo(WheelEntity(ID, NAME, DEVICE_NAME, DEVICE_ADDR, PREMILEAGE, 0, WH, VOLTAGE_MAX, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_START))
        )
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
        verify(mockedSaveComparator).canSave(any(), any())
        verify(mockedWidgets).enable(mockedButtonSave)

        assertThat(
            fragment.updatedWheel,
            equalTo(
                WheelEntity(
                    ID,
                    NAME,
                    DEVICE_NAME,
                    DEVICE_ADDR,
                    PREMILEAGE_NEW,
                    MILEAGE,
                    WH,
                    VOLTAGE_MAX,
                    VOLTAGE_MIN,
                    VOLTAGE_RESERVE,
                    VOLTAGE_START
                )
            )
        )
    }

    @Test
    fun onUpdatePreMileage_whenEmpty_zero() {
        // Given
        initForUpdates(false)
        injectMocks()

        // When
        fragment.onUpdatePreMileage().invoke(" ")

        // Then
        verify(mockedSaveComparator).canSave(any(), any())
        verify(mockedWidgets).disable(mockedButtonSave)

        assertThat(
            fragment.updatedWheel,
            equalTo(WheelEntity(ID, NAME, DEVICE_NAME, DEVICE_ADDR, 0, MILEAGE, WH, VOLTAGE_MAX, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_START))
        )
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
        verify(mockedSaveComparator).canSave(any(), any())
        verify(mockedWidgets).enable(mockedButtonSave)

        assertThat(
            fragment.updatedWheel,
            equalTo(
                WheelEntity(
                    ID,
                    NAME_NEW,
                    DEVICE_NAME,
                    DEVICE_ADDR,
                    PREMILEAGE,
                    MILEAGE,
                    WH,
                    VOLTAGE_MAX,
                    VOLTAGE_MIN,
                    VOLTAGE_RESERVE,
                    VOLTAGE_START
                )
            )
        )
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
        verify(mockedSaveComparator).canSave(any(), any())
        verify(mockedWidgets).enable(mockedButtonSave)

        assertThat(
            fragment.updatedWheel,
            equalTo(
                WheelEntity(
                    ID,
                    NAME,
                    DEVICE_NAME,
                    DEVICE_ADDR,
                    PREMILEAGE,
                    MILEAGE,
                    WH,
                    VOLTAGE_MAX_NEW,
                    VOLTAGE_MIN,
                    VOLTAGE_RESERVE,
                    VOLTAGE_START
                )
            )
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
        verify(mockedSaveComparator).canSave(any(), any())
        verify(mockedWidgets).disable(mockedButtonSave)

        assertThat(
            fragment.updatedWheel,
            equalTo(WheelEntity(ID, NAME, DEVICE_NAME, DEVICE_ADDR, PREMILEAGE, MILEAGE, WH, 0f, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_START))
        )
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
        verify(mockedSaveComparator).canSave(any(), any())
        verify(mockedWidgets).enable(mockedButtonSave)

        assertThat(
            fragment.updatedWheel,
            equalTo(
                WheelEntity(
                    ID,
                    NAME,
                    DEVICE_NAME,
                    DEVICE_ADDR,
                    PREMILEAGE,
                    MILEAGE,
                    WH,
                    VOLTAGE_MAX,
                    VOLTAGE_MIN_NEW,
                    VOLTAGE_RESERVE,
                    VOLTAGE_START
                )
            )
        )
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
        verify(mockedSaveComparator).canSave(any(), any())
        verify(mockedWidgets).disable(mockedButtonSave)

        assertThat(
            fragment.updatedWheel,
            equalTo(WheelEntity(ID, NAME, DEVICE_NAME, DEVICE_ADDR, PREMILEAGE, MILEAGE, WH, VOLTAGE_MAX, 0f, VOLTAGE_RESERVE, VOLTAGE_START))
        )
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
        verify(mockedSaveComparator).canSave(any(), any())
        verify(mockedWidgets).enable(mockedButtonSave)

        assertThat(
            fragment.updatedWheel,
            equalTo(
                WheelEntity(
                    ID, NAME, DEVICE_NAME, DEVICE_ADDR,
                    PREMILEAGE, MILEAGE, WH,
                    VOLTAGE_MAX, VOLTAGE_MIN, VOLTAGE_RESERVE_NEW, VOLTAGE_START
                )
            )
        )
    }

    @Test
    fun onUpdateVoltageReserve_whenEmpty_zero() {
        // Given
        initForUpdates(false)
        injectMocks()

        // When
        fragment.onUpdateVoltageReserve().invoke(" ")

        // Then
        verify(mockedDb, never()).saveWheels(any())
        verify(mockedSaveComparator).canSave(any(), any())
        verify(mockedWidgets).disable(mockedButtonSave)

        assertThat(
            fragment.updatedWheel,
            equalTo(
                WheelEntity(
                    ID, NAME, DEVICE_NAME, DEVICE_ADDR,
                    PREMILEAGE, MILEAGE, WH,
                    VOLTAGE_MAX, VOLTAGE_MIN, 0F, VOLTAGE_START
                )
            )
        )
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
        verify(mockedSaveComparator).canSave(any(), any())
        verify(mockedWidgets).enable(mockedButtonSave)

        assertThat(
            fragment.updatedWheel,
            equalTo(
                WheelEntity(
                    ID,
                    NAME,
                    DEVICE_NAME,
                    DEVICE_ADDR,
                    PREMILEAGE,
                    MILEAGE,
                    WH_NEW,
                    VOLTAGE_MAX,
                    VOLTAGE_MIN,
                    VOLTAGE_RESERVE,
                    VOLTAGE_START
                )
            )
        )
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
        verify(mockedSaveComparator).canSave(any(), any())
        verify(mockedWidgets).disable(mockedButtonSave)

        assertThat(
            fragment.updatedWheel,
            equalTo(WheelEntity(ID, NAME, DEVICE_NAME, DEVICE_ADDR, PREMILEAGE, MILEAGE, 0, VOLTAGE_MAX, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_START))
        )
    }

    private fun definedWheel() = WheelEntity(
        ID,
        NAME,
        DEVICE_NAME,
        DEVICE_ADDR,
        PREMILEAGE,
        MILEAGE,
        WH,
        VOLTAGE_MAX,
        VOLTAGE_MIN,
        VOLTAGE_RESERVE,
        VOLTAGE_START
    )

    private fun initForUpdates(canSave: Boolean) {
        fragment.initialWheel = definedWheel()
        fragment.updatedWheel = fragment.initialWheel

        given(mockedSaveComparator.canSave(any(), any()))
            .willReturn(canSave)
    }

    private fun injectMocks() {
        fragment.buttonSave = mockedButtonSave
    }

    private fun mockFields() {
        mockField(R.id.button_delete, mockedButtonDelete)
        mockField(R.id.button_save, mockedButtonSave)
        mockField(R.id.edit_name, mockedEditName)
        mockField(R.id.edit_premileage, mockedEditPreMileage)
        mockField(R.id.edit_mileage, mockedEditMileage)
        mockField(R.id.edit_voltage_max, mockedEditVoltageMax)
        mockField(R.id.edit_voltage_min, mockedEditVoltageMin)
        mockField(R.id.edit_voltage_reserve, mockedEditVoltageReserve)
        mockField(R.id.edit_wh, mockedEditWh)
    }
}
