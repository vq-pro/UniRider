package quebec.virtualite.unirider.views

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
class WheelEditFragmentTest : BaseFragmentTest() {

    private val ID = 2222L
    private val MILEAGE = 1111
    private val NAME = "Sherman"
    private val NEW_NAME = "Sherman Max"
    private val VOLTAGE_MAX = 100.8f
    private val VOLTAGE_MIN = 75.6f

    @InjectMocks
    val fragment: WheelEditFragment = TestableWheelEditFragment(this)

    @Mock
    lateinit var mockedFieldMileage: EditText

    @Mock
    lateinit var mockedFieldName: EditText

    @Mock
    lateinit var mockedFieldVoltageMax: EditText

    @Mock
    lateinit var mockedFieldVoltageMin: EditText

    @Before
    fun before() {
        fragment.parmWheelName = NAME

        mockField(R.id.wheel_name_edit, mockedFieldName)
        mockField(R.id.wheel_mileage, mockedFieldMileage)
        mockField(R.id.wheel_voltage_max, mockedFieldVoltageMax)
        mockField(R.id.wheel_voltage_min, mockedFieldVoltageMin)
    }

    @Test
    fun onCreateView() {
        // Given
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

        assertThat(fragment.fieldName, equalTo(mockedFieldName))
        verify(mockedFieldName).setText(NAME)
        verifyOnUpdateText(mockedFieldName, "WheelEditFragment", "onUpdateName")

        assertThat(fragment.fieldMileage, equalTo(mockedFieldMileage))
        verify(mockedFieldMileage).setText("$MILEAGE")

        assertThat(fragment.fieldVoltageMax, equalTo(mockedFieldVoltageMax))
        verify(mockedFieldVoltageMax).setText("$VOLTAGE_MAX")

        assertThat(fragment.fieldVoltageMin, equalTo(mockedFieldVoltageMin))
        verify(mockedFieldVoltageMin).setText("$VOLTAGE_MIN")
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

//    @Test
//    fun onUpdateMileage_whenMileageIsEmpty_zero() {
//        // Given
//        fragment.wheel = WheelEntity(ID, NAME, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)
//
//        // When
//        fragment.onUpdateMileage().invoke("")
//
//        // Then
//        verify(mockedDb).saveWheels(listOf(WheelEntity(ID, NAME, ZERO_MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)))
//    }

    class TestableWheelEditFragment(val test: WheelEditFragmentTest) : WheelEditFragment() {

        override fun connectDb(function: () -> Unit) {
            db = test.mockedDb
            function()
        }

        override fun runDb(function: () -> Unit) {
            function()
        }
    }
}
