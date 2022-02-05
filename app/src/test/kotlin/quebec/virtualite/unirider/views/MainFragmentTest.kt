package quebec.virtualite.unirider.views

import android.widget.ListView
import android.widget.TextView
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.commons.android.utils.ArrayListUtils.set
import quebec.virtualite.commons.views.NavigatedTo
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity

@RunWith(MockitoJUnitRunner::class)
class MainFragmentTest :
    BaseFragmentTest(MainFragment::class.java.simpleName) {

    private val MILEAGE_A = 123
    private val MILEAGE_B = 456
    private val MILEAGE_C = 123
    private val WHEEL_A = "A"
    private val WHEEL_B = "B"
    private val WHEEL_C = "C"
    private val WHEEL_ITEM_A_123 = WheelRow(WHEEL_A, MILEAGE_A)
    private val WHEEL_ITEM_B_456 = WheelRow(WHEEL_B, MILEAGE_B)
    private val WHEEL_ITEM_C_123 = WheelRow(WHEEL_C, MILEAGE_C)

    @InjectMocks
    val fragment: MainFragment = TestableMainFragment(this)

    @Mock
    lateinit var mockedLVWheels: ListView

    @Mock
    lateinit var mockedTextMileage: TextView

    @Mock
    lateinit var mockedTextName: TextView

    @Mock
    lateinit var mockedTextTotalMileage: TextView

    @Test
    fun onCreateView() {
        // When
        fragment.onCreateView(mockedInflater, mockedContainer, SAVED_INSTANCE_STATE)

        // Then
        verifyInflate(R.layout.main_fragment)
    }

    @Test
    fun onViewCreated() {
        // Given
        given(mockedDb.getWheelList())
            .willReturn(
                listOf(
                    WheelEntity(0, WHEEL_C, MILEAGE_C, 0f, 0f),
                    WheelEntity(0, WHEEL_B, MILEAGE_B, 0f, 0f),
                    WheelEntity(0, WHEEL_A, MILEAGE_A, 0f, 0f),
                )
            )

        mockField(R.id.wheels, mockedLVWheels)
        mockField(R.id.total_mileage, mockedTextTotalMileage)

        set(fragment.wheelList, listOf(WheelRow("some previous content", 999)))

        // When
        fragment.onViewCreated(mockedView, SAVED_INSTANCE_STATE)

        // Then
        val expectedWheels = listOf(WHEEL_ITEM_B_456, WHEEL_ITEM_A_123, WHEEL_ITEM_C_123)

        verify(mockedDb).getWheelList()

        verify(mockedLVWheels).isEnabled = true
        verifyMultiFieldListAdapter(mockedLVWheels, R.layout.wheels_item, expectedWheels, "onDisplayWheel")
        verifyOnSelectItem(mockedLVWheels, "onSelectWheel")

        verify(mockedTextTotalMileage).text = (MILEAGE_A + MILEAGE_B + MILEAGE_C).toString()
    }

    @Test
    fun onDisplayItem() {
        // Given
        mockField(R.id.row_name, mockedTextName)
        mockField(R.id.row_mileage, mockedTextMileage)

        // When
        fragment.onDisplayWheel().invoke(mockedView, WHEEL_ITEM_A_123)

        // Then
        verify(mockedTextName).text = WHEEL_A
        verify(mockedTextMileage).text = MILEAGE_A.toString()
    }

    @Test
    fun onSelectWheel() {
        // Given
        fragment.wheelList += listOf(WHEEL_ITEM_A_123, WHEEL_ITEM_B_456)

        // When
        fragment.onSelectWheel().invoke(mockedView, 1)

        // Then
        assertThat(
            navigatedTo, equalTo(
                NavigatedTo(
                    R.id.action_MainFragment_to_WheelViewFragment,
                    Pair(WheelViewFragment.PARAMETER_WHEEL_NAME, WHEEL_B)
                )
            )
        )
    }

    class TestableMainFragment(val test: MainFragmentTest) : MainFragment() {

        override fun connectDb(function: () -> Unit) {
            db = test.mockedDb
            function()
        }

        override fun navigateTo(id: Int, parms: Pair<String, String>) {
            test.navigatedTo = NavigatedTo(id, parms)
        }
    }
}
