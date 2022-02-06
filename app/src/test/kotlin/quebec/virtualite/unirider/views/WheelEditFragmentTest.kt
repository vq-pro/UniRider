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
import quebec.virtualite.unirider.views.WheelViewFragment.Companion.PARAMETER_WHEEL_NAME

@RunWith(MockitoJUnitRunner::class)
class WheelEditFragmentTest :
    BaseFragmentTest(WheelEditFragment::class.java.simpleName) {

    private val ID = 1111L
    private val MILEAGE = 2222
    private val NAME = "Sherman"
    private val NEW_MILEAGE = 3333
    private val NEW_NAME = "Sherman Max"
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

    @Before
    fun before() {
        fragment.parmWheelName = NAME

        mockField(R.id.button_save, mockedButtonSave)
        mockField(R.id.edit_name, mockedEditName)
        mockField(R.id.edit_mileage, mockedEditMileage)
        mockField(R.id.edit_voltage_max, mockedEditVoltageMax)
        mockField(R.id.edit_voltage_min, mockedEditVoltageMin)
    }

    @Test
    fun onCreateView() {
        // Given
//        FIXME-1 Change this to ID
        mockArgument(fragment, PARAMETER_WHEEL_NAME, NAME)

        // When
        fragment.onCreateView(mockedInflater, mockedContainer, SAVED_INSTANCE_STATE)

        // Then
        verifyInflate(R.layout.wheel_edit_fragment)
    }

    @Test
    fun onViewCreated() {
        // Given
        val wheel = WheelEntity(ID, NAME, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)
        given(mockedDb.findWheel(NAME))
            .willReturn(wheel)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedDb).findWheel(NAME)

        assertThat(fragment.initialWheel, equalTo(wheel))
        assertThat(fragment.updatedWheel, equalTo(wheel))

        assertThat(fragment.editName, equalTo(mockedEditName))
        verify(mockedEditName).setText(NAME)
        verifyOnUpdateText(mockedEditName, "onUpdateName")

        assertThat(fragment.editMileage, equalTo(mockedEditMileage))
        verify(mockedEditMileage).setText("$MILEAGE")
        verifyOnUpdateText(mockedEditMileage, "onUpdateMileage")

        assertThat(fragment.editVoltageMax, equalTo(mockedEditVoltageMax))
        verify(mockedEditVoltageMax).setText("$VOLTAGE_MAX")

        assertThat(fragment.editVoltageMin, equalTo(mockedEditVoltageMin))
        verify(mockedEditVoltageMin).setText("$VOLTAGE_MIN")

        assertThat(fragment.buttonSave, equalTo(mockedButtonSave))
        verifyOnClick(mockedButtonSave, "onSave")
    }

    @Test
    fun onViewCreated_whenWheelIsntFound() {
        // Given
        given(mockedDb.findWheel(NAME))
            .willReturn(null)

        // When
        val result = { fragment.onViewCreated(mockedView, mockedBundle) }

        // Then
        assertThrows(WheelNotFoundException::class.java, result)
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
        fragment.updatedWheel = WheelEntity(ID, NAME, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)

        // When
        fragment.onUpdateMileage().invoke("$NEW_MILEAGE ")

        // Then
        verify(mockedDb, never()).saveWheels(any())

        assertThat(
            fragment.updatedWheel, equalTo(
                WheelEntity(ID, NAME, NEW_MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)
            )
        )
    }

    @Test
    fun onUpdateName() {
        // Given
        fragment.updatedWheel = WheelEntity(ID, NAME, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)

        // When
        fragment.onUpdateName().invoke("$NEW_NAME ")

        // Then
        verify(mockedDb, never()).saveWheels(any())

        assertThat(
            fragment.updatedWheel, equalTo(
                WheelEntity(ID, NEW_NAME, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)
            )
        )
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
