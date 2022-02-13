package quebec.virtualite.unirider.views

import android.widget.Button
import android.widget.TextView
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.exceptions.WheelNotFoundException

@RunWith(MockitoJUnitRunner::class)
class WheelDeleteConfirmationFragmentTest :
    BaseFragmentTest(WheelDeleteConfirmationFragment::class.java) {

    private val BT_ADDR = "AA:BB:CC:DD:EE:FF"
    private val BT_NAME = "LK2000"
    private val ID = 1111L
    private val MILEAGE = 2222
    private val NAME = "Sherman"
    private val VOLTAGE_MAX = 100.8f
    private val VOLTAGE_MIN = 75.6f

    @InjectMocks
    val fragment: WheelDeleteConfirmationFragment = TestableWheelDeleteConfirmationFragment(this)

    @Mock
    lateinit var mockedButtonDeleteConfirmation: Button

    @Mock
    lateinit var mockedButtonDeleteCancel: Button

    @Mock
    lateinit var mockedTextName: TextView

    @Before
    fun before() {
        fragment.parmWheelId = ID

        mockField(R.id.button_delete_confirmation, mockedButtonDeleteConfirmation)
        mockField(R.id.button_delete_cancel, mockedButtonDeleteCancel)
        mockField(R.id.view_name, mockedTextName)
    }

    @Test
    fun onCreateView() {
        // Given
        mockArgument(fragment, WheelViewFragment.PARAMETER_WHEEL_ID, ID)

        // When
        fragment.onCreateView(mockedInflater, mockedContainer, SAVED_INSTANCE_STATE)

        // Then
        verifyInflate(R.layout.wheel_delete_confirmation_fragment)
    }

    @Test
    fun onViewCreated() {
        // Given
        val wheel = WheelEntity(ID, NAME, BT_NAME, BT_ADDR, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)
        given(mockedDb.getWheel(ID))
            .willReturn(wheel)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedDb).getWheel(ID)

        assertThat(fragment.wheel, equalTo(wheel))

        assertThat(fragment.textName, equalTo(mockedTextName))
        assertThat(fragment.buttonDeleteConfirmation, equalTo(mockedButtonDeleteConfirmation))
        assertThat(fragment.buttonDeleteCancel, equalTo(mockedButtonDeleteCancel))

        verify(fragment.textName).setText(NAME)

        verifyOnClick(mockedButtonDeleteConfirmation, "onDelete")
        verifyOnClick(mockedButtonDeleteCancel, "onCancel")
    }

    @Test
    fun onViewCreated_whenWheelIsntFound() {
        // Given
        given(mockedDb.getWheel(ID))
            .willReturn(null)

        // When
        val result = { fragment.onViewCreated(mockedView, mockedBundle) }

        // Then
        Assert.assertThrows(WheelNotFoundException::class.java, result)
    }

    @Test
    fun onCancel() {
        // When
        fragment.onCancel().invoke(mockedView)

        // Then
        verifyNavigatedBack()
    }

    @Test
    fun onDelete() {
        // Given
        fragment.wheel = WheelEntity(ID, NAME, BT_NAME, BT_ADDR, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)

        // When
        fragment.onDelete().invoke(mockedView)

        // Then
        verify(mockedDb).deleteWheel(ID)
        verifyNavigatedBack(2)
    }

    class TestableWheelDeleteConfirmationFragment(val test: WheelDeleteConfirmationFragmentTest) :
        WheelDeleteConfirmationFragment() {

        override fun connectDb(function: () -> Unit) {
            test.connectDb(this, function)
        }

        override fun navigateBack(nb: Int) {
            test.navigateBack(nb)
        }

        override fun runDb(function: () -> Unit) {
            test.runDb(function)
        }
    }
}