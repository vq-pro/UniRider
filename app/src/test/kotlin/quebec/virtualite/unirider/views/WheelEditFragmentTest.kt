package quebec.virtualite.unirider.views

import android.widget.Button
import android.widget.EditText
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.exceptions.WheelNotFoundException
import quebec.virtualite.unirider.views.WheelViewFragment.Companion.PARAMETER_WHEEL_ID

@RunWith(MockitoJUnitRunner::class)
class WheelEditFragmentTest :
    BaseFragmentTest(WheelEditFragment::class.java) {

    private val ID = 1111L
    private val MILEAGE = 2222
    private val NAME = "Sherman"
    private val NEW_MILEAGE = 3333
    private val NEW_NAME = "Sherman Max"
    private val NEW_VOLTAGE_MAX = 100.9f
    private val NEW_VOLTAGE_MIN = 74.5f
    private val VOLTAGE_MAX = 100.8f
    private val VOLTAGE_MIN = 75.6f

    @InjectMocks
    val fragment: WheelEditFragment = TestableWheelEditFragment(this)

    @Mock
    lateinit var mockedButtonSave: Button

    @Mock
    lateinit var mockedEditMileage: EditText

    @Mock
    lateinit var mockedEditName: EditText

    @Mock
    lateinit var mockedEditVoltageMax: EditText

    @Mock
    lateinit var mockedEditVoltageMin: EditText

    @Mock
    lateinit var mockedSaveComparator: SaveComparator

    @Before
    fun before() {
        fragment.parmWheelId = ID

        mockField(R.id.button_save, mockedButtonSave)
        mockField(R.id.edit_name, mockedEditName)
        mockField(R.id.edit_mileage, mockedEditMileage)
        mockField(R.id.edit_voltage_max, mockedEditVoltageMax)
        mockField(R.id.edit_voltage_min, mockedEditVoltageMin)
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
        val wheel = WheelEntity(ID, NAME, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)
        given(mockedDb.getWheel(ID))
            .willReturn(wheel)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedDb).getWheel(ID)

        assertThat(fragment.initialWheel, equalTo(wheel))
        assertThat(fragment.updatedWheel, equalTo(wheel))

        assertThat(fragment.editName, equalTo(mockedEditName))
        assertThat(fragment.editMileage, equalTo(mockedEditMileage))
        assertThat(fragment.editVoltageMax, equalTo(mockedEditVoltageMax))
        assertThat(fragment.editVoltageMin, equalTo(mockedEditVoltageMin))
        assertThat(fragment.buttonSave, equalTo(mockedButtonSave))

        verify(mockedEditName).setText(NAME)
        verify(mockedEditMileage).setText("$MILEAGE")
        verify(mockedEditVoltageMax).setText("$VOLTAGE_MAX")
        verify(mockedEditVoltageMin).setText("$VOLTAGE_MIN")
        verify(mockedWidgets).disable(mockedButtonSave)

        verifyOnUpdateText(mockedEditName, "onUpdateName")
        verifyOnUpdateText(mockedEditMileage, "onUpdateMileage")
        verifyOnUpdateText(mockedEditVoltageMax, "onUpdateVoltageMax")
        verifyOnUpdateText(mockedEditVoltageMin, "onUpdateVoltageMin")
        verifyOnClick(mockedButtonSave, "onSave")
    }

    @Test
    fun onViewCreated_whenWheelIsntFound() {
        // Given
        given(mockedDb.getWheel(ID))
            .willReturn(null)

        // When
        val result = { fragment.onViewCreated(mockedView, mockedBundle) }

        // Then
        assertThrows(WheelNotFoundException::class.java, result)
    }

    @Test
    fun enableSaveIfChanged_whenChanged_enabled() {
        // Given
        initForUpdates(true)

        // When
        fragment.enableSaveIfChanged()

        // Then
        verify(mockedWidgets).enable(mockedButtonSave)
    }

    @Test
    fun enableSaveIfChanged_whenNotChanged_disabled() {
        // Given
        initForUpdates(false)

        // When
        fragment.enableSaveIfChanged()

        // Then
        verify(mockedWidgets).disable(mockedButtonSave)
    }

    @Test
    fun onSave() {
        // Given
        fragment.updatedWheel = WheelEntity(ID, NAME, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)

        // When
        fragment.onSave().invoke(mockedView)

        // Then
        verify(mockedDb).saveWheel(fragment.updatedWheel)
        verifyNavigatedBack()
    }

    @Test
    fun onUpdateMileage() {
        // Given
        initForUpdates(true)

        // When
        fragment.onUpdateMileage().invoke("$NEW_MILEAGE ")

        // Then
        verify(mockedDb, never()).saveWheels(any())
        verify(mockedSaveComparator).canSave(any(), any())
        verify(mockedWidgets).enable(mockedButtonSave)

        assertThat(
            fragment.updatedWheel, equalTo(
                WheelEntity(ID, NAME, NEW_MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)
            )
        )
    }

    @Test
    fun onUpdateName() {
        // Given
        initForUpdates(true)

        // When
        fragment.onUpdateName().invoke("$NEW_NAME ")

        // Then
        verify(mockedDb, never()).saveWheels(any())
        verify(mockedSaveComparator).canSave(any(), any())
        verify(mockedWidgets).enable(mockedButtonSave)

        assertThat(
            fragment.updatedWheel, equalTo(
                WheelEntity(ID, NEW_NAME, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)
            )
        )
    }

    @Test
    fun onUpdateVoltageMax() {
        // Given
        initForUpdates(true)

        // When
        fragment.onUpdateVoltageMax().invoke("$NEW_VOLTAGE_MAX ")

        // Then
        verify(mockedDb, never()).saveWheels(any())
        verify(mockedSaveComparator).canSave(any(), any())
        verify(mockedWidgets).enable(mockedButtonSave)

        assertThat(
            fragment.updatedWheel, equalTo(
                WheelEntity(ID, NAME, MILEAGE, VOLTAGE_MIN, NEW_VOLTAGE_MAX)
            )
        )
    }

    @Test
    fun onUpdateVoltageMin() {
        // Given
        initForUpdates(true)

        // When
        fragment.onUpdateVoltageMin().invoke("$NEW_VOLTAGE_MIN ")

        // Then
        verify(mockedDb, never()).saveWheels(any())
        verify(mockedSaveComparator).canSave(any(), any())
        verify(mockedWidgets).enable(mockedButtonSave)

        assertThat(
            fragment.updatedWheel, equalTo(
                WheelEntity(ID, NAME, MILEAGE, NEW_VOLTAGE_MIN, VOLTAGE_MAX)
            )
        )
    }

    private fun enableSaveIfChanged(
        name: String, mileage: Int, voltageMin: Float, voltageMax: Float, shouldEnable: Boolean
    ) {
        // Given
        fragment.initialWheel = WheelEntity(ID, NAME, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)
        fragment.updatedWheel = WheelEntity(ID, name, mileage, voltageMin, voltageMax)

        // When
        fragment.enableSaveIfChanged()

        // Then
        if (shouldEnable)
            verify(mockedWidgets).enable(mockedButtonSave)
        else
            verify(mockedWidgets).disable(mockedButtonSave)
    }

    private fun initForUpdates(canSave: Boolean) {
        fragment.initialWheel = WheelEntity(ID, NAME, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)
        fragment.updatedWheel = fragment.initialWheel

        given(mockedSaveComparator.canSave(any(), any()))
            .willReturn(canSave)
    }

    class TestableWheelEditFragment(val test: WheelEditFragmentTest) : WheelEditFragment() {

        override fun connectDb(function: () -> Unit) {
            test.connectDb(this, function)
        }

        override fun navigateBack() {
            test.navigateBack()
        }

        override fun runDb(function: () -> Unit) {
            test.runDb(function)
        }
    }
}
