package quebec.virtualite.unirider.views

import android.widget.ListView
import android.widget.TextView
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.commons.android.utils.ArrayListUtils.setList
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.views.WheelViewFragment.Companion.PARAMETER_WHEEL_ID

@RunWith(MockitoJUnitRunner::class)
class MainFragmentTest :
    BaseFragmentTest(MainFragment::class.java) {

    private val ID_A = 111L
    private val ID_B = 222L
    private val ID_C = 333L
    private val MILEAGE_A = 123
    private val MILEAGE_B = 456
    private val MILEAGE_C = 123
    private val WHEEL_A = "A"
    private val WHEEL_B = "B"
    private val WHEEL_C = "C"
    private val WHEEL_NEW = "<New>"
    private val WHEEL_ITEM_A_123 = WheelRow(ID_A, WHEEL_A, MILEAGE_A)
    private val WHEEL_ITEM_B_456 = WheelRow(ID_B, WHEEL_B, MILEAGE_B)
    private val WHEEL_ITEM_C_123 = WheelRow(ID_C, WHEEL_C, MILEAGE_C)
    private val WHEEL_ITEM_NEW = WheelRow(0, WHEEL_NEW, 0)

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
        given(mockedDb.getWheels())
            .willReturn(
                listOf(
                    WheelEntity(ID_C, WHEEL_C, MILEAGE_C, 0f, 0f),
                    WheelEntity(ID_B, WHEEL_B, MILEAGE_B, 0f, 0f),
                    WheelEntity(ID_A, WHEEL_A, MILEAGE_A, 0f, 0f),
                )
            )

        mockField(R.id.wheels, mockedLVWheels)
        mockField(R.id.total_mileage, mockedTextTotalMileage)

        setList(fragment.wheelList, listOf(WheelRow(999, "some previous content", 999)))

        // When
        fragment.onViewCreated(mockedView, SAVED_INSTANCE_STATE)

        // Then
        val expectedEntries = listOf(
            WHEEL_ITEM_B_456,
            WHEEL_ITEM_A_123,
            WHEEL_ITEM_C_123,
            WHEEL_ITEM_NEW
        )

        verify(mockedDb).getWheels()

        verify(mockedLVWheels).isEnabled = true
        verifyMultiFieldListAdapter(mockedLVWheels, R.layout.wheels_item, expectedEntries, "onDisplayWheel")
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
    fun onDisplayItem_newItem_hideMileageZero() {
        // Given
        mockField(R.id.row_name, mockedTextName)
        mockField(R.id.row_mileage, mockedTextMileage)

        // When
        fragment.onDisplayWheel().invoke(mockedView, WHEEL_ITEM_NEW)

        // Then
        verify(mockedTextName).text = WHEEL_NEW
        verify(mockedTextMileage).text = ""
    }

    @Test
    fun onSelectWheel() {
        // Given
        fragment.wheelList += listOf(WHEEL_ITEM_A_123, WHEEL_ITEM_B_456, WHEEL_ITEM_NEW)

        // When
        fragment.onSelectWheel().invoke(mockedView, 1)

        // Then
        verifyNavigatedTo(
            R.id.action_MainFragment_to_WheelViewFragment,
            Pair(PARAMETER_WHEEL_ID, ID_B)
        )
    }

    @Test
    fun onSelectWheel_withNewEntry_straightToEditInAddMode() {
        // Given
        fragment.wheelList += listOf(WHEEL_ITEM_A_123, WHEEL_ITEM_B_456, WHEEL_ITEM_NEW)

        // When
        fragment.onSelectWheel().invoke(mockedView, 2)

        // Then
        verifyNavigatedTo(
            R.id.action_MainFragment_to_WheelEditFragment,
            Pair(PARAMETER_WHEEL_ID, 0L)
        )
    }

    class TestableMainFragment(val test: MainFragmentTest) : MainFragment() {

        override fun connectDb(function: () -> Unit) {
            test.connectDb(this, function)
        }

        override fun navigateTo(id: Int, param: Pair<String, Any>) {
            test.navigateTo(id, param)
        }
    }
}
